package com.sdelcore.hermex

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.json.*
import java.io.IOException

private val ComponentActivity.settings by preferencesDataStore("settings")
private val baseUrlKey = stringPreferencesKey("base_url")
private val tokenKey = stringPreferencesKey("api_token")
private val json = Json { ignoreUnknownKeys = true }
private val client = OkHttpClient()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { App(this) } } }
    }
}

@Composable private fun App(activity: MainActivity) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "settings") {
        composable("settings") { Settings(activity) { nav.navigate("sessions") } }
        composable("sessions") { Sessions(activity) { id -> nav.navigate("chat/$id") } }
        composable("chat/{id}") { entry -> Chat(activity, entry.arguments?.getString("id").orEmpty()) }
    }
}


@Composable private fun Settings(activity: MainActivity, connected: () -> Unit) {
    val scope = rememberCoroutineScope()
    val prefs by activity.settings.data.collectAsState(initial = androidx.datastore.preferences.core.emptyPreferences())
    var url by remember(prefs) { mutableStateOf(prefs[baseUrlKey] ?: "") }
    var token by remember(prefs) { mutableStateOf(prefs[tokenKey] ?: "") }
    var status by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Connect to Hermes", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(url, { url = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(token, { token = it }, label = { Text("API token") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { scope.launch {
            activity.settings.edit { it[baseUrlKey] = url.trim().trimEnd('/'); it[tokenKey] = token }
            val target = url.trim().trimEnd('/') + "/api/status"
            val req = Request.Builder().url(target).header("Authorization", "Bearer $token").build()
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { activity.runOnUiThread { status = e.message ?: "Connection failed" } }
                override fun onResponse(call: Call, response: Response) { response.use { activity.runOnUiThread { status = if (it.isSuccessful) "Connected" else "HTTP ${it.code}" } } }
            })
        } }) { Text("Test connection") }
        Text(status)
        Button(onClick = { scope.launch { activity.settings.edit { it[baseUrlKey] = url.trim().trimEnd('/'); it[tokenKey] = token }; connected() } }) { Text("Open sessions") }
    }
}

@Composable private fun Sessions(activity: MainActivity, open: (String) -> Unit) {
    val prefs by activity.settings.data.collectAsState(initial = androidx.datastore.preferences.core.emptyPreferences())
    var rows by remember { mutableStateOf(emptyList<JsonObject>()) }
    var error by remember { mutableStateOf("") }
    LaunchedEffect(prefs) {
        val base = prefs[baseUrlKey].orEmpty(); val token = prefs[tokenKey].orEmpty()
        if (base.isNotBlank()) client.newCall(Request.Builder().url("$base/api/sessions").header("Authorization", "Bearer $token").build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { error = e.message.orEmpty() }
            override fun onResponse(call: Call, response: Response) { response.use { val data = it.body?.string().orEmpty(); if (it.isSuccessful) rows = (json.parseToJsonElement(data).jsonObject["sessions"]?.jsonArray ?: JsonArray(emptyList())).mapNotNull { item -> item as? JsonObject } else error = "HTTP ${it.code}" } }
        })
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sessions", style = MaterialTheme.typography.headlineSmall); Text(error)
        LazyColumn { lazyItems(rows) { row -> val id = row["session_id"]?.jsonPrimitive?.contentOrNull ?: row["id"]?.jsonPrimitive?.contentOrNull.orEmpty(); TextButton(onClick = { open(id) }) { Text(row["title"]?.jsonPrimitive?.contentOrNull ?: id) } } }
    }
}

@Composable private fun Chat(activity: MainActivity, id: String) {
    val prefs by activity.settings.data.collectAsState(initial = androidx.datastore.preferences.core.emptyPreferences())
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }; var streamId by remember { mutableStateOf("") }; var reply by remember { mutableStateOf("") }; var busy by remember { mutableStateOf(false) }; var error by remember { mutableStateOf("") }
    val base = prefs[baseUrlKey].orEmpty(); val token = prefs[tokenKey].orEmpty()
    fun send() {
        if (message.isBlank()) return
        val text = message; message = ""; reply = ""; busy = true
        val payload = buildJsonObject { put("session_id", id); put("message", text); put("workspace", JsonNull); put("model", JsonNull) }.toString()
        client.newCall(Request.Builder().url("$base/api/chat/start").header("Authorization", "Bearer $token").post(payload.toRequestBody("application/json".toMediaType())).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { activity.runOnUiThread { error = e.message.orEmpty(); busy = false } }
            override fun onResponse(call: Call, response: Response) { response.use {
                val data = it.body?.string().orEmpty()
                if (!it.isSuccessful) { activity.runOnUiThread { error = "HTTP ${it.code}"; busy = false }; return }
                val sid = runCatching { json.parseToJsonElement(data).jsonObject["stream_id"]?.jsonPrimitive?.content }.getOrNull().orEmpty()
                activity.runOnUiThread { streamId = sid }
                val req = Request.Builder().url("$base/api/chat/stream?stream_id=$sid").header("Authorization", "Bearer $token").header("Accept", "text/event-stream").build()
                client.newCall(req).execute().use { stream ->
                    val source = stream.body?.source() ?: return@use
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: break
                        if (line.startsWith("data:")) {
                            val event = runCatching { json.parseToJsonElement(line.removePrefix("data:").trim()).jsonObject }.getOrNull() ?: continue
                            val delta = event["content"]?.jsonPrimitive?.contentOrNull ?: event["delta"]?.jsonPrimitive?.contentOrNull
                            if (delta != null) activity.runOnUiThread { reply += delta }
                        }
                    }
                    activity.runOnUiThread { busy = false; streamId = "" }
                }
            } }
        })
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Chat", style = MaterialTheme.typography.headlineSmall); Text(id); Text(reply); Text(error)
        Row { OutlinedTextField(message, { message = it }, modifier = Modifier.weight(1f), label = { Text("Message") }); Button(onClick = { send() }) { Text("Send") } }
        if (busy) Button(onClick = { val sid = streamId; client.newCall(Request.Builder().url("$base/api/chat/cancel?stream_id=$sid").header("Authorization", "Bearer $token").get().build()).enqueue(object : Callback { override fun onFailure(call: Call, e: IOException) {}; override fun onResponse(call: Call, response: Response) { response.close(); activity.runOnUiThread { busy = false } } }) }) { Text("Stop") }
    }
}

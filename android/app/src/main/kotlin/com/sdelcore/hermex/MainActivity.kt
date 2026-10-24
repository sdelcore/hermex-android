package com.sdelcore.hermex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

private val ComponentActivity.settings by preferencesDataStore("settings")
private val baseUrlKey = stringPreferencesKey("base_url")
private val tokenKey = stringPreferencesKey("api_token")
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
        composable("sessions") { Sessions(activity) { nav.navigate("chat/${it}") } }
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
            val base = url.trim().trimEnd('/')
            if (openSavedHermesServer(prefs[baseUrlKey].orEmpty(), base)) {
                connected()
                return@launch
            }
            activity.settings.edit { it[baseUrlKey] = base; it[tokenKey] = token }
            client.newCall(HermesProtocol.healthRequest(base, token)).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { activity.runOnUiThread { status = e.message ?: "Connection failed" } }
                override fun onResponse(call: Call, response: Response) { response.use { activity.runOnUiThread { status = if (it.isSuccessful) "Connected" else "HTTP ${it.code}" } } }
            })
        } }) { Text("Test connection") }
        Text(status)
        Button(onClick = { scope.launch {
            val base = url.trim().trimEnd('/')
            if (!openSavedHermesServer(prefs[baseUrlKey].orEmpty(), base)) {
                activity.settings.edit { it[baseUrlKey] = base; it[tokenKey] = token }
            }
            connected()
        } }) { Text("Open sessions") }
    }
}

internal fun openSavedHermesServer(savedAddress: String, address: String): Boolean =
    address.isNotBlank() && savedAddress.trimEnd('/') == address

@Composable private fun Sessions(activity: MainActivity, open: (String) -> Unit) {
    val prefs by activity.settings.data.collectAsState(initial = androidx.datastore.preferences.core.emptyPreferences())
    var rows by remember { mutableStateOf(emptyList<HermesProtocol.SessionRow>()) }
    var error by remember { mutableStateOf("") }
    LaunchedEffect(prefs) {
        val base = prefs[baseUrlKey].orEmpty(); val token = prefs[tokenKey].orEmpty()
        if (base.isNotBlank()) client.newCall(HermesProtocol.sessionsRequest(base, token)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { activity.runOnUiThread { error = e.message.orEmpty() } }
            override fun onResponse(call: Call, response: Response) { response.use { val data = it.body?.string().orEmpty(); activity.runOnUiThread { if (it.isSuccessful) rows = HermesProtocol.parseSessions(data) else error = "HTTP ${it.code}" } } }
        })
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sessions", style = MaterialTheme.typography.headlineSmall); Text(error)
        LazyColumn { items(rows) { row -> row.sessionId?.let { id -> TextButton(onClick = { open(id) }) { Text(row.title ?: id) } } } }
    }
}

@Composable private fun Chat(activity: MainActivity, id: String) {
    val prefs by activity.settings.data.collectAsState(initial = androidx.datastore.preferences.core.emptyPreferences())
    var message by remember { mutableStateOf("") }
    var streamId by remember { mutableStateOf("") }
    var reply by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val base = prefs[baseUrlKey].orEmpty(); val token = prefs[tokenKey].orEmpty()
    fun send() {
        if (message.isBlank()) return
        val text = message; message = ""; reply = ""; busy = true; error = ""
        client.newCall(HermesProtocol.chatStartRequest(base, token, id, text)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { activity.runOnUiThread { error = e.message.orEmpty(); busy = false } }
            override fun onResponse(call: Call, response: Response) { response.use {
                if (!it.isSuccessful) { activity.runOnUiThread { error = "HTTP ${it.code}"; busy = false }; return }
                val start = runCatching { HermesProtocol.parseChatStart(it.body?.string().orEmpty()) }.getOrElse { failure -> activity.runOnUiThread { error = failure.message.orEmpty(); busy = false }; return }
                val sid = start.streamId
                if (sid.isNullOrBlank()) { activity.runOnUiThread { error = start.error ?: "Missing stream ID"; busy = false }; return }
                activity.runOnUiThread { streamId = sid }
                client.newCall(HermesProtocol.streamRequest(base, token, sid)).execute().use { stream ->
                    val source = stream.body?.source()
                    if (source == null) { activity.runOnUiThread { error = "Empty stream"; busy = false }; return@use }
                    val parser = HermesProtocol.SseParser()
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: break
                        when (val frame = parser.feed(line)) {
                            is HermesProtocol.StreamFrame.Token -> activity.runOnUiThread { reply += frame.text }
                            is HermesProtocol.StreamFrame.Error -> activity.runOnUiThread { error = frame.message ?: "Stream error" }
                            HermesProtocol.StreamFrame.StreamEnd, HermesProtocol.StreamFrame.Cancelled -> break
                            null -> Unit
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
        if (busy) Button(onClick = { val sid = streamId; client.newCall(HermesProtocol.cancelRequest(base, token, sid)).enqueue(object : Callback { override fun onFailure(call: Call, e: IOException) {}; override fun onResponse(call: Call, response: Response) { response.close(); activity.runOnUiThread { busy = false } } }) }) { Text("Stop") }
    }
}

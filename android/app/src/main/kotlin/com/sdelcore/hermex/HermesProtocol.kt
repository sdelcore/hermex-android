package com.sdelcore.hermex

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HermesProtocol {
    private val json = Json { ignoreUnknownKeys = true }
    data class ChatStart(val streamId: String?, val sessionId: String?, val error: String?)
    data class SessionRow(val sessionId: String?, val title: String?)

    sealed interface StreamFrame {
        data class Token(val text: String) : StreamFrame
        data object StreamEnd : StreamFrame
        data object Cancelled : StreamFrame
        data class Error(val message: String?) : StreamFrame
    }

    fun healthRequest(baseUrl: String, token: String) = get("$baseUrl/health", token)
    fun sessionsRequest(baseUrl: String, token: String) = get("$baseUrl/api/sessions", token)

    fun chatStartRequest(baseUrl: String, token: String, sessionId: String, message: String): Request {
        val body = """{"session_id":${json.encodeToString(sessionId)},"message":${json.encodeToString(message)},"workspace":null,"model":null}"""
        return Request.Builder().url("$baseUrl/api/chat/start").header("Authorization", "Bearer $token")
            .post(body.toRequestBody("application/json".toMediaType())).build()
    }

    fun streamRequest(baseUrl: String, token: String, streamId: String): Request =
        get("$baseUrl/api/chat/stream?stream_id=${encode(streamId)}", token)
            .newBuilder().header("Accept", "text/event-stream").build()

    fun cancelRequest(baseUrl: String, token: String, streamId: String): Request =
        get("$baseUrl/api/chat/cancel?stream_id=${encode(streamId)}", token)

    fun parseChatStart(source: String): ChatStart {
        val obj = json.parseToJsonElement(source).jsonObject
        return ChatStart(obj.string("streamId"), obj.string("sessionId"), obj.string("error"))
    }

    fun parseSessions(source: String): List<SessionRow> =
        json.parseToJsonElement(source).jsonObject["sessions"]?.jsonArray.orEmpty().mapNotNull { element ->
            val row = element as? JsonObject ?: return@mapNotNull null
            SessionRow(row.string("sessionId"), row.string("title"))
        }

    class SseParser {
        private var event = "message"
        private val data = StringBuilder()

        fun feed(line: String): StreamFrame? {
            if (line.isEmpty()) return dispatch().also { event = "message"; data.clear() }
            if (line.startsWith(':')) return null
            val colon = line.indexOf(':')
            val field = if (colon < 0) line else line.substring(0, colon)
            val value = if (colon < 0) "" else line.substring(colon + 1).removePrefix(" ")
            when (field) {
                "event" -> event = value
                "data" -> { if (data.isNotEmpty()) data.append('\n'); data.append(value) }
            }
            return null
        }

        private fun dispatch(): StreamFrame? {
            if (data.isEmpty()) return null
            val payload = runCatching { json.parseToJsonElement(data.toString()).jsonObject }.getOrNull() ?: JsonObject(emptyMap())
            return when (event) {
                "token" -> StreamFrame.Token(payload.string("text").orEmpty())
                "stream_end" -> StreamFrame.StreamEnd
                "cancel" -> StreamFrame.Cancelled
                "error", "apperror" -> StreamFrame.Error(payload.string("error") ?: payload.string("message"))
                else -> null
            }
        }
    }

    private fun JsonObject.string(key: String) = this[key]?.jsonPrimitive?.contentOrNull
    private fun get(url: String, token: String) = Request.Builder().url(url).header("Authorization", "Bearer $token").get().build()
    private fun encode(value: String) = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
}

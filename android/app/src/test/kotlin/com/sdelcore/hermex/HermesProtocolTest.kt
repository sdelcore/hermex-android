package com.sdelcore.hermex

import kotlin.test.Test
import kotlin.test.assertEquals

class HermesProtocolTest {

    @Test fun savedServerShortcutRequiresAnExactNonblankAddress() {
        assertEquals(true, openSavedHermesServer("https://hermes.example", "https://hermes.example"))
        assertEquals(true, openSavedHermesServer("https://hermes.example/", "https://hermes.example"))
        assertEquals(false, openSavedHermesServer("", "https://hermes.example"))
        assertEquals(false, openSavedHermesServer("https://hermes.example", "https://other.example"))
        assertEquals(false, openSavedHermesServer("https://hermes.example", ""))
    }
    @Test fun parsesSnakeCaseChatStartResponse() {
        assertEquals("str-1", HermesProtocol.parseChatStart("""{"stream_id":"str-1"}""").streamId)
    }

    @Test fun parsesCamelCaseChatStartResponse() {
        assertEquals("str-1", HermesProtocol.parseChatStart("""{"streamId":"str-1"}""").streamId)
    }

    @Test fun parsesSnakeCaseSessionRows() {
        val rows = HermesProtocol.parseSessions("""{"sessions":[{"session_id":"ses-1","title":"First"}]}""")
        assertEquals(listOf(HermesProtocol.SessionRow("ses-1", "First")), rows)
    }

    @Test fun parsesCamelCaseSessionRows() {
        val rows = HermesProtocol.parseSessions("""{"sessions":[{"sessionId":"ses-1","title":"First"}]}""")
        assertEquals(listOf(HermesProtocol.SessionRow("ses-1", "First")), rows)
    }

    @Test fun incrementallyParsesTypedSseFrames() {
        val parser = HermesProtocol.SseParser()
        val lines = listOf("event: token", "data: {\"text\":\"Hel\"}", "", "event: token", "data: {\"text\":\"lo\"}", "", "event: stream_end", "data: {}", "")
        val frames = lines.mapNotNull(parser::feed)
        assertEquals("Hello", frames.filterIsInstance<HermesProtocol.StreamFrame.Token>().joinToString("") { it.text })
        assertEquals(HermesProtocol.StreamFrame.StreamEnd, frames.last())
    }

    @Test fun buildsSwiftEndpointsAndRequestFields() {
        val start = HermesProtocol.chatStartRequest("https://example.test", "token", "session-1", "hello")
        val buffer = okio.Buffer().also(start.body!!::writeTo)
        assertEquals("https://example.test/api/chat/start", start.url.toString())
        assertEquals("{\"session_id\":\"session-1\",\"message\":\"hello\",\"workspace\":null,\"model\":null}", buffer.readUtf8())
        assertEquals("https://example.test/health", HermesProtocol.healthRequest("https://example.test", "token").url.toString())
        assertEquals("https://example.test/api/chat/stream?stream_id=stream-1", HermesProtocol.streamRequest("https://example.test", "token", "stream-1").url.toString())
        assertEquals("https://example.test/api/chat/cancel?stream_id=stream-1", HermesProtocol.cancelRequest("https://example.test", "token", "stream-1").url.toString())
    }
}

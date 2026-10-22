package com.sdelcore.hermex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HermesProtocolTest {
    @Test fun parsesSwiftChatStartResponse() {
        val start = HermesProtocol.parseChatStart("""{"streamId":"str-1","sessionId":"ses-1"}""")
        assertEquals("str-1", start.streamId)
        assertEquals("ses-1", start.sessionId)
    }

    @Test fun parsesSwiftSessionRows() {
        val rows = HermesProtocol.parseSessions("""{"sessions":[{"sessionId":"ses-1","title":"First"}]}""")
        assertEquals(listOf(HermesProtocol.SessionRow("ses-1", "First")), rows)
    }

    @Test fun rejectsSnakeCaseSessionId() {
        val row = HermesProtocol.parseSessions("""{"sessions":[{"session_id":"ses-1","title":"First"}]}""").single()
        assertNull(row.sessionId)
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

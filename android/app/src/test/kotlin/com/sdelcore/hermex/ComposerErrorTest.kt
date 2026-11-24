package com.sdelcore.hermex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComposerErrorTest {

    @Test fun aFailedSendIsShown() {
        assertTrue(ComposerError("HTTP 500").isShown)
        assertFalse(ComposerError().isShown)
    }

    @Test fun editingTheDraftClearsAShownError() {
        assertEquals(ComposerError(), ComposerError("Empty stream").afterEdit())
    }

    @Test fun editingADraftWithNoErrorKeepsTheComposerQuiet() {
        assertEquals(ComposerError(), ComposerError().afterEdit())
    }
}

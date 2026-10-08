package com.ezzy.ccp.countrypicker.ui

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** The phone field's ghost digits: what is left of the example number, aligned to what was typed. */
class GhostDigitsTest {

    private val ghost = Color.Gray

    @Test
    fun `the rest of the example continues its grouping as zeros`() {
        val hint = ghostDigits(typed = "712 3", example = "712 123456", ghost = ghost)
        assertEquals("712 300000", hint?.text)
    }

    @Test
    fun `the typed part is transparent so only the editor draws it`() {
        val hint = ghostDigits(typed = "712 3", example = "712 123456", ghost = ghost)!!
        val typed = hint.spanStyles.first { it.start == 0 }
        assertEquals(Color.Transparent, typed.item.color)
        assertEquals(5, typed.end)
        assertEquals(ghost, hint.spanStyles.first { it.start == 5 }.item.color)
    }

    @Test
    fun `alignment is by digit — not by character`() {
        // Typed without the group separator the example has: the ghost still starts after the
        // fourth digit, not after the fourth character.
        assertEquals("7123" + "00000", ghostDigits(typed = "7123", example = "712 123456", ghost = ghost)?.text)
    }

    @Test
    fun `a complete number has no ghost`() {
        assertNull(ghostDigits(typed = "712 123456", example = "712 123456", ghost = ghost))
    }

    @Test
    fun `a number longer than the example has no ghost`() {
        assertNull(ghostDigits(typed = "7121234567", example = "712 123456", ghost = ghost))
    }
}

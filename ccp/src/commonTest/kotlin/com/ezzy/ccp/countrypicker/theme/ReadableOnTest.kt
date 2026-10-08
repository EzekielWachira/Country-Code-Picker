package com.ezzy.ccp.countrypicker.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

/** Content colors on an accent: white where it reads, black on pale accents. */
class ReadableOnTest {

    @Test
    fun `saturated accents carry white`() {
        assertEquals(Color.White, readableOn(Color(0xFF6650A4))) // Material baseline primary
        assertEquals(Color.White, readableOn(Color(0xFF007AFF))) // iOS system blue
        assertEquals(Color.White, readableOn(Color(0xFF0F766E))) // teal
    }

    @Test
    fun `pale accents carry black`() {
        // The Material baseline dark-theme primary: white on it is barely 1.7:1.
        assertEquals(Color.Black, readableOn(Color(0xFFD0BCFF)))
        assertEquals(Color.Black, readableOn(Color(0xFFFFD54F)))
    }
}

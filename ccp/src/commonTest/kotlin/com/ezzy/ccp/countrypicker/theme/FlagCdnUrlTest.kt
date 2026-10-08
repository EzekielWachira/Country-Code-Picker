package com.ezzy.ccp.countrypicker.theme

import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.ui.originalArtSize
import kotlin.test.Test
import kotlin.test.assertEquals

/** flagcdn addresses for each shape and format, and how flat artwork is fitted to its slot. */
class FlagCdnUrlTest {

    private fun url(shape: FlagImageShape, format: FlagImageFormat = FlagImageFormat.Png, width: Int = 96, height: Int = 72) =
        CountryFlagSource.FlagCdn(shape = shape, format = format).urlFor("KE", width, height)

    @Test
    fun `waving flags use the smallest width by height that covers the request`() {
        assertEquals("https://flagcdn.com/96x72/ke.png", url(FlagImageShape.Waving))
        assertEquals("https://flagcdn.com/108x81/ke.png", url(FlagImageShape.Waving, width = 97, height = 72))
    }

    @Test
    fun `original shapes use a common width or a common height`() {
        assertEquals("https://flagcdn.com/w160/ke.png", url(FlagImageShape.OriginalSameWidth))
        assertEquals("https://flagcdn.com/h80/ke.png", url(FlagImageShape.OriginalSameHeight))
    }

    @Test
    fun `requests beyond the largest size use the largest`() {
        assertEquals("https://flagcdn.com/256x192/ke.png", url(FlagImageShape.Waving, width = 4000, height = 3000))
        assertEquals("https://flagcdn.com/w2560/ke.png", url(FlagImageShape.OriginalSameWidth, width = 4000))
        assertEquals("https://flagcdn.com/h240/ke.png", url(FlagImageShape.OriginalSameHeight, height = 4000))
    }

    @Test
    fun `every format for the original shapes`() {
        assertEquals("https://flagcdn.com/w160/ke.webp", url(FlagImageShape.OriginalSameWidth, FlagImageFormat.WebP))
        assertEquals("https://flagcdn.com/h80/ke.jpg", url(FlagImageShape.OriginalSameHeight, FlagImageFormat.Jpeg))
        assertEquals("https://flagcdn.com/ke.svg", url(FlagImageShape.OriginalSameWidth, FlagImageFormat.Svg))
    }

    @Test
    fun `waving flags fall back to png for formats the cdn does not serve`() {
        assertEquals("https://flagcdn.com/96x72/ke.webp", url(FlagImageShape.Waving, FlagImageFormat.WebP))
        assertEquals("https://flagcdn.com/96x72/ke.png", url(FlagImageShape.Waving, FlagImageFormat.Jpeg))
        assertEquals("https://flagcdn.com/96x72/ke.png", url(FlagImageShape.Waving, FlagImageFormat.Svg))
    }

    @Test
    fun `a custom base url is used as given — with or without a trailing slash`() {
        val mirror = CountryFlagSource.FlagCdn(shape = FlagImageShape.OriginalSameHeight, baseUrl = "https://cdn.example.com/flags/")
        assertEquals("https://cdn.example.com/flags/h40/gb-eng.png", mirror.urlFor("GB-ENG", 40, 40))
    }

    @Test
    fun `same width fills the slot width at the flag's own height`() {
        assertEquals(48.dp to 32.dp, originalArtSize(FlagImageShape.OriginalSameWidth, 1.5f, 48.dp, 36.dp))
    }

    @Test
    fun `same height is half the slot width so every common proportion shares one height`() {
        // 3:2 (Kenya) and 2:1 (United Kingdom) flags come out the same height, the 2:1 one exactly
        // as wide as the slot.
        assertEquals(36.dp to 24.dp, originalArtSize(FlagImageShape.OriginalSameHeight, 1.5f, 48.dp, 36.dp))
        assertEquals(48.dp to 24.dp, originalArtSize(FlagImageShape.OriginalSameHeight, 2f, 48.dp, 36.dp))
    }

    @Test
    fun `no flag is ever wider than its slot`() {
        // Qatar (28:11) at the same height would be 61dp wide in a 48dp slot; it is scaled down
        // whole to the slot's width instead of spilling past it.
        val (qatarWidth, qatarHeight) = originalArtSize(FlagImageShape.OriginalSameHeight, 28f / 11f, 48.dp, 36.dp)
        assertEquals(48f, qatarWidth.value, 0.01f)
        assertEquals(48f * 11f / 28f, qatarHeight.value, 0.01f)
    }

    @Test
    fun `a very tall flag at the same width is capped at a bounded overhang`() {
        // Nepal at the same width would be 58dp tall in a 36dp slot; it stops at 1.3× the slot.
        val (_, nepalHeight) = originalArtSize(FlagImageShape.OriginalSameWidth, 0.82f, 48.dp, 36.dp)
        assertEquals(46.8f, nepalHeight.value, 0.01f)
    }
}

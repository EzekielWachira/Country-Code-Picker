package com.ezzy.ccp.screenshot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.request.ImageResult
import coil3.request.SuccessResult
import kotlin.math.roundToInt

/**
 * Image loaders for the screenshot suite, so no golden ever depends on the network.
 *
 * [offline] fails every request, which is what a device without connectivity sees: every flag shows
 * its emoji fallback. [fake] answers flagcdn addresses with striped stand-ins drawn at each flag's
 * real proportions (4:3 for the waving shape), so the geometry of the three shapes is what the
 * goldens record.
 */
internal object FakeFlagImages {

    fun offline(context: Context): ImageLoader = ImageLoader.Builder(context)
        .memoryCache(null)
        .diskCache(null)
        .components { add(Interceptor { chain -> ErrorResult(null, chain.request, IllegalStateException("offline")) }) }
        .build()

    fun fake(context: Context): ImageLoader = ImageLoader.Builder(context)
        .memoryCache(null)
        .diskCache(null)
        .components { add(Interceptor { chain -> SuccessResult(flag(chain.request.data as String).asImage(), chain.request, DataSource.MEMORY) }) }
        .build()

    /** A three-stripe stand-in for the flag at [url], at its true proportions. */
    private fun flag(url: String): Bitmap {
        val code = url.substringAfterLast('/').substringBefore('.')
        val waving = Regex("/\\d+x\\d+/").containsMatchIn(url)
        val (aspect, stripes) = FLAGS[code] ?: (1.5f to listOf(0xFF9CA3AF.toInt(), 0xFFE5E7EB.toInt(), 0xFF9CA3AF.toInt()))
        val height = 120
        val width = (height * if (waving) 4f / 3f else aspect).roundToInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        stripes.forEachIndexed { index, color ->
            paint.color = color
            canvas.drawRect(0f, height * index / 3f, width.toFloat(), height * (index + 1) / 3f, paint)
        }
        return bitmap
    }

    /** Width ÷ height and three stripe colors, for flags with telling proportions. */
    private val FLAGS: Map<String, Pair<Float, List<Int>>> = mapOf(
        "ke" to (1.5f to listOf(0xFF000000.toInt(), 0xFFBB0000.toInt(), 0xFF006600.toInt())),
        "de" to (5f / 3f to listOf(0xFF000000.toInt(), 0xFFDD0000.toInt(), 0xFFFFCE00.toInt())),
        "ch" to (1f to listOf(0xFFDA291C.toInt(), 0xFFDA291C.toInt(), 0xFFDA291C.toInt())),
        "np" to (0.82f to listOf(0xFFDC143C.toInt(), 0xFFDC143C.toInt(), 0xFF003893.toInt())),
        "qa" to (28f / 11f to listOf(0xFF8A1538.toInt(), 0xFF8A1538.toInt(), 0xFF8A1538.toInt())),
        "jp" to (1.5f to listOf(0xFFFFFFFF.toInt(), 0xFFBC002D.toInt(), 0xFFFFFFFF.toInt())),
        "gb" to (2f to listOf(0xFF012169.toInt(), 0xFFC8102E.toInt(), 0xFF012169.toInt())),
        "us" to (1.9f to listOf(0xFFB22234.toInt(), 0xFFFFFFFF.toInt(), 0xFF3C3B6E.toInt())),
        "ng" to (2f to listOf(0xFF008751.toInt(), 0xFFFFFFFF.toInt(), 0xFF008751.toInt())),
    )
}

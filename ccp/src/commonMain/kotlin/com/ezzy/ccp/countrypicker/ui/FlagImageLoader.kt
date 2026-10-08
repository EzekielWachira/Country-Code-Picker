/**
 * Copyright (c) 2025 Ezekiel Wachira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */package com.ezzy.ccp.countrypicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder
import okio.Path

/**
 * Overrides the loader flag images are fetched with. `null` — the default — uses the library's own;
 * tests provide a fake one so screenshots never depend on the network.
 */
internal val LocalFlagImageLoader: ProvidableCompositionLocal<ImageLoader?> = staticCompositionLocalOf { null }

/** The loader for flag images: [LocalFlagImageLoader] when set, otherwise the shared one. */
@Composable
internal fun rememberFlagImageLoader(): ImageLoader {
    LocalFlagImageLoader.current?.let { return it }
    val context = LocalPlatformContext.current
    return remember(context) { FlagImageLoader.get(context) }
}

/**
 * One loader per process, so every picker in the app shares one memory and one disk cache and each
 * flag downloads once.
 *
 * The library's own rather than the host's singleton: flags then work whether or not the host uses
 * Coil, and need no SVG decoder or cache settings from it — and nothing here changes the host's.
 */
internal object FlagImageLoader {
    // Composition is single-threaded, and so is every call here.
    private var instance: ImageLoader? = null

    fun get(context: PlatformContext): ImageLoader = instance ?: build(context).also { instance = it }

    private fun build(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(KtorNetworkFetcherFactory())
            add(SvgDecoder.Factory())
        }
        .memoryCache { MemoryCache.Builder().maxSizePercent(context, MEMORY_CACHE_FRACTION).build() }
        .diskCache { DiskCache.Builder().directory(flagCacheDirectory(context)).maxSizeBytes(DISK_CACHE_BYTES).build() }
        .build()
}

/** The platform's cache directory for flag images — evictable by the OS, never backed up. */
internal expect fun flagCacheDirectory(context: PlatformContext): Path

/** Every flag in the dataset at list size is a few megabytes; this leaves room for larger renders. */
private const val MEMORY_CACHE_FRACTION = 0.05
private const val DISK_CACHE_BYTES = 24L * 1024 * 1024

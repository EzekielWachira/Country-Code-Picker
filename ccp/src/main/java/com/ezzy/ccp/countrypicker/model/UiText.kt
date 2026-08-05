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
 */

package com.ezzy.ccp.countrypicker.model

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * A string that has not been resolved yet — either a literal the host supplied, or a resource the
 * library will resolve against the current configuration.
 *
 * Every user-facing string in the library flows through this type. That keeps the components free
 * of hardcoded English while still letting a host override any title, subtitle or message with a
 * plain [String] at the call site:
 *
 * ```kotlin
 * CountrySelector(
 *     label = UiText.of("Nationality"),                    // literal
 *     placeholder = UiText.resource(R.string.pick_market), // host resource
 * )
 * ```
 */
@Immutable
sealed interface UiText {

    /** A literal the caller already resolved. */
    @Immutable
    data class Literal(val value: String) : UiText

    /** A string resource, optionally with format arguments. */
    @Immutable
    data class Resource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    /** A plurals resource selected by [count], which is also passed as the sole format argument. */
    @Immutable
    data class Plural(
        @PluralsRes val id: Int,
        val count: Int,
    ) : UiText

    companion object {
        /** Wraps a literal string. */
        fun of(value: String): UiText = Literal(value)

        /** References a string resource. */
        fun resource(@StringRes id: Int, vararg args: Any): UiText = Resource(id, args.toList())

        /** References a plurals resource. */
        fun plural(@PluralsRes id: Int, count: Int): UiText = Plural(id, count)
    }
}

/**
 * Resolves this [UiText] against the current composition's resources.
 *
 * Prefer this over `LocalContext.current.getString(...)` in composables: it reads the same
 * configuration Compose uses, so it recomposes correctly on locale change.
 */
@Composable
@ReadOnlyComposable
fun UiText.resolve(): String = when (this) {
    is UiText.Literal -> value
    is UiText.Resource ->
        if (args.isEmpty()) stringResource(id) else stringResource(id, *args.toTypedArray())
    is UiText.Plural -> LocalContext.current.resources.getQuantityString(id, count, count)
}

/** Resolves this [UiText] outside composition, for content descriptions built in plain code. */
fun UiText.resolve(context: android.content.Context): String = when (this) {
    is UiText.Literal -> value
    is UiText.Resource ->
        if (args.isEmpty()) context.getString(id) else context.getString(id, *args.toTypedArray())
    is UiText.Plural -> context.resources.getQuantityString(id, count, count)
}

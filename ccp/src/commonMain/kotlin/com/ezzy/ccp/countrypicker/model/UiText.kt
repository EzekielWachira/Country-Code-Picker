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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

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
 *     label = UiText.of("Nationality"),                      // literal
 *     placeholder = UiText.resource(Res.string.pick_market), // host Compose resource
 * )
 * ```
 *
 * Resources are Compose Multiplatform resources, so the same call works from common code on every
 * platform. On Android, `UiText.resource(R.string.pick_market)` with a classic `@StringRes` id is
 * also accepted (see the Android source set), for hosts whose strings live in `res/values`.
 */
@Immutable
public sealed interface UiText {

    /** A literal the caller already resolved. */
    @Immutable
    public data class Literal(val value: String) : UiText

    /** A string resource, optionally with format arguments. */
    @Immutable
    public data class Resource(
        val resource: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    /**
     * A plurals resource selected by [count].
     *
     * [args] are the format arguments substituted into the chosen item. They default to `[count]`,
     * which is the common case (`"3 countries selected"`), but a plural can carry more — an error
     * whose digit count decides singular/plural while the message also names the country and its
     * dial code, for example.
     */
    @Immutable
    public data class Plural(
        val resource: PluralStringResource,
        val count: Int,
        val args: List<Any> = listOf(count),
    ) : UiText

    /**
     * A string from a source the library does not know about, resolved by the subclass during
     * composition.
     *
     * This is the bridge for strings that are neither literals nor Compose Multiplatform resources:
     * Android `@StringRes` ids use it, and a host can extend it to read from anywhere else (a
     * remote-config catalog, an iOS `Localizable.strings` lookup). Implementations should be
     * immutable and implement `equals`, so an unchanged text does not recompose its component.
     */
    public abstract class Custom : UiText {
        /** Resolves the text against the current composition. */
        @Composable
        public abstract fun resolve(): String
    }

    public companion object {
        /** Wraps a literal string. */
        public fun of(value: String): UiText = Literal(value)

        /** References a string resource. */
        public fun resource(resource: StringResource, vararg args: Any): UiText =
            Resource(resource, args.toList())

        /**
         * References a plurals resource. [count] selects the item; [args] are its format
         * arguments, defaulting to `count` alone when none are given.
         */
        public fun plural(resource: PluralStringResource, count: Int, vararg args: Any): UiText =
            Plural(resource, count, if (args.isEmpty()) listOf(count) else args.toList())
    }
}

/**
 * Resolves this [UiText] against the current composition's resources.
 *
 * Reads the same environment Compose uses, so it recomposes correctly on locale change.
 */
@Composable
public fun UiText.resolve(): String = when (this) {
    is UiText.Literal -> value
    is UiText.Resource ->
        if (args.isEmpty()) stringResource(resource) else stringResource(resource, *args.toTypedArray())
    is UiText.Plural -> pluralStringResource(resource, count, *args.toTypedArray())
    is UiText.Custom -> resolve()
}

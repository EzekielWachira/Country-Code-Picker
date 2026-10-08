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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/**
 * References an Android string resource — for hosts whose strings live in `res/values`:
 *
 * ```kotlin
 * CountrySelector(placeholder = UiText.resource(R.string.pick_market))
 * ```
 *
 * Resolved against the composition's configuration like any other [UiText]. Code shared with iOS
 * should use the Compose Multiplatform overload, `UiText.resource(Res.string.pick_market)`.
 */
public fun UiText.Companion.resource(@StringRes id: Int, vararg args: Any): UiText =
    AndroidStringText(id, args.toList())

/** References an Android plurals resource; see [UiText.Companion.plural] for how [args] default. */
public fun UiText.Companion.plural(@PluralsRes id: Int, count: Int, vararg args: Any): UiText =
    AndroidPluralText(id, count, if (args.isEmpty()) listOf(count) else args.toList())

@Immutable
internal data class AndroidStringText(
    @StringRes val id: Int,
    val args: List<Any>,
) : UiText.Custom() {
    @Composable
    override fun resolve(): String =
        if (args.isEmpty()) stringResource(id) else stringResource(id, *args.toTypedArray())
}

@Immutable
internal data class AndroidPluralText(
    @PluralsRes val id: Int,
    val count: Int,
    val args: List<Any>,
) : UiText.Custom() {
    @Composable
    override fun resolve(): String = pluralStringResource(id, count, *args.toTypedArray())
}

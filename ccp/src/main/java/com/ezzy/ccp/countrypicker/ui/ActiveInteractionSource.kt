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

package com.ezzy.ccp.countrypicker.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * A [MutableInteractionSource] that reports a synthetic focus whenever [active] is true.
 *
 * [androidx.compose.material3.OutlinedTextFieldDefaults.Container]/`DecorationBox` read "focused"
 * from whatever interaction source they're given to decide the border color/width and the label's
 * notch position — normally the text field's own real keyboard-focus source. A unified phone field's
 * outline needs to read as active for a second reason too: the embedded country picker sheet being
 * open, which is not a text-field focus event at all. Feeding that second condition into the *same*
 * source the text field itself reports on would conflate "the picker is open" with "this field has
 * real keyboard focus" for any other code that reads it. A dedicated source kept only for the
 * border/label removes that conflation — the text field's real interaction source stays a truthful
 * signal of actual focus, and this one is purely a rendering input.
 */
@Composable
internal fun rememberActiveInteractionSource(active: Boolean): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    var currentFocus by remember { mutableStateOf<FocusInteraction.Focus?>(null) }
    LaunchedEffect(active) {
        if (active) {
            val focus = FocusInteraction.Focus()
            currentFocus = focus
            source.emit(focus)
        } else {
            currentFocus?.let { source.emit(FocusInteraction.Unfocus(it)) }
            currentFocus = null
        }
    }
    return source
}

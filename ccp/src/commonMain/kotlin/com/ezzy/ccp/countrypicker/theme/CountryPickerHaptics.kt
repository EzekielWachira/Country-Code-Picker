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

package com.ezzy.ccp.countrypicker.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Haptic feedback for the picker's key moments.
 *
 * Each moment maps to a platform haptic, which Compose Multiplatform plays through Android's haptic
 * constants and iOS's `UIFeedbackGenerator`s. Set a moment to `null` to keep it silent, or turn
 * everything off with [enabled].
 *
 * @property enabled False silences every haptic.
 * @property select A single selection commits.
 * @property toggleOn A country is ticked in multiple selection.
 * @property toggleOff A country is unticked.
 * @property scrub The A–Z rail crosses into a new letter.
 * @property filter A region filter changes.
 * @property limitReached A tick is refused because the selection is full.
 * @property error A field enters its error state.
 */
@Immutable
public data class CountryPickerHaptics(
    val enabled: Boolean = true,
    val select: HapticFeedbackType? = HapticFeedbackType.Confirm,
    val toggleOn: HapticFeedbackType? = HapticFeedbackType.ToggleOn,
    val toggleOff: HapticFeedbackType? = HapticFeedbackType.ToggleOff,
    val scrub: HapticFeedbackType? = HapticFeedbackType.SegmentFrequentTick,
    val filter: HapticFeedbackType? = HapticFeedbackType.SegmentTick,
    val limitReached: HapticFeedbackType? = HapticFeedbackType.Reject,
    val error: HapticFeedbackType? = HapticFeedbackType.Reject,
) {
    public companion object {
        /** No haptics at all. */
        public val Off: CountryPickerHaptics = CountryPickerHaptics(enabled = false)
    }
}

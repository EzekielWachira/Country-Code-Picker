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

/**
 * Whether a field-like component (the phone number field, a country selector) shows a visible label.
 *
 * - [Floating]: the label is drawn above the value, in the field's own outline, the way a Material
 *   text field does.
 * - [Hidden]: no visible label at all — the value is vertically centered in the space the label would
 *   otherwise have reserved. This is a *visual* choice only: hiding the label never removes it from
 *   accessibility. A component with `labelMode = Hidden` must still expose the label text through
 *   `contentDescription`/`stateDescription` so a screen-reader user is never left with an unlabeled
 *   control — see the `accessibilityLabel` parameter on the components that accept this enum.
 */
enum class InputLabelMode {
    Floating,
    Hidden,
}

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

package com.ezzy.ccp.countrypicker.phone

import androidx.compose.runtime.Immutable
import com.ezzy.ccp.countrypicker.model.UiText

/**
 * Where a phone number is in the verification flow.
 *
 * This is the whole observable surface of verification: a host that supplies its own OTP screen can
 * drive it from these states and ignore the library's UI entirely.
 */
@Immutable
public sealed interface PhoneVerificationState {

    /** Nothing started, or the number changed and any prior verification was invalidated. */
    public data object Idle : PhoneVerificationState

    /** A code request is in flight. Duplicate requests are rejected while in this state. */
    public data object SendingCode : PhoneVerificationState

    /**
     * A code was sent and the user is expected to enter it.
     *
     * @property verificationId Handle for this challenge.
     * @property destination Where the code went, for display.
     * @property resendAvailableAtMillis Unix-epoch milliseconds instant when resend unlocks,
     *   or `null` if resend is immediately available. Epoch millis keeps this usable at
     *   `minSdk 24` without desugaring `java.time`.
     */
    @Immutable
    public data class CodeSent(
        val verificationId: String,
        val destination: String,
        val resendAvailableAtMillis: Long? = null,
    ) : PhoneVerificationState

    /** A submitted code is being checked. */
    @Immutable
    public data class Verifying(val verificationId: String) : PhoneVerificationState

    /**
     * The number is verified.
     *
     * @property e164Number The verified number. Compare against the current field value to detect
     *   that the user has since edited the number.
     */
    @Immutable
    public data class Verified(val e164Number: String) : PhoneVerificationState

    /**
     * A step failed. The user can act again.
     *
     * @property verificationId Non-null when the challenge is still usable and the user can just
     *   re-enter the code; null when the failure was in requesting the code.
     */
    @Immutable
    public data class Failed(
        val message: UiText,
        val verificationId: String? = null,
        val retryable: Boolean = true,
    ) : PhoneVerificationState

    /** The challenge timed out. The user must request a new code. */
    public data object Expired : PhoneVerificationState

    /** True while a network call is in flight, for progress indicators and input blocking. */
    public val isInProgress: Boolean
        get() = this is SendingCode || this is Verifying

    /**
     * The active challenge id, if any.
     *
     * Named distinctly from the subclasses' own `verificationId` properties so it reads as a
     * cross-state accessor rather than shadowing them.
     */
    public val activeVerificationId: String?
        get() = when (this) {
            is CodeSent -> verificationId
            is Verifying -> verificationId
            is Failed -> verificationId
            else -> null
        }
}

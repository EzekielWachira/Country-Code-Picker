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

import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.UiText

/**
 * The contract a host implements to verify a phone number with its own backend.
 *
 * The library deliberately ships **no** implementation of this interface. There is no bundled
 * "demo" or in-memory verifier, because a fake that accepts `123456` is the kind of thing that
 * reaches production. Verification is entirely opt-in: pass a handler and the phone field grows a
 * verification affordance; pass nothing and the picker works exactly as before.
 *
 * Implementations own all transport concerns — endpoints, auth, retry, rate limiting, Firebase Auth
 * or SMS-gateway specifics. The library only sequences the calls and renders the resulting
 * [PhoneVerificationState]. Nothing in this interface touches Compose, so it can live in a data
 * module and be unit-tested without Android.
 *
 * All three functions are called from a coroutine the caller controls and may suspend for as long
 * as the network takes; the UI shows progress meanwhile and blocks duplicate requests.
 */
interface PhoneNumberVerificationHandler {

    /**
     * Requests a verification code be sent to [phoneNumber].
     *
     * The library guarantees [PhoneNumberValue.isValid] is `true` before calling this, so
     * implementations do not need to re-validate — though re-checking server-side is still correct.
     *
     * Throwing is acceptable: the controller catches it and surfaces
     * [PhoneVerificationState.Failed]. Returning [VerificationRequestResult.Failure] is preferred
     * when you have a message worth showing.
     */
    suspend fun requestVerification(phoneNumber: PhoneNumberValue): VerificationRequestResult

    /**
     * Checks [code] against the challenge identified by [verificationId].
     *
     * @param verificationId The id from the [VerificationRequestResult.Success] that started this
     *   challenge.
     */
    suspend fun verifyCode(verificationId: String, code: String): VerificationResult

    /**
     * Re-sends the code for an existing challenge.
     *
     * May return a new [VerificationRequestResult.Success.verificationId]; the controller adopts
     * whatever it returns, so rotating ids is fine.
     */
    suspend fun resendCode(verificationId: String): VerificationRequestResult
}

/** Outcome of [PhoneNumberVerificationHandler.requestVerification] or `resendCode`. */
sealed interface VerificationRequestResult {

    /**
     * A code was dispatched.
     *
     * @property verificationId Opaque handle for this challenge, passed back to `verifyCode`.
     * @property destination Where the code went, for display (e.g. `"+254 712 345 678"`).
     *   Prefer a masked form if your product masks numbers.
     * @property resendAvailableAtMillis `System.currentTimeMillis()`-based instant at which a
     *   resend becomes allowed, or `null` for no cooldown. Epoch millis rather than
     *   `java.time.Instant` so the library stays usable at `minSdk 24` without core-library
     *   desugaring.
     */
    data class Success(
        val verificationId: String,
        val destination: String,
        val resendAvailableAtMillis: Long? = null,
    ) : VerificationRequestResult

    /**
     * The code could not be sent.
     *
     * @property message Shown to the user as-is. Use [UiText.resource] to keep it localized.
     * @property retryable Whether offering a retry makes sense (network blip vs. blocked number).
     */
    data class Failure(
        val message: UiText,
        val retryable: Boolean = true,
    ) : VerificationRequestResult
}

/** Outcome of [PhoneNumberVerificationHandler.verifyCode]. */
sealed interface VerificationResult {

    /**
     * The code was correct.
     *
     * @property e164Number The number that is now verified, in E.164 form.
     */
    data class Verified(val e164Number: String) : VerificationResult

    /** The code was wrong. The user stays on the code screen and can retry. */
    data class Invalid(val message: UiText? = null) : VerificationResult

    /** The challenge is no longer valid; the user must request a new code. */
    data object Expired : VerificationResult

    /** Something else went wrong (network, server). */
    data class Failure(val message: UiText, val retryable: Boolean = true) : VerificationResult
}

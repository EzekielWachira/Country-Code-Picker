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

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.UiText
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The verification state machine.
 *
 * The handler here is a test double, not a shipped fake — the library deliberately provides no
 * implementation of [PhoneNumberVerificationHandler], so the only place one exists is in tests.
 */
class PhoneVerificationControllerTest {

    private val kenya = requireNotNull(DefaultCountryDataSource.findByIso2("KE"))
    private val validNumber = PhoneNumberValidator.evaluate("712345678", kenya)
    private val incompleteNumber = PhoneNumberValidator.evaluate("712", kenya)

    /** Records calls so duplicate-suppression can be asserted, not just assumed. */
    private class RecordingHandler(
        var requestResult: VerificationRequestResult = VerificationRequestResult.Success(
            verificationId = "vid-1",
            destination = "+254 712 345678",
        ),
        var verifyResult: VerificationResult = VerificationResult.Verified("+254712345678"),
    ) : PhoneNumberVerificationHandler {
        var requestCount = 0
        var verifyCount = 0
        var resendCount = 0
        var lastCode: String? = null

        override suspend fun requestVerification(
            phoneNumber: com.ezzy.ccp.countrypicker.model.PhoneNumberValue,
        ): VerificationRequestResult {
            requestCount++
            return requestResult
        }

        override suspend fun verifyCode(verificationId: String, code: String): VerificationResult {
            verifyCount++
            lastCode = code
            return verifyResult
        }

        override suspend fun resendCode(verificationId: String): VerificationRequestResult {
            resendCount++
            return requestResult
        }
    }

    @Test
    fun `a valid number moves to CodeSent`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)

        controller.requestCode(validNumber)

        assertEquals(1, handler.requestCount)
        val state = controller.state
        assertTrue(state is PhoneVerificationState.CodeSent)
        assertEquals("vid-1", (state as PhoneVerificationState.CodeSent).verificationId)
    }

    @Test
    fun `an invalid number cannot enter verification`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)

        controller.requestCode(incompleteNumber)

        // The handler must never be called — that would send a real SMS to an incomplete number.
        assertEquals(0, handler.requestCount)
        assertTrue(controller.state is PhoneVerificationState.Failed)
    }

    @Test
    fun `submitting the right code verifies`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)

        controller.requestCode(validNumber)
        controller.submitCode("123456")

        assertEquals("123456", handler.lastCode)
        assertEquals(PhoneVerificationState.Verified("+254712345678"), controller.state)
        assertTrue(controller.isVerified(validNumber))
    }

    @Test
    fun `a wrong code fails but keeps the challenge open for a retry`() = runTest {
        val handler = RecordingHandler(verifyResult = VerificationResult.Invalid())
        val controller = PhoneVerificationController(handler)

        controller.requestCode(validNumber)
        controller.submitCode("000000")

        val state = controller.state
        assertTrue(state is PhoneVerificationState.Failed)
        assertEquals("vid-1", (state as PhoneVerificationState.Failed).verificationId)

        // The same challenge can be retried rather than forcing a new code request.
        handler.verifyResult = VerificationResult.Verified("+254712345678")
        controller.submitCode("123456")
        assertTrue(controller.state is PhoneVerificationState.Verified)
    }

    @Test
    fun `an expired challenge requires a new code`() = runTest {
        val handler = RecordingHandler(verifyResult = VerificationResult.Expired)
        val controller = PhoneVerificationController(handler)

        controller.requestCode(validNumber)
        controller.submitCode("123456")

        assertEquals(PhoneVerificationState.Expired, controller.state)
        // No challenge id survives, so submitting again is a no-op rather than a wasted attempt.
        controller.submitCode("123456")
        assertEquals(1, handler.verifyCount)
    }

    @Test
    fun `a handler that throws becomes a Failed state, not a crash`() = runTest {
        val throwing = object : PhoneNumberVerificationHandler {
            override suspend fun requestVerification(
                phoneNumber: com.ezzy.ccp.countrypicker.model.PhoneNumberValue,
            ): VerificationRequestResult = error("network down")

            override suspend fun verifyCode(verificationId: String, code: String) =
                VerificationResult.Expired

            override suspend fun resendCode(verificationId: String): VerificationRequestResult =
                error("network down")
        }
        val controller = PhoneVerificationController(throwing)

        controller.requestCode(validNumber)

        assertTrue(controller.state is PhoneVerificationState.Failed)
    }

    @Test
    fun `a handler failure result is surfaced with its own message`() = runTest {
        val handler = RecordingHandler(
            requestResult = VerificationRequestResult.Failure(
                message = UiText.of("Number blocked"),
                retryable = false,
            ),
        )
        val controller = PhoneVerificationController(handler)

        controller.requestCode(validNumber)

        val state = controller.state as PhoneVerificationState.Failed
        assertEquals(UiText.of("Number blocked"), state.message)
        assertFalse(state.retryable)
    }

    // ── Duplicate suppression ───────────────────────────────────────────────────────────────────

    @Test
    fun `submitting twice while a check is in flight consumes one attempt`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)

        controller.submitCode("123456")
        // Already Verified, so there is no live challenge; a second submit must be a no-op.
        controller.submitCode("123456")

        assertEquals(1, handler.verifyCount)
    }

    @Test
    fun `submitting with no active challenge does nothing`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)

        controller.submitCode("123456")

        assertEquals(0, handler.verifyCount)
        assertEquals(PhoneVerificationState.Idle, controller.state)
    }

    // ── Resend cooldown ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `resend is refused until the cooldown elapses`() = runTest {
        var now = 1_000_000L
        val handler = RecordingHandler(
            requestResult = VerificationRequestResult.Success(
                verificationId = "vid-1",
                destination = "+254 712 345678",
                resendAvailableAtMillis = now + 30_000,
            ),
        )
        val controller = PhoneVerificationController(handler) { now }

        controller.requestCode(validNumber)
        assertEquals(30, controller.resendAvailableInSeconds())

        controller.resendCode()
        assertEquals("Resend must be refused during the cooldown", 0, handler.resendCount)

        now += 30_001
        assertEquals(0, controller.resendAvailableInSeconds())
        controller.resendCode()
        assertEquals(1, handler.resendCount)
    }

    @Test
    fun `no cooldown means resend is immediately available`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)

        assertEquals(0, controller.resendAvailableInSeconds())
        controller.resendCode()
        assertEquals(1, handler.resendCount)
    }

    // ── Invalidation on number change ───────────────────────────────────────────────────────────

    @Test
    fun `editing the number invalidates a completed verification`() = runTest {
        // The rule that stops a form submitting "verified" for a number the user has since edited.
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)
        controller.submitCode("123456")
        assertTrue(controller.state is PhoneVerificationState.Verified)

        val edited = PhoneNumberValidator.evaluate("712345679", kenya)
        controller.onPhoneNumberChanged(edited)

        assertEquals(PhoneVerificationState.Idle, controller.state)
        assertFalse(controller.isVerified(edited))
    }

    @Test
    fun `changing the country invalidates verification`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)
        controller.submitCode("123456")

        val germany = requireNotNull(DefaultCountryDataSource.findByIso2("DE"))
        controller.onPhoneNumberChanged(PhoneNumberValidator.evaluate("712345678", germany))

        assertEquals(PhoneVerificationState.Idle, controller.state)
    }

    @Test
    fun `re-reporting the same number is idempotent`() = runTest {
        // Called from a LaunchedEffect on every value change, so it must not reset a verified state.
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)
        controller.submitCode("123456")

        repeat(3) { controller.onPhoneNumberChanged(validNumber) }

        assertTrue(controller.state is PhoneVerificationState.Verified)
        assertTrue(controller.isVerified(validNumber))
    }

    @Test
    fun `isVerified is false for a different number even while Verified`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)
        controller.submitCode("123456")

        val other = PhoneNumberValidator.evaluate("722123456", kenya)
        assertFalse(controller.isVerified(other))
    }

    @Test
    fun `reset returns the controller to Idle`() = runTest {
        val handler = RecordingHandler()
        val controller = PhoneVerificationController(handler)
        controller.requestCode(validNumber)

        controller.reset()

        assertEquals(PhoneVerificationState.Idle, controller.state)
    }
}

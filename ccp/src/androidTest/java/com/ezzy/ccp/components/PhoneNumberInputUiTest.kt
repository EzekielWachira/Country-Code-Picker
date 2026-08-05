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

package com.ezzy.ccp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.height
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.state.PhoneState
import com.ezzy.ccp.state.rememberPhoneState
import com.ezzy.ccp.utils.CCPDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Regression coverage for the legacy [PhoneNumberInput] through the visual redesign: its public API,
 * callbacks and [com.ezzy.ccp.state.PhoneState] behaviour must be unaffected by
 * `UnifiedLegacyPhoneField` now rendering the `BottomSheet` style on one shared outline instead of two.
 *
 * See [com.ezzy.ccp.countrypicker.CountryPickerUiTest]'s class doc for why lookups here go through
 * content description rather than visible text.
 */
class PhoneNumberInputUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(content: @Composable () -> Unit) {
        rule.setContent {
            MaterialTheme { Surface { content() } }
        }
    }

    @Test
    fun onValueChangeStillReceivesTheFullPhoneSnapshot() {
        var last: Phone? = null
        setContent {
            PhoneNumberInput(setCountry = "KE", onValueChange = { last = it })
        }

        rule.onNodeWithContentDescription("Phone number input").performTextInput("712345678")
        rule.waitForIdle()

        assertEquals("KE", last?.country?.code)
        assertTrue(last?.isValid == true)
        assertEquals("+254712345678", last?.formattedPhone?.replace(" ", ""))
    }

    @Test
    fun onPhoneValueChangeStillFiresWithTheLegacyThreeArgSignature() {
        var formatted: String? = null
        var unformatted: String? = null
        var valid: Boolean? = null
        setContent {
            PhoneNumberInput(
                setCountry = "KE",
                onPhoneValueChange = { f, u, v -> formatted = f; unformatted = u; valid = v },
            )
        }

        rule.onNodeWithContentDescription("Phone number input").performTextInput("712345678")
        rule.waitForIdle()

        assertEquals(true, valid)
        // The legacy "unFormatedPhone" argument has always carried the E.164 form (see
        // PhoneNumberValue.toLegacyPhone: phoneNumber = e164Number ?: nationalNumber) — this
        // pre-dates the redesign, so the regression check is that it is still E.164, not bare digits.
        assertEquals("+254712345678", unformatted)
        assertTrue(formatted?.isNotEmpty() == true)
    }

    @Test
    fun theEmbeddedPrefixStillOpensTheSameCountryPickerSheet() {
        setContent {
            PhoneNumberInput(setCountry = "KE")
        }

        rule.onNode(hasContentDescription("Kenya", substring = true)).performClick()
        // The embedded prefix's sheet title is "Country code" (R.string.ccp_country_code_title),
        // distinct from the standalone selector's "Select country" — each context configures its own.
        rule.onNodeWithText("Country code").assertIsDisplayed()
    }

    @Test
    fun selectingACountryFromTheEmbeddedSheetUpdatesTheField() {
        var last: Phone? = null
        setContent {
            PhoneNumberInput(setCountry = "KE", onValueChange = { last = it })
        }

        // onValueChange's LaunchedEffect keys off formattedPhone/unformattedPhone/isValid, not the
        // country alone — with no digits typed, switching country leaves all three unchanged (still
        // empty/invalid) and the effect never refires. A digit must be present first so the region
        // switch actually changes the derived value and the callback observably fires.
        rule.onNodeWithContentDescription("Phone number input").performTextInput("712345678")
        rule.waitForIdle()

        rule.onNode(hasContentDescription("Kenya", substring = true)).performClick()
        rule.onNodeWithContentDescription("Search countries").performTextInput("Germany")
        rule.waitForIdle()
        // The legacy phone sheet shows dial-code metadata by default (showDialCodeCountryItem = true),
        // so a row's merged description is "Germany, +49", not the bare name.
        rule.onNode(hasContentDescription("Germany", substring = true)).performClick()
        rule.waitForIdle()

        assertEquals("DE", last?.country?.code)
    }

    @Test
    fun theClearButtonStillClearsTheNumberAndFiresOnValueChange() {
        var last: Phone? = null
        setContent {
            PhoneNumberInput(
                setCountry = "KE",
                onValueChange = { last = it },
                ccpConfig = CCPDefaults.defaultConfig(showClearButton = true),
            )
        }

        rule.onNodeWithContentDescription("Phone number input").performTextInput("712345678")
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Clear phone number").performClick()
        rule.waitForIdle()

        assertEquals("", last?.phoneNumber)
    }

    @Test
    fun theHintIsShownWhenEmpty() {
        setContent {
            PhoneNumberInput(setCountry = "KE", phoneHint = "Enter phone")
        }
        rule.onNodeWithText("Enter phone").assertIsDisplayed()
    }

    // ── Configurable label and divider ──────────────────────────────────────────────────────────

    @Test
    fun theLabelIsHiddenByDefault() {
        setContent {
            PhoneNumberInput(setCountry = "KE", label = "Phone number")
        }
        rule.onNodeWithText("Phone number").assertDoesNotExist()
    }

    @Test
    fun theLabelIsShownWhenConfigured() {
        setContent {
            PhoneNumberInput(
                setCountry = "KE",
                label = "Phone number",
                ccpConfig = CCPDefaults.defaultConfig(showLabel = true),
            )
        }
        rule.onNodeWithText("Phone number").assertIsDisplayed()
    }

    @Test
    fun hidingThePrefixDividerLeavesTheFieldFunctional() {
        setContent {
            PhoneNumberInput(
                setCountry = "KE",
                ccpConfig = CCPDefaults.defaultConfig(showPhonePrefixDivider = false),
            )
        }

        rule.onNode(hasContentDescription("Kenya", substring = true)).assertIsDisplayed()
        rule.onNodeWithContentDescription("Phone number input").performTextInput("712345678")
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Phone number input").assertIsDisplayed()
    }

    // ── Field size ──────────────────────────────────────────────────────────────────────────────

    @Test
    fun compactAndExtraCompactShrinkTheFieldHeight() {
        // All three sizes render in one composition — a ComposeTestRule only allows a single
        // setContent call per test. Each field's content description is the same fixed string
        // regardless of size, so each instance is wrapped in its own tagged Box instead; with no
        // error text shown, the Box's bounds equal the field's own bounds exactly.
        setContent {
            Column {
                listOf(PhoneFieldSize.Regular, PhoneFieldSize.Compact, PhoneFieldSize.ExtraCompact).forEach { size ->
                    Box(modifier = Modifier.testTag(size.name)) {
                        PhoneNumberInput(
                            setCountry = "KE",
                            ccpConfig = CCPDefaults.defaultConfig(phoneFieldSize = size),
                        )
                    }
                }
            }
        }

        val regular = rule.onNodeWithTag("Regular").getUnclippedBoundsInRoot().height
        val compact = rule.onNodeWithTag("Compact").getUnclippedBoundsInRoot().height
        val extraCompact = rule.onNodeWithTag("ExtraCompact").getUnclippedBoundsInRoot().height

        assertTrue("Compact ($compact) should be shorter than Regular ($regular)", compact < regular)
        assertTrue("ExtraCompact ($extraCompact) should be shorter than Compact ($compact)", extraCompact < compact)
    }

    @Test
    fun theExtraCompactFieldRemainsFullyFunctional() {
        // A smaller field must still work end to end — the size preset is meant to scale content
        // down, not to hide or disable it.
        setContent {
            PhoneNumberInput(
                setCountry = "KE",
                ccpConfig = CCPDefaults.defaultConfig(phoneFieldSize = PhoneFieldSize.ExtraCompact),
            )
        }

        rule.onNode(hasContentDescription("Kenya", substring = true)).performClick()
        rule.onNodeWithText("Country code").assertIsDisplayed()
        rule.onNodeWithContentDescription("Close").performClick()
        rule.waitForIdle()

        rule.onNodeWithContentDescription("Phone number input").performTextInput("712345678")
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Phone number input").assertIsDisplayed()
    }

    // ── Formatting and cursor placement ─────────────────────────────────────────────────────────

    @Test
    fun theFieldUsesInternationalFormattingNotNational() {
        // A US number's national form uses parentheses ("(712) 345-6789"); its international form
        // does not ("712-345-6789") — the dial code is already shown separately by the field's own
        // prefix, so the number itself should read the way it would in international notation.
        lateinit var capturedState: PhoneState
        setContent {
            val state = rememberPhoneState()
            capturedState = state
            PhoneNumberInput(setCountry = "US", state = state)
        }

        rule.onNodeWithContentDescription("Phone number input").performTextInput("7123456789")
        rule.waitForIdle()

        assertFalse(
            "Expected no parentheses, got '${capturedState.phoneField.text}'",
            "(" in capturedState.phoneField.text,
        )
    }

    @Test
    fun theCursorCanBePlacedMidStringAndStaysThereWhileEditing() {
        // Regression: the field used to reset the cursor to the end of the text on every edit,
        // making it impossible to place it anywhere but the tail.
        lateinit var capturedState: PhoneState
        setContent {
            val state = rememberPhoneState()
            capturedState = state
            PhoneNumberInput(setCountry = "KE", state = state)
        }

        val field = rule.onNodeWithContentDescription("Phone number input")
        field.performTextInput("712345678")
        rule.waitForIdle()

        // Kenya's grouping starts with a 3-digit block ("712 345 678"), so index 3 sits right after
        // "712" and before the separator — a stable, deterministic mid-string anchor to type at.
        field.performTextInputSelection(TextRange(3))
        field.performTextInput("9")
        rule.waitForIdle()

        val digitsBeforeCursor = capturedState.phoneField.text
            .take(capturedState.phoneField.selection.start)
            .count(Char::isDigit)
        assertEquals(4, digitsBeforeCursor)
        assertTrue(
            "Cursor should not have snapped to the end",
            capturedState.phoneField.selection.start < capturedState.phoneField.text.length,
        )
    }
}

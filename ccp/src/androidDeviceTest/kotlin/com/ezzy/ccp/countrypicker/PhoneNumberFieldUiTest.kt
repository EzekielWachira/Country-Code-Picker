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

package com.ezzy.ccp.countrypicker

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.model.InputLabelMode
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI behaviour for the refinement pass: the corrected close icon, the unified
 * [PhoneNumberField], its [PhoneNumberInputDefaults] configuration surface, and the row-only
 * [CountrySelector] variant.
 *
 * See [CountryPickerUiTest]'s class doc for why country/field lookups go through content
 * description rather than visible text throughout this library.
 */
class PhoneNumberFieldUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val kenya = requireNotNull(DefaultCountryDataSource.findByIso2("KE"))
    private val germany = requireNotNull(DefaultCountryDataSource.findByIso2("DE"))

    private fun setContent(content: @Composable () -> Unit) {
        rule.setContent {
            MaterialTheme { Surface { content() } }
        }
    }

    // ── Close icon ──────────────────────────────────────────────────────────────────────────────

    @Test
    fun theCloseIconTouchTargetMeetsTheAccessibilityMinimum() {
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(state = state, onValueChange = {})
        }
        rule.onNodeWithContentDescription("Kenya, calling code plus two five four. Double tap to change country.")
            .performClick()

        // The 48dp floor comes from CountryPickerDimensions.iconButtonSize on the IconButton itself,
        // independent of the 24dp glyph drawn inside it — this is what the oversized-icon bug broke.
        rule.onNodeWithContentDescription("Close")
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun theCloseIconDismissesTheEmbeddedSheet() {
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(state = state, onValueChange = {})
        }
        rule.onNodeWithContentDescription("Kenya, calling code plus two five four. Double tap to change country.")
            .performClick()
        // PhoneNumberField's embedded prefix sheet title is "Country code"
        // (Res.string.ccp_country_code_title) — a different context from the standalone selector's
        // "Select country", each configured independently per the sheet's own title/subtitle params.
        rule.onNodeWithText("Country code").assertIsDisplayed()

        rule.onNodeWithContentDescription("Close").performClick()
        rule.waitForIdle()

        rule.onNodeWithText("Country code").assertDoesNotExist()
    }

    // ── Unified field / prefix focus behaviour ─────────────────────────────────────────────────

    @Test
    fun tappingThePrefixOpensThePickerWithoutClearingTypedDigits() {
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
            PhoneNumberField(state = state, onValueChange = {})
        }

        rule.onNodeWithContentDescription("Kenya, calling code plus two five four. Double tap to change country.")
            .performClick()
        rule.waitForIdle()

        rule.onNodeWithText("Country code").assertIsDisplayed()
        // The number editor's own content description is unaffected by the prefix's own sheet opening
        // — the two regions stay independently interactive rather than merging into one element.
        rule.onNode(hasContentDescription("Phone number", substring = true)).assertExists()
    }

    @Test
    fun changingCountryFromThePrefixReformatsTheNumber() {
        var lastValue: PhoneNumberValue? = null
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
            PhoneNumberField(state = state, onValueChange = { lastValue = it })
        }

        rule.onNodeWithContentDescription("Kenya, calling code plus two five four. Double tap to change country.")
            .performClick()
        rule.onNodeWithContentDescription("Search countries").performTextInput("Germany")
        rule.waitForIdle()
        // PhoneNumberField's embedded prefix uses CountryPickerDefaults.phoneConfig(), which shows
        // dial-code metadata by default — a row's description is "Germany, +49", not the bare name.
        rule.onNode(hasContentDescription("Germany", substring = true)).performClick()
        rule.waitForIdle()

        assertEquals("DE", lastValue?.country?.iso2Code)
        // Digits are preserved and simply re-formatted/re-validated for the new region.
        assertTrue(lastValue?.nationalNumber?.contains("712345678") == true)
    }

    @Test
    fun theCursorCanBePlacedMidStringAndStaysThereWhileEditing() {
        // Regression: the field used to reset the cursor to the end of the text on every edit,
        // making it impossible to place it anywhere but the tail.
        lateinit var state: com.ezzy.ccp.countrypicker.state.PhoneNumberFieldState
        setContent {
            state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(state = state, onValueChange = {})
        }

        val editor = rule.onNode(hasContentDescription("Phone number", substring = true))
        editor.performTextInput("712345678")
        rule.waitForIdle()

        // Kenya's grouping starts with a 3-digit block ("712 345 678"), so index 3 sits right after
        // "712" and before the separator — a stable, deterministic mid-string anchor to type at.
        editor.performTextInputSelection(TextRange(3))
        editor.performTextInput("9")
        rule.waitForIdle()

        val digitsBeforeCursor = state.textFieldValue.text
            .take(state.textFieldValue.selection.start)
            .count(Char::isDigit)
        assertEquals(4, digitsBeforeCursor)
        assertTrue(
            "Cursor should not have snapped to the end",
            state.textFieldValue.selection.start < state.textFieldValue.text.length,
        )
    }

    // ── Field size ──────────────────────────────────────────────────────────────────────────────

    @Test
    fun compactAndExtraCompactShrinkTheFieldHeight() {
        // All three sizes render in one composition — a ComposeTestRule only allows a single
        // setContent call per test — distinguished by test tag on each field's own modifier.
        setContent {
            Column {
                listOf(PhoneFieldSize.Regular, PhoneFieldSize.Compact, PhoneFieldSize.ExtraCompact).forEach { size ->
                    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
                    PhoneNumberField(
                        state = state,
                        onValueChange = {},
                        inputStyle = PhoneNumberInputDefaults.style(size = size),
                        showHelperText = false,
                        modifier = Modifier.testTag(size.name),
                    )
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
            val state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(
                state = state,
                onValueChange = {},
                inputStyle = PhoneNumberInputDefaults.style(size = PhoneFieldSize.ExtraCompact),
            )
        }

        rule.onNodeWithContentDescription("Kenya, calling code plus two five four. Double tap to change country.")
            .performClick()
        rule.onNodeWithText("Country code").assertIsDisplayed()
        rule.onNodeWithContentDescription("Close").performClick()
        rule.waitForIdle()

        rule.onNode(hasContentDescription("Phone number", substring = true)).performTextInput("712345678")
        rule.waitForIdle()
        rule.onNode(hasContentDescription("Phone number", substring = true)).assertIsDisplayed()
    }

    // ── Label modes ─────────────────────────────────────────────────────────────────────────────

    @Test
    fun theFloatingLabelIsVisibleByDefault() {
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(state = state, onValueChange = {}, label = UiText.of("Phone number"))
        }
        rule.onNodeWithText("Phone number").assertIsDisplayed()
    }

    @Test
    fun theHiddenLabelModeRemovesTheVisibleLabelButKeepsAccessibilitySemantics() {
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(
                state = state,
                onValueChange = {},
                label = null,
                accessibilityLabel = UiText.of("Phone number"),
                inputStyle = PhoneNumberInputDefaults.hiddenLabelStyle(),
            )
        }

        rule.onNodeWithText("Phone number").assertDoesNotExist()
        rule.onNode(hasContentDescription("Phone number", substring = true)).assertExists()
    }

    // ── Prefix content modes ────────────────────────────────────────────────────────────────────

    @Test
    fun flagOnlyPrefixModeStillAnnouncesTheCountryAndOpensThePicker() {
        setContent {
            val state = rememberPhoneNumberFieldState(initialCountry = kenya)
            PhoneNumberField(
                state = state,
                onValueChange = {},
                inputStyle = PhoneNumberInputDefaults.style(prefixContentMode = PhonePrefixContentMode.FlagOnly),
            )
        }

        rule.onNodeWithContentDescription("Kenya, calling code plus two five four. Double tap to change country.")
            .performClick()
        rule.onNodeWithText("Country code").assertIsDisplayed()
    }

    // ── Row-only country selector ───────────────────────────────────────────────────────────────

    @Test
    fun theRowOnlySelectorHasNoVisibleLabelOrMetadataText() {
        setContent {
            var country by remember { mutableStateOf(germany) }
            CountrySelector(
                selectedCountry = country,
                onCountrySelected = { country = it },
                label = UiText.of("Country of residence"),
                labelMode = InputLabelMode.Hidden,
            )
        }

        // The label is suppressed entirely in row-only mode, not merely made invisible.
        rule.onNodeWithText("Country of residence").assertDoesNotExist()
        // Its own merged description still identifies the row for accessibility.
        rule.onNode(hasContentDescription("Germany", substring = true)).assertIsDisplayed()
    }

    @Test
    fun supportingMetadataCanBeDisabledIndependentlyOfTheSheet() {
        setContent {
            var country by remember { mutableStateOf(germany) }
            CountrySelector(
                selectedCountry = country,
                onCountrySelected = { country = it },
                supportingContent = CountrySupportingContent.None,
            )
        }

        // With metadata off, the field's merged description carries only the name, not ISO/dial code.
        rule.onNode(hasContentDescription("DEU", substring = true)).assertDoesNotExist()
        rule.onNode(hasContentDescription("+49", substring = true)).assertDoesNotExist()
        rule.onNode(hasContentDescription("Germany", substring = true)).assertIsDisplayed()
    }

    @Test
    fun theRowOnlySelectorStillOpensTheSameSheetAndUpdatesOnSelection() {
        var received: com.ezzy.ccp.countrypicker.model.Country? = null
        setContent {
            var country by remember { mutableStateOf(germany) }
            CountrySelector(
                selectedCountry = country,
                onCountrySelected = {
                    country = it
                    received = it
                },
                labelMode = InputLabelMode.Hidden,
            )
        }

        rule.onNode(hasContentDescription("Germany", substring = true)).performClick()
        rule.onNodeWithContentDescription("Search countries").performTextInput("Kenya")
        rule.waitForIdle()
        // A row reads its name and dial code: "Kenya, +254".
        rule.onNodeWithContentDescription("Kenya, +254").performClick()
        rule.waitForIdle()

        assertEquals("KE", received?.iso2Code)
        rule.onNode(hasContentDescription("Kenya", substring = true)).assertIsDisplayed()
    }
}

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

package com.ezzy.ccp.countrypicker.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryFlagConfig
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountrySelectorDefaults
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.ui.CountryPickerSheet
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorState
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField

/**
 * Previews for the UI-review refinement: label modes, flag presentation, prefix content modes, and the
 * row-only selector. Kept separate from [CountryPickerPreviews] so this pass's additions are easy to
 * find and review together.
 *
 * As with the rest of the catalogue, every preview uses static in-memory state and the bundled
 * [DefaultCountryDataSource] — no backend required to render any of these in Android Studio.
 */
private val kenya by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("KE")) }
private val germany by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("DE")) }

@Composable
private fun RefinementPreviewSurface(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                content()
            }
        }
    }
}

// ── Label modes ────────────────────────────────────────────────────────────────────────────────────

@Preview(name = "Phone field — floating label", showBackground = true)
@Composable
private fun PhoneFieldFloatingLabelPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(state = state, onValueChange = {})
}

@Preview(name = "Phone field — hidden label", showBackground = true)
@Composable
private fun PhoneFieldHiddenLabelPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        label = null,
        accessibilityLabel = UiText.of("Phone number"),
        inputStyle = PhoneNumberInputDefaults.hiddenLabelStyle(),
    )
}

// ── Flag presentation ──────────────────────────────────────────────────────────────────────────────

@Preview(name = "Phone field — plain flag (default)", showBackground = true)
@Composable
private fun PhoneFieldPlainFlagPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(
            flagConfig = CountryFlagConfig(style = CountryFlagStyle.Plain, size = 24.dp),
        ),
    )
}

@Preview(name = "Phone field — filled flag container", showBackground = true)
@Composable
private fun PhoneFieldFilledFlagPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(
            flagConfig = CountryFlagConfig(style = CountryFlagStyle.FilledContainer, size = 26.dp),
        ),
    )
}

@Preview(name = "Phone field — tonal flag container", showBackground = true)
@Composable
private fun PhoneFieldTonalFlagPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(
            flagConfig = CountryFlagConfig(style = CountryFlagStyle.TonalContainer, size = 26.dp),
        ),
    )
}

// ── Prefix content modes ───────────────────────────────────────────────────────────────────────────

@Preview(name = "Phone field — flag and dial code", showBackground = true)
@Composable
private fun PhoneFieldFlagAndDialCodePreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(prefixContentMode = PhonePrefixContentMode.FlagAndDialCode),
    )
}

@Preview(name = "Phone field — flag only prefix", showBackground = true)
@Composable
private fun PhoneFieldFlagOnlyPrefixPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(prefixContentMode = PhonePrefixContentMode.FlagOnly),
    )
}

@Preview(name = "Phone field — dial code only prefix", showBackground = true)
@Composable
private fun PhoneFieldDialCodeOnlyPrefixPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(prefixContentMode = PhonePrefixContentMode.DialCodeOnly),
    )
}

@Preview(name = "Phone field — no prefix divider", showBackground = true)
@Composable
private fun PhoneFieldNoDividerPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(showPrefixDivider = false),
    )
}

// ── Phone field states ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Phone field — validation error", showBackground = true)
@Composable
private fun PhoneFieldErrorPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "71")
    PhoneNumberField(state = state, onValueChange = {}, validateWhileTyping = true)
}

@Preview(name = "Phone field — disabled", showBackground = true)
@Composable
private fun PhoneFieldDisabledPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(state = state, onValueChange = {}, enabled = false)
}

// ── Field size ─────────────────────────────────────────────────────────────────────────────────────

@Preview(name = "Phone field — compact", showBackground = true)
@Composable
private fun PhoneFieldCompactPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(size = PhoneFieldSize.Compact),
    )
}

@Preview(name = "Phone field — extra compact", showBackground = true)
@Composable
private fun PhoneFieldExtraCompactPreview() = RefinementPreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        inputStyle = PhoneNumberInputDefaults.style(size = PhoneFieldSize.ExtraCompact),
    )
}

// ── Row-only country selector ──────────────────────────────────────────────────────────────────────

@Preview(name = "Country selector — full (label + metadata)", showBackground = true)
@Composable
private fun SelectorFullContentPreview() = RefinementPreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        contentConfig = CountrySelectorDefaults.contentConfig(),
    )
}

@Preview(name = "Country selector — label, no metadata", showBackground = true)
@Composable
private fun SelectorNoMetadataPreview() = RefinementPreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        contentConfig = CountrySelectorDefaults.contentConfig(
            supportingContent = CountrySupportingContent.None,
        ),
    )
}

@Preview(name = "Country selector — row only (no label, no metadata)", showBackground = true)
@Composable
private fun SelectorRowOnlyPreview() = RefinementPreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        label = UiText.of("Country of residence"),
        contentConfig = CountrySelectorDefaults.rowOnlyContentConfig(),
    )
}

@Preview(name = "Country selector — row only, error state", showBackground = true)
@Composable
private fun SelectorRowOnlyErrorPreview() = RefinementPreviewSurface {
    CountrySelector(
        selectedCountry = null,
        onCountrySelected = {},
        contentConfig = CountrySelectorDefaults.rowOnlyContentConfig(),
        state = CountrySelectorState.Error,
        errorText = UiText.of("Country of residence is required"),
    )
}

// ── Corrected close icon ───────────────────────────────────────────────────────────────────────────

@Preview(name = "Sheet — corrected close icon", showBackground = true, heightDp = 800)
@Composable
private fun CorrectedCloseIconSheetPreview() = RefinementPreviewSurface {
    val state = rememberCountryPickerState(selectedCountries = setOf(germany))
    remember(Unit) { state.open() }
    CountryPickerSheet(state = state, onDismiss = {})
}

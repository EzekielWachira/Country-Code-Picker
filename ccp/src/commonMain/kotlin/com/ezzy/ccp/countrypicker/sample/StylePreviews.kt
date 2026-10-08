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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.model.InputLabelMode
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryListStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerDensity
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyles
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.ui.CountryPickerSheet
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorState
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField

/**
 * Previews of the style presets and of the phone field's options. Kept apart from
 * [CountryPickerPreviews], which covers the components and their states.
 *
 * As with the rest of the catalogue, every preview uses static in-memory state and the bundled
 * [DefaultCountryDataSource] — no backend required to render any of these in Android Studio.
 */
private val kenya by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("KE")) }
private val germany by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("DE")) }

@Composable
private fun StylePreviewSurface(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                content()
            }
        }
    }
}

/** A selector and a filled-in phone field in [style], for comparing presets side by side. */
@Composable
private fun PresetSample(style: CountryPickerStyle) {
    CountryPickerTheme(style) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            var country by remember { mutableStateOf(germany) }
            CountrySelector(
                selectedCountry = country,
                onCountrySelected = { country = it },
                supportingContent = CountrySupportingContent.DialCode,
            )
            PhoneNumberField(
                onValueChange = {},
                state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678"),
            )
        }
    }
}

// ── Presets ───────────────────────────────────────────────────────────────────────────────────────

@Preview(name = "Preset — Signature", showBackground = true)
@Composable
private fun SignaturePresetPreview() = StylePreviewSurface { PresetSample(CountryPickerStyles.signature()) }

@Preview(name = "Preset — Signature, custom accent", showBackground = true)
@Composable
private fun SignatureAccentPresetPreview() = StylePreviewSurface {
    PresetSample(CountryPickerStyles.signature(accent = Color(0xFF0F766E)))
}

@Preview(name = "Preset — Material", showBackground = true)
@Composable
private fun MaterialPresetPreview() = StylePreviewSurface { PresetSample(CountryPickerStyles.material()) }

@Preview(name = "Preset — Cupertino", showBackground = true)
@Composable
private fun CupertinoPresetPreview() = StylePreviewSurface { PresetSample(CountryPickerStyles.cupertino()) }

@Preview(name = "Preset — Minimal", showBackground = true)
@Composable
private fun MinimalPresetPreview() = StylePreviewSurface { PresetSample(CountryPickerStyles.minimal()) }

@Preview(name = "Preset — Signature, dark", showBackground = true, uiMode = 0x20)
@Composable
private fun SignatureDarkPresetPreview() = StylePreviewSurface { PresetSample(CountryPickerStyles.signature(dark = true)) }

@Preview(name = "Sheet — Cupertino", showBackground = true, heightDp = 800)
@Composable
private fun CupertinoSheetPreview() = StylePreviewSurface {
    val state = rememberOpenCountryPickerState(selectedCountries = setOf(germany))
    CountryPickerSheet(state = state, onDismiss = {}, style = CountryPickerStyles.cupertino())
}

@Preview(name = "Sheet — plain list, circle flags, compact", showBackground = true, heightDp = 800)
@Composable
private fun PlainCompactSheetPreview() = StylePreviewSurface {
    val base = CountryPickerStyles.signature(density = CountryPickerDensity.Compact)
    val state = rememberOpenCountryPickerState(selectedCountries = setOf(germany))
    CountryPickerSheet(
        state = state,
        onDismiss = {},
        style = base.copy(layout = base.layout.copy(listStyle = CountryListStyle.Plain, flagStyle = CountryFlagStyle.Circle)),
    )
}

// ── Selector variants ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Selector — every variant", showBackground = true, heightDp = 900)
@Composable
private fun SelectorVariantsPreview() = StylePreviewSurface {
    var country by remember { mutableStateOf(kenya) }
    CountrySelectorVariant.entries.forEach { variant ->
        CountrySelector(
            selectedCountry = country,
            onCountrySelected = { country = it },
            variant = variant,
            label = UiText.of(variant.name),
        )
    }
}

@Preview(name = "Selector — hidden label, no metadata", showBackground = true)
@Composable
private fun SelectorHiddenLabelPreview() = StylePreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        labelMode = InputLabelMode.Hidden,
    )
}

@Preview(name = "Selector — required, error", showBackground = true)
@Composable
private fun SelectorRequiredErrorPreview() = StylePreviewSurface {
    CountrySelector(
        selectedCountry = null,
        onCountrySelected = {},
        required = true,
        state = CountrySelectorState.Error,
        errorText = UiText.of("Country of residence is required"),
    )
}

// ── Phone field: label, prefix, divider ───────────────────────────────────────────────────────────

@Preview(name = "Phone field — floating label, resting", showBackground = true)
@Composable
private fun PhoneFieldRestingLabelPreview() = StylePreviewSurface {
    PhoneNumberField(state = rememberPhoneNumberFieldState(initialCountry = kenya), onValueChange = {})
}

@Preview(name = "Phone field — hidden label", showBackground = true)
@Composable
private fun PhoneFieldHiddenLabelPreview() = StylePreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "7123")
    PhoneNumberField(
        state = state,
        onValueChange = {},
        label = null,
        accessibilityLabel = UiText.of("Phone number"),
        inputStyle = PhoneNumberInputDefaults.hiddenLabelStyle(),
    )
}

@Preview(name = "Phone field — prefix content modes", showBackground = true)
@Composable
private fun PhoneFieldPrefixModesPreview() = StylePreviewSurface {
    PhonePrefixContentMode.entries.forEach { mode ->
        PhoneNumberField(
            state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678"),
            onValueChange = {},
            label = UiText.of(mode.name),
            inputStyle = PhoneNumberInputDefaults.style(prefixContentMode = mode),
        )
    }
}

@Preview(name = "Phone field — plain, no extras", showBackground = true)
@Composable
private fun PhoneFieldNoExtrasPreview() = StylePreviewSurface {
    PhoneNumberField(
        state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678"),
        onValueChange = {},
        variant = CountrySelectorVariant.Outlined,
        inputStyle = PhoneNumberInputDefaults.style(
            showPrefixDivider = false,
            showGhostDigits = false,
            showProgress = false,
            showNumberType = false,
            showValidIndicator = false,
        ),
    )
}

// ── Phone field: variants and sizes ───────────────────────────────────────────────────────────────

@Preview(name = "Phone field — variants", showBackground = true, heightDp = 700)
@Composable
private fun PhoneFieldVariantsPreview() = StylePreviewSurface {
    listOf(
        CountrySelectorVariant.Elevated,
        CountrySelectorVariant.Outlined,
        CountrySelectorVariant.Filled,
        CountrySelectorVariant.Underlined,
        CountrySelectorVariant.Card,
    ).forEach { variant ->
        PhoneNumberField(
            state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "71234"),
            onValueChange = {},
            variant = variant,
            label = UiText.of(variant.name),
            showHelperText = false,
        )
    }
}

@Preview(name = "Phone field — sizes", showBackground = true)
@Composable
private fun PhoneFieldSizesPreview() = StylePreviewSurface {
    PhoneFieldSize.entries.forEach { size ->
        PhoneNumberField(
            state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678"),
            onValueChange = {},
            label = UiText.of(size.name),
            showHelperText = false,
            inputStyle = PhoneNumberInputDefaults.style(size = size),
        )
    }
}

// ── Phone field: states ───────────────────────────────────────────────────────────────────────────

@Preview(name = "Phone field — validation error", showBackground = true)
@Composable
private fun PhoneFieldErrorPreview() = StylePreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "71")
    PhoneNumberField(state = state, onValueChange = {}, validateWhileTyping = true)
}

@Preview(name = "Phone field — disabled", showBackground = true)
@Composable
private fun PhoneFieldDisabledPreview() = StylePreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(state = state, onValueChange = {}, enabled = false)
}

// ── Right-to-left ──────────────────────────────────────────────────────────────────────────────────

/**
 * The phone field and the country sheet under an RTL layout direction.
 *
 * This is the layout most likely to break: a dial code and a grouped national number are entirely
 * neutral and weak-direction characters, so without the LTR pinning in
 * [com.ezzy.ccp.countrypicker.model.CountryPickerBidi] they reorder inside an RTL paragraph and
 * "+254" renders as "254+". Previewing the direction — rather than only the Arabic strings — is
 * what catches that, because the bug reproduces with English text in an RTL layout too.
 */
@Preview(name = "RTL — phone field", showBackground = true, locale = "ar")
@Composable
private fun RtlPhoneFieldPreview() = StylePreviewSurface {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PhoneNumberField(
                onValueChange = {},
                state = rememberPhoneNumberFieldState(initialCountry = germany, initialNumber = "15123456789"),
            )
            PhoneNumberField(
                onValueChange = {},
                state = rememberPhoneNumberFieldState(initialCountry = germany),
            )
        }
    }
}

@Preview(name = "RTL — country sheet", showBackground = true, heightDp = 800, locale = "ar")
@Composable
private fun RtlCountrySheetPreview() = StylePreviewSurface {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val state = rememberOpenCountryPickerState(selectedCountries = setOf(germany))
        CountryPickerSheet(state = state, onDismiss = {})
    }
}

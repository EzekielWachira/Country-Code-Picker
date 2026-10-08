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
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.state.CountryPickerState
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.ui.CountryPickerSheet
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorState
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.ezzy.ccp.countrypicker.ui.MultiCountrySelector
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField

/**
 * The library's preview catalogue.
 *
 * Every preview uses static, in-memory state — no backend, no persistence, no network — so the whole
 * catalogue renders in Android Studio with nothing running. Country data comes straight from
 * [DefaultCountryDataSource], the same bundled dataset the library ships, so what you see here is
 * exactly what a host app sees offline.
 */
private val germany by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("DE")) }
private val kenya by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("KE")) }
private val france by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("FR")) }
private val japan by lazy { requireNotNull(DefaultCountryDataSource.findByIso2("JP")) }

/** Wraps a preview in the app's Material theme and a themed background. */
@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                content()
            }
        }
    }
}

/**
 * A [CountryPickerState] that is already open, with [configure] applied once.
 *
 * Previews have no user to tap the selector, so the sheet has to be opened programmatically. Doing
 * that inline as `remember(Unit) { state.open() }` returns `Unit` from `remember` (which is a lint
 * error, and reads as a side effect disguised as a value); worse, the follow-up calls that set a
 * region or a search query used to sit bare in the composable body and therefore re-ran on every
 * recomposition. Both problems go away when the whole setup happens once, keyed on the state, and
 * the state itself is what `remember` returns.
 */
@Composable
internal fun rememberOpenCountryPickerState(
    config: CountryPickerConfig = CountryPickerConfig(),
    selectedCountries: Set<Country> = emptySet(),
    configure: CountryPickerState.() -> Unit = {},
): CountryPickerState {
    val state = rememberCountryPickerState(config = config, selectedCountries = selectedCountries)
    return remember(state) { state.apply { open(); configure() } }
}

// ── 1. Full country selector ──────────────────────────────────────────────────────────────────────

@Preview(name = "1 · Full selector — outlined", showBackground = true)
@Composable
private fun FullSelectorOutlinedPreview() = PreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
}

@Preview(name = "1 · Full selector — filled", showBackground = true)
@Composable
private fun FullSelectorFilledPreview() = PreviewSurface {
    var country by remember { mutableStateOf(kenya) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        variant = CountrySelectorVariant.Filled,
    )
}

@Preview(name = "1 · Full selector — empty", showBackground = true)
@Composable
private fun FullSelectorEmptyPreview() = PreviewSurface {
    var country by remember { mutableStateOf<com.ezzy.ccp.countrypicker.model.Country?>(null) }
    CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
}

// ── 2. Compact selector ────────────────────────────────────────────────────────────────────────────

@Preview(name = "2 · Compact selector", showBackground = true)
@Composable
private fun CompactSelectorPreview() = PreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        variant = CountrySelectorVariant.Compact,
        label = null,
    )
}

// ── 3. Flag-only selector ──────────────────────────────────────────────────────────────────────────

@Preview(name = "3 · Flag-only selector", showBackground = true)
@Composable
private fun FlagOnlySelectorPreview() = PreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        variant = CountrySelectorVariant.FlagOnly,
        label = null,
    )
}

// ── 4. Phone prefix selector ───────────────────────────────────────────────────────────────────────

@Preview(name = "4 · Phone prefix selector", showBackground = true)
@Composable
private fun PhonePrefixSelectorPreview() = PreviewSurface {
    var country by remember { mutableStateOf(kenya) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        variant = CountrySelectorVariant.DialCode,
        label = null,
        config = CountryPickerDefaults.phoneConfig(),
    )
}

// ── 5. Complete phone-number input ─────────────────────────────────────────────────────────────────

@Preview(name = "5 · Phone number field", showBackground = true)
@Composable
private fun PhoneNumberFieldPreview() = PreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678")
    PhoneNumberField(state = state, onValueChange = {})
}

@Preview(name = "5 · Phone number field — empty", showBackground = true)
@Composable
private fun PhoneNumberFieldEmptyPreview() = PreviewSurface {
    val state = rememberPhoneNumberFieldState(initialCountry = germany)
    PhoneNumberField(state = state, onValueChange = {})
}

// ── 6–8. Selector states ───────────────────────────────────────────────────────────────────────────

@Preview(name = "6 · Disabled selector", showBackground = true)
@Composable
private fun DisabledSelectorPreview() = PreviewSurface {
    CountrySelector(selectedCountry = germany, onCountrySelected = {}, enabled = false)
}

@Preview(name = "7 · Error selector", showBackground = true)
@Composable
private fun ErrorSelectorPreview() = PreviewSurface {
    CountrySelector(
        selectedCountry = null,
        onCountrySelected = {},
        state = CountrySelectorState.Error,
        errorText = UiText.of("Country of residence is required"),
    )
}

@Preview(name = "8 · Loading selector", showBackground = true)
@Composable
private fun LoadingSelectorPreview() = PreviewSurface {
    CountrySelector(
        selectedCountry = null,
        onCountrySelected = {},
        state = CountrySelectorState.Loading,
    )
}

@Preview(name = "Success selector", showBackground = true)
@Composable
private fun SuccessSelectorPreview() = PreviewSurface {
    CountrySelector(
        selectedCountry = kenya,
        onCountrySelected = {},
        state = CountrySelectorState.Success,
        successText = UiText.of("Residency confirmed"),
    )
}

// ── 9–12. Sheet states (opened once, statically, for the preview) ────────────────────────────────────

@Preview(name = "9 · Single-selection sheet", showBackground = true, heightDp = 800)
@Composable
private fun SingleSelectionSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState(selectedCountries = setOf(germany))
    CountryPickerSheet(state = state, onDismiss = {})
}

@Preview(name = "10 · Region-filtered sheet", showBackground = true, heightDp = 800)
@Composable
private fun RegionFilteredSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState {
        selectRegion(com.ezzy.ccp.countrypicker.model.CountryRegion.Africa)
    }
    CountryPickerSheet(state = state, onDismiss = {})
}

@Preview(name = "11 · Search results", showBackground = true, heightDp = 800)
@Composable
private fun SearchResultsSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState { updateSearchQuery("uni") }
    CountryPickerSheet(state = state, onDismiss = {})
}

@Preview(name = "12 · Empty search results", showBackground = true, heightDp = 800)
@Composable
private fun EmptySearchResultsSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState { updateSearchQuery("zzzznotacountry") }
    CountryPickerSheet(state = state, onDismiss = {})
}

// ── 13–14. Multi-selection ─────────────────────────────────────────────────────────────────────────

@Preview(name = "13 · Multi-selection selector", showBackground = true)
@Composable
private fun MultiSelectionSelectorPreview() = PreviewSurface {
    var selected by remember { mutableStateOf(setOf(france, japan)) }
    MultiCountrySelector(selectedCountries = selected, onSelectionConfirmed = { selected = it })
}

@Preview(name = "13 · Multi-selection sheet", showBackground = true, heightDp = 800)
@Composable
private fun MultiSelectionSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState(
        config = CountryPickerDefaults.multiSelectConfig(),
        selectedCountries = setOf(france, japan),
    )
    val style = CountryPickerTheme.style
    CountryPickerSheet(state = state, onDismiss = {}, style = style.copy(layout = style.layout.copy(showDialCode = false)))
}

@Preview(name = "14 · Multi-selection with ISO and dial code", showBackground = true, heightDp = 800)
@Composable
private fun MultiSelectionWithMetadataSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState(
        config = CountryPickerDefaults.multiSelectConfig(),
        selectedCountries = setOf(france),
    )
    val style = CountryPickerTheme.style
    CountryPickerSheet(state = state, onDismiss = {}, style = style.copy(layout = style.layout.copy(showIsoCode = true)))
}

// ── 15. Dark mode ──────────────────────────────────────────────────────────────────────────────────

@Preview(name = "15 · Dark mode — selector", showBackground = true, uiMode = 0x20)
@Composable
private fun DarkModeSelectorPreview() = PreviewSurface {
    var country by remember { mutableStateOf(germany) }
    CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
}

@Preview(name = "15 · Dark mode — sheet", showBackground = true, heightDp = 800, uiMode = 0x20)
@Composable
private fun DarkModeSheetPreview() = PreviewSurface {
    val state = rememberOpenCountryPickerState(selectedCountries = setOf(kenya))
    CountryPickerSheet(state = state, onDismiss = {})
}

// ── 16. Large font scale ───────────────────────────────────────────────────────────────────────────

@Preview(
    name = "16 · Large font scale",
    showBackground = true,
    fontScale = 2f,
)
@Composable
private fun LargeFontScalePreview() = PreviewSurface {
    var country by remember { mutableStateOf(germany) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
        CountrySelector(
            selectedCountry = country,
            onCountrySelected = { country = it },
            variant = CountrySelectorVariant.Compact,
            label = null,
        )
    }
}

// ── 17. RTL layout ─────────────────────────────────────────────────────────────────────────────────

@Preview(name = "17 · RTL layout", showBackground = true, locale = "ar")
@Composable
private fun RtlLayoutPreview() = PreviewSurface {
    androidx.compose.ui.platform.LocalLayoutDirection.current
    var country by remember { mutableStateOf(germany) }
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
            CountrySelector(
                selectedCountry = country,
                onCountrySelected = { country = it },
                variant = CountrySelectorVariant.DialCode,
                label = null,
            )
        }
    }
}

// ── Sample screen ──────────────────────────────────────────────────────────────────────────────────

@Preview(name = "Sample — Your details", showBackground = true, heightDp = 900)
@Composable
private fun YourDetailsSampleScreenPreview() = MaterialTheme {
    YourDetailsSampleScreen()
}

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

package com.ezzy.ccp.sample

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.detection.rememberDefaultCountryDetector
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.persistence.rememberDefaultRecentCountryStore
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryFlagSource
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryListStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDensity
import com.ezzy.ccp.countrypicker.theme.CountryPickerHaptics
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyles
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.FlagImageFormat
import com.ezzy.ccp.countrypicker.theme.FlagImageShape
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.theme.PickerPresentation
import com.ezzy.ccp.countrypicker.theme.QuickPicksStyle
import com.ezzy.ccp.countrypicker.ui.CountryFlag
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.ezzy.ccp.countrypicker.ui.MultiCountrySelector
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField

/** The style presets the showcase switches between. */
private enum class Preset { Signature, Material, Cupertino, Minimal }

/** Flag artwork: the platform's emoji, or one of flagcdn's three shapes. */
private enum class FlagArt(val label: String, val shape: FlagImageShape?) {
    Emoji("Emoji", null),
    Waving("Waving", FlagImageShape.Waving),
    SameWidth("Same width", FlagImageShape.OriginalSameWidth),
    SameHeight("Same height", FlagImageShape.OriginalSameHeight),
}

/** Accent swatches. `null` takes the host theme's primary color. */
private val Accents: List<Pair<String, Color?>> = listOf(
    "Theme" to null,
    "Indigo" to Color(0xFF4F46E5),
    "Teal" to Color(0xFF0F766E),
    "Rose" to Color(0xFFE11D48),
    "Amber" to Color(0xFFD97706),
    "Violet" to Color(0xFF7C3AED),
)

/**
 * A live tour of the picker: every option of the design system is a control at the top, and every
 * component below re-styles as they change.
 *
 * The sample exists as much for CI as for reading: it is the only place the library is exercised as
 * a real app, and a feature that is never called from here is a feature no build ever runs.
 *
 * @param dark Dark mode, owned by the caller because it also re-themes the app around this screen.
 */
@Composable
fun ShowcaseScreen(dark: Boolean, onDarkChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var preset by rememberSaveable { mutableStateOf(Preset.Signature) }
    var accent by rememberSaveable { mutableIntStateOf(0) }
    var density by rememberSaveable { mutableStateOf(CountryPickerDensity.Comfortable) }
    // Layout overrides. `null` keeps the preset's own choice; picking a preset clears them.
    var listStyle by rememberSaveable(preset) { mutableStateOf<CountryListStyle?>(null) }
    var flagStyle by rememberSaveable(preset) { mutableStateOf<CountryFlagStyle?>(null) }
    var presentation by rememberSaveable(preset) { mutableStateOf<PickerPresentation?>(null) }
    var quickPicks by rememberSaveable(preset) { mutableStateOf<QuickPicksStyle?>(null) }
    var regionFilters by rememberSaveable(preset) { mutableStateOf<Boolean?>(null) }
    var alphabetIndex by rememberSaveable(preset) { mutableStateOf<Boolean?>(null) }
    var dialCodes by rememberSaveable(preset) { mutableStateOf<Boolean?>(null) }
    var flagArt by rememberSaveable(preset) { mutableStateOf<FlagArt?>(null) }
    var flagFormat by rememberSaveable { mutableStateOf(FlagImageFormat.Png) }
    var haptics by rememberSaveable { mutableStateOf(true) }

    val accentColor = Accents[accent].second
    val base = when (preset) {
        Preset.Signature -> CountryPickerStyles.signature(
            accent = accentColor ?: MaterialTheme.colorScheme.primary,
            density = density,
        )
        Preset.Material -> CountryPickerStyles.material(density = density)
        Preset.Cupertino -> CountryPickerStyles.cupertino(accent = accentColor, density = density)
        Preset.Minimal -> CountryPickerStyles.minimal(density = density)
    }
    val layout = base.layout
    val presetArt = (layout.flagSource as? CountryFlagSource.FlagCdn)?.shape
        ?.let { shape -> FlagArt.entries.first { it.shape == shape } } ?: FlagArt.Emoji
    val art = flagArt ?: presetArt
    val flagSource = art.shape?.let { CountryFlagSource.FlagCdn(shape = it, format = flagFormat) } ?: CountryFlagSource.Emoji
    val style = base.copy(
        layout = layout.copy(
            listStyle = listStyle ?: layout.listStyle,
            flagStyle = flagStyle ?: layout.flagStyle,
            flagSource = flagSource,
            presentation = presentation ?: layout.presentation,
            quickPicks = quickPicks ?: layout.quickPicks,
            showRegionFilters = regionFilters ?: layout.showRegionFilters,
            showAlphabetIndex = alphabetIndex ?: layout.showAlphabetIndex,
            showDialCode = dialCodes ?: layout.showDialCode,
        ),
        haptics = if (haptics) base.haptics else CountryPickerHaptics.Off,
    )
    val current = style.layout
    val focusManager = LocalFocusManager.current

    CountryPickerTheme(style) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(style.colors.background)
                // A tap on the page itself dismisses the keyboard — iOS's number pad has no Done key.
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                // Keeps the focused field above the keyboard by shrinking the scroll viewport, rather
                // than letting the platform shift the whole window (see MainViewController on iOS).
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Hero(style)

            Panel(style) {
                OptionRow("Preset", Preset.entries, preset, { it.name }) { preset = it }
                if (preset == Preset.Signature || preset == Preset.Cupertino) {
                    AccentRow(selected = accent, onSelect = { accent = it })
                }
                OptionRow("Appearance", listOf(false, true), dark, { if (it) "Dark" else "Light" }, onDarkChange)
                OptionRow("Density", CountryPickerDensity.entries, density, { it.name }) { density = it }
                OptionRow("List", CountryListStyle.entries, current.listStyle, { it.name }) { listStyle = it }
                OptionRow("Flag art", FlagArt.entries, art, { it.label }) { flagArt = it }
                if (art != FlagArt.Emoji) {
                    OptionRow("Format", FlagImageFormat.entries, flagFormat, { it.name }) { flagFormat = it }
                }
                OptionRow("Flag frame", CountryFlagStyle.entries, current.flagStyle, { it.name }) { flagStyle = it }
                OptionRow("Opens as", PickerPresentation.entries, current.presentation, { it.name }) { presentation = it }
                OptionRow("Quick picks", QuickPicksStyle.entries, current.quickPicks, { it.name }) { quickPicks = it }
                ToggleRow(
                    "Extras",
                    listOf(
                        Triple("Region filters", current.showRegionFilters) { on: Boolean -> regionFilters = on },
                        Triple("A–Z rail", current.showAlphabetIndex) { on: Boolean -> alphabetIndex = on },
                        Triple("Dial codes", current.showDialCode) { on: Boolean -> dialCodes = on },
                        Triple("Haptics", haptics) { on: Boolean -> haptics = on },
                    ),
                )
            }

            ResidenceSection()
            PhoneSection()
            MarketsSection()
            VariantsSection()
            LocalizedNamesSection()
        }
    }
}

// ── Sections ──────────────────────────────────────────────────────────────────────────────────────

/** The everyday selector: detected from the device, with recents that survive a relaunch. */
@Composable
private fun ResidenceSection() = Section(
    title = "Country of residence",
    body = "Detected from the device on first launch and remembered afterwards. Search by name, " +
        "ISO code or dial code — and misspell one to see the suggestions.",
) {
    var country by rememberSaveableCountry(initialIso2 = null)
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        supportingContent = CountrySupportingContent.DialCode,
        required = true,
        supportingText = UiText.of("Where you pay tax"),
        recentCountryStore = rememberDefaultRecentCountryStore(),
        detector = rememberDefaultCountryDetector(),
    )
}

/**
 * A phone field for an SMS one-time-code flow.
 *
 * `allowedNumberTypes` is the point: a UK landline like `020 7946 0000` is a perfectly valid number
 * and completely useless here, and without the restriction the user would submit it and then wait
 * for a code that never arrives.
 */
@Composable
private fun PhoneSection() = Section(
    title = "Phone number",
    body = "Mobile numbers only. Try a UK landline (020 7946 0000), then a mobile (07400 123456). " +
        "Paste a +254… number to switch country.",
) {
    var size by rememberSaveable { mutableStateOf(PhoneFieldSize.Regular) }
    var value by remember { mutableStateOf<PhoneNumberValue?>(null) }
    val state = rememberPhoneNumberFieldState(
        initialCountry = DefaultCountryDataSource.findByIso2("GB", Locale.current)!!,
        allowedNumberTypes = PhoneNumberType.SmsCapable,
    )
    OptionRow("Size", PhoneFieldSize.entries, size, { it.name }) { size = it }
    // Autofill is on by default; a provider handing back "+44…" switches the country by itself.
    PhoneNumberField(
        onValueChange = { value = it },
        state = state,
        inputStyle = PhoneNumberInputDefaults.style(size = size),
        recentCountryStore = rememberDefaultRecentCountryStore(),
    )
    ResultCard(
        "e164" to (value?.e164Number ?: "—"),
        "international" to (value?.internationalNumber?.ifEmpty { null } ?: "—"),
        "type" to (value?.numberType?.name ?: "—"),
        "validity" to (value?.validity?.name ?: "—"),
    )
}

/** Multiple selection: chips in the field, a floating confirm bar in the sheet, and a limit. */
@Composable
private fun MarketsSection() = Section(
    title = "Several countries",
    body = "Up to five. The sheet keeps the picks pinned at the top and confirms them together.",
) {
    var markets by rememberSaveable(
        stateSaver = mapSaver(
            save = { set -> mapOf("iso" to set.joinToString(",") { it.iso2Code }) },
            restore = { saved ->
                (saved["iso"] as? String).orEmpty().split(',').filter(String::isNotBlank)
                    .mapNotNull { DefaultCountryDataSource.findByIso2(it) }.toSet()
            },
        ),
    ) { mutableStateOf(emptySet<Country>()) }
    MultiCountrySelector(
        selectedCountries = markets,
        onSelectionConfirmed = { markets = it },
        label = UiText.of("Launch markets"),
        supportingText = UiText.of("Pick up to 5"),
        config = CountryPickerDefaults.multiSelectConfig(
            minimumSelectionCount = 1,
            maximumSelectionCount = 5,
            suggestedCountryCodes = listOf("US", "GB", "DE", "KE", "JP"),
        ),
    )
}

/** Every selector footprint, sharing one country so they can be compared directly. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VariantsSection() = Section(
    title = "Footprints",
    body = "Full fields for forms, a card for settings, and pills for toolbars and phone rows.",
) {
    val fullWidth = CountrySelectorVariant.entries.filter { it.isFullWidth }
    var variant by rememberSaveable { mutableStateOf(CountrySelectorVariant.Elevated) }
    var country by rememberSaveableCountry(initialIso2 = "KE")

    OptionRow("Field", fullWidth, variant, { it.name }) { variant = it }
    CountrySelector(selectedCountry = country, onCountrySelected = { country = it }, variant = variant)
    PhoneNumberField(
        onValueChange = {},
        variant = variant,
        state = rememberPhoneNumberFieldState(initialCountry = DefaultCountryDataSource.findByIso2("KE")!!),
        showHelperText = false,
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CountrySelectorVariant.entries.filter { it.isPill }.forEach { pill ->
            CountrySelector(selectedCountry = country, onCountrySelected = { country = it }, variant = pill, label = null)
        }
    }
}

/**
 * Country names in the device's language.
 *
 * Nothing is configured for this — the picker localizes by default. The panel shows the two lookups
 * side by side because the difference between them is the one thing a host has to get right: seed a
 * selector from the plain `findByIso2` and the field reads "Germany" while the sheet it opens lists
 * "Allemagne".
 */
@Composable
private fun LocalizedNamesSection() {
    val locale = Locale.current
    Section(
        title = "Localized names",
        body = "On by default — device language ${locale.toLanguageTag()}. Switch languages in system " +
            "settings and reopen the sheet; it rebuilds.",
    ) {
        ResultCard(
            "findByIso2(DE, locale)" to (DefaultCountryDataSource.findByIso2("DE", locale)?.displayName ?: "—"),
            "findByIso2(DE)" to (DefaultCountryDataSource.findByIso2("DE")?.displayName ?: "—"),
            // Pinned to French so the difference shows even on an English device.
            "findByIso2(DE, fr)" to (DefaultCountryDataSource.findByIso2("DE", Locale("fr"))?.displayName ?: "—"),
        )
    }
}

// ── Building blocks ───────────────────────────────────────────────────────────────────────────────

@Composable
private fun Hero(style: CountryPickerStyle) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("KE", "JP", "BR", "DE", "CA", "IN").forEach { iso ->
                CountryFlag(country = DefaultCountryDataSource.findByIso2(iso), size = 34.dp)
            }
        }
        Text("Country Code Picker", style = style.typography.title, color = style.colors.textPrimary)
        Text(
            "Selectors, a country sheet and an international phone field for Compose Multiplatform. " +
                "Everything below follows the controls.",
            style = style.typography.subtitle,
            color = style.colors.textSecondary,
        )
    }
}

/** The control panel: a raised card of option rows. */
@Composable
private fun Panel(style: CountryPickerStyle, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(style.shapes.groupCornerRadius))
            .background(style.colors.surface)
            .border(BorderStroke(1.dp, style.colors.hairline), RoundedCornerShape(style.shapes.groupCornerRadius))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) { content() }
}

@Composable
private fun Section(title: String, body: String?, content: @Composable () -> Unit) {
    val style = CountryPickerTheme.style
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = style.typography.emptyTitle, color = style.colors.textPrimary)
            if (body != null) Text(body, style = style.typography.subtitle, color = style.colors.textSecondary)
        }
        content()
    }
}

/** A labelled, horizontally scrolling row of single-choice pills. */
@Composable
private fun <T> OptionRow(
    label: String,
    options: List<T>,
    selected: T,
    name: (T) -> String,
    onSelect: (T) -> Unit,
) {
    val style = CountryPickerTheme.style
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RowLabel(label, style)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            options.forEach { option ->
                Pill(
                    text = name(option),
                    selected = option == selected,
                    style = style,
                    modifier = Modifier.selectable(
                        selected = option == selected,
                        role = Role.RadioButton,
                        onClick = { onSelect(option) },
                    ),
                )
            }
        }
    }
}

/** A labelled row of independent on/off pills. */
@Composable
private fun ToggleRow(label: String, toggles: List<Triple<String, Boolean, (Boolean) -> Unit>>) {
    val style = CountryPickerTheme.style
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RowLabel(label, style)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            toggles.forEach { (text, on, onChange) ->
                Pill(
                    text = text,
                    selected = on,
                    style = style,
                    modifier = Modifier.toggleable(value = on, role = Role.Switch, onValueChange = onChange),
                )
            }
        }
    }
}

@Composable
private fun AccentRow(selected: Int, onSelect: (Int) -> Unit) {
    val style = CountryPickerTheme.style
    val fallback = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RowLabel("Accent", style)
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Accents.forEachIndexed { index, (name, color) ->
                val ring by animateColorAsState(if (index == selected) style.colors.textPrimary else Color.Transparent)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .selectable(selected = index == selected, role = Role.RadioButton, onClick = { onSelect(index) })
                        .semantics { contentDescription = name }
                        .border(2.dp, ring, CircleShape)
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(color ?: fallback),
                )
            }
        }
    }
}

@Composable
private fun Pill(text: String, selected: Boolean, style: CountryPickerStyle, modifier: Modifier) {
    val container by animateColorAsState(if (selected) style.colors.accent else style.colors.surfaceSunken)
    val content by animateColorAsState(if (selected) style.colors.onAccent else style.colors.textPrimary)
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(style.shapes.pill)
            .then(modifier)
            .background(container)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = style.typography.chip, color = content)
    }
}

@Composable
private fun RowLabel(text: String, style: CountryPickerStyle) {
    Text(
        text = text.uppercase(),
        style = style.typography.sectionLabel,
        color = style.colors.textTertiary,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

/** Key–value output in a quiet monospace card. */
@Composable
private fun ResultCard(vararg rows: Pair<String, String>) {
    val style = CountryPickerTheme.style
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(style.colors.surfaceSunken)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        rows.forEach { (key, value) ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(key, style = style.typography.caption.copy(fontFamily = FontFamily.Monospace), color = style.colors.textTertiary)
                Text(value, style = style.typography.caption.copy(fontFamily = FontFamily.Monospace), color = style.colors.textPrimary)
            }
        }
    }
}

/** A country that survives configuration changes, stored by ISO code and re-localized on restore. */
@Composable
private fun rememberSaveableCountry(initialIso2: String?) = Locale.current.let { locale ->
    rememberSaveable(
        stateSaver = mapSaver(
            save = { mapOf("iso" to it?.iso2Code) },
            restore = { DefaultCountryDataSource.findByIso2(it["iso"] as? String, locale) },
        ),
    ) {
        // The locale overload, so the initial value matches what the sheet will show.
        mutableStateOf(initialIso2?.let { DefaultCountryDataSource.findByIso2(it, locale) })
    }
}

package com.ezzy.ccp.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import com.ezzy.ccp.utils.countryToFlagEmoji

/**
 * Demonstrates the modern picker API, including the parts that are easy to miss.
 *
 * The sample exists as much for CI as for reading: it is the only place the library is exercised as
 * a real app, and a feature that is never called from here is a feature no build ever runs.
 */
@Composable
fun ModernFeaturesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // Keeps the focused field above the keyboard by shrinking the scroll viewport, rather
            // than letting the platform shift the whole window (see MainViewController on iOS).
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        LocalizedNamesSection()
        HorizontalDivider()
        SmsPhoneFieldSection()
        HorizontalDivider()
        AlphabetIndexSection()
        HorizontalDivider()
        SelectorVariantsSection()
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

    SectionHeader(
        title = "Localized country names",
        body = "On by default. Device language: ${locale.toLanguageTag()}. " +
            "Switch languages in system settings and reopen the sheet — it rebuilds.",
    )

    var country by rememberSaveable(
        stateSaver = androidx.compose.runtime.saveable.mapSaver(
            save = { mapOf("iso" to it?.iso2Code) },
            restore = { DefaultCountryDataSource.findByIso2(it["iso"] as? String, locale) },
        ),
    ) {
        // The locale overload, so the initial value matches what the sheet will show.
        mutableStateOf<Country?>(DefaultCountryDataSource.findByIso2("DE", locale))
    }

    CountrySelector(selectedCountry = country, onCountrySelected = { country = it })

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Mono("device    findByIso2(\"DE\", locale) → ${DefaultCountryDataSource.findByIso2("DE", locale)?.displayName}")
            Mono("canonical findByIso2(\"DE\")         → ${DefaultCountryDataSource.findByIso2("DE")?.displayName}")
            // Pinned to French so the difference is visible even on an English device, where the
            // two lines above are necessarily identical.
            Mono("fr        findByIso2(\"DE\", fr)     → ${DefaultCountryDataSource.findByIso2("DE", Locale("fr"))?.displayName}")
            Mono("emoji     \"DE\".countryToFlagEmoji() → ${"DE".countryToFlagEmoji()}")
        }
    }
}

/**
 * A phone field for an SMS one-time-code flow.
 *
 * `allowedNumberTypes` is the point: a UK landline like `020 7946 0000` is a perfectly valid number
 * and completely useless here, and without the restriction the user would submit it and then wait
 * for a code that never arrives.
 */
@Composable
private fun SmsPhoneFieldSection() {
    SectionHeader(
        title = "Phone field — mobile numbers only",
        body = "Try a UK landline (020 7946 0000) and a UK mobile (07400 123456). Both are valid " +
            "numbers; only one can receive an SMS.",
    )

    var value by remember { mutableStateOf<PhoneNumberValue?>(null) }
    val state = rememberPhoneNumberFieldState(
        initialCountry = DefaultCountryDataSource.findByIso2("GB")!!,
        allowedNumberTypes = PhoneNumberType.SmsCapable,
    )

    // Autofill is on by default; a provider handing back "+44…" switches the country by itself.
    PhoneNumberField(onValueChange = { value = it }, state = state)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Mono("e164       ${value?.e164Number ?: "—"}")
            Mono("numberType ${value?.numberType ?: "—"}")
            Mono("validity   ${value?.validity ?: "—"}")
            Mono("isValid    ${value?.isValid ?: false}")
        }
    }

    SectionHeader(title = "Compact sizes", body = null)
    PhoneFieldSize.entries.forEach { size ->
        PhoneNumberField(
            onValueChange = {},
            state = rememberPhoneNumberFieldState(
                initialCountry = DefaultCountryDataSource.findByIso2("KE")!!,
                initialNumber = "712345678",
            ),
            inputStyle = PhoneNumberInputDefaults.style(size = size),
        )
    }
}

/** The A–Z rail, which is off by default because it is only worth its space on the full list. */
@Composable
private fun AlphabetIndexSection() {
    SectionHeader(
        title = "Alphabet index",
        body = "Open the sheet and drag the A–Z rail on the right edge to scrub.",
    )

    var country by remember { mutableStateOf<Country?>(null) }
    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        config = CountryPickerConfig(showAlphabetIndex = true),
    )
}

/** Every selector variant, so the footprints can be compared directly. */
@Composable
private fun SelectorVariantsSection() {
    SectionHeader(title = "Selector variants", body = null)

    var variant by remember { mutableStateOf(CountrySelectorVariant.Outlined) }
    var country by remember { mutableStateOf(DefaultCountryDataSource.findByIso2("KE")) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        CountrySelectorVariant.entries.take(3).forEach { option ->
            FilterChip(
                selected = variant == option,
                onClick = { variant = option },
                label = { Text(option.name) },
            )
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        CountrySelectorVariant.entries.drop(3).forEach { option ->
            FilterChip(
                selected = variant == option,
                onClick = { variant = option },
                label = { Text(option.name) },
            )
        }
    }

    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        variant = variant,
    )
}

@Composable
private fun SectionHeader(title: String, body: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        if (body != null) {
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Mono(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
    )
}

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.detection.CountryDetectionBehavior
import com.ezzy.ccp.countrypicker.detection.DefaultCountryDetector
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.ui.CountryPickerSheet
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorField
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.ezzy.ccp.countrypicker.ui.DetectedCountryBadge
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import com.ezzy.ccp.countrypicker.ui.PickerIcons

/**
 * A demonstration screen combining most of the library's pieces into one form: legal name, country of
 * residence with detection, a phone number field, and the compact/flag-only selector variants.
 *
 * This mirrors the imported design's "Your details" screen. It is a **sample**, not part of the
 * library's architecture — a real app assembles its own screen from [CountrySelector],
 * [PhoneNumberField] and the rest; this file exists to show how those pieces fit together and to give
 * the library something concrete to preview.
 *
 * Nothing here is wired to navigation. [onBack] and [onContinue] are plain callbacks so this screen
 * stays usable in a preview, in a nav graph, or standalone.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourDetailsSampleScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onMoreOptions: () -> Unit = {},
    onContinue: (YourDetailsResult) -> Unit = {},
) {
    val context = LocalContext.current

    var legalName by rememberSaveable { mutableStateOf("Amara Otieno") }

    // Kenya as a sensible always-available fallback if device signals resolve to nothing.
    val fallbackCountry = remember { requireNotNull(DefaultCountryDataSource.findByIso2("KE")) }

    var residence by rememberSaveable(stateSaver = CountrySaver) { mutableStateOf<Country?>(null) }

    val residenceConfig = remember { CountryPickerDefaults.config() }
    val residencePickerState = rememberCountryPickerState(
        config = residenceConfig,
        selectedCountries = residence?.let(::setOf).orEmpty(),
        // Device signals only — SIM, network, locale. No location permission, matching the library's
        // detection contract.
        detector = remember { DefaultCountryDetector(context, fallbackIso2Code = fallbackCountry.iso2Code) },
        detectionBehavior = CountryDetectionBehavior.ShowBadge,
        onDetectedCountry = { residence = it },
    )

    val phoneState = rememberPhoneNumberFieldState(initialCountry = residence ?: fallbackCountry)

    fun clearAll() {
        legalName = ""
        residence = null
        residencePickerState.markExplicitSelection()
        phoneState.clear()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(PickerIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onMoreOptions) {
                        Icon(PickerIcons.MoreVert, contentDescription = "More options")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            Text(text = "Your details", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = legalName,
                onValueChange = { legalName = it },
                label = { Text("Legal name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))

            CountrySelectorField(
                country = residence,
                onClick = residencePickerState::open,
                isOpen = residencePickerState.isSheetOpen,
                label = UiText.of("Country of residence"),
                placeholder = UiText.of("Select country"),
                required = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // The design's "✓ Detected — From your network · tap to change" row. Suppressed once the
            // user has made an explicit choice — detection is a suggestion, never an override.
            residencePickerState.detectionSource?.let { source ->
                Spacer(Modifier.height(6.dp))
                DetectedCountryBadge(source = source, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(Modifier.height(20.dp))

            PhoneNumberField(
                state = phoneState,
                onValueChange = {},
                label = UiText.of("Phone number"),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))

            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Same selector, compact + flag-only",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CountrySelector(
                    selectedCountry = residence,
                    onCountrySelected = { residence = it },
                    config = residenceConfig,
                    variant = CountrySelectorVariant.Compact,
                    label = null,
                )
                CountrySelector(
                    selectedCountry = residence,
                    onCountrySelected = { residence = it },
                    config = residenceConfig,
                    variant = CountrySelectorVariant.FlagOnly,
                    label = null,
                )
            }

            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = ::clearAll) { Text("Clear") }
                Button(
                    onClick = {
                        onContinue(
                            YourDetailsResult(
                                legalName = legalName,
                                residence = residence,
                                phoneNumber = phoneState.value,
                            ),
                        )
                    },
                    modifier = Modifier.padding(start = 12.dp),
                ) { Text("Continue") }
            }
        }
    }

    if (residencePickerState.isSheetOpen) {
        CountryPickerSheet(
            state = residencePickerState,
            onCountrySelected = { country ->
                residence = country
                residencePickerState.markExplicitSelection()
            },
            onDismiss = residencePickerState::dismiss,
        )
    }
}

/** What the sample screen's Continue action hands back. */
data class YourDetailsResult(
    val legalName: String,
    val residence: Country?,
    val phoneNumber: com.ezzy.ccp.countrypicker.model.PhoneNumberValue,
)

/** Saves the selected country as an ISO code, resolving it back against the canonical dataset. */
private val CountrySaver = mapSaver<Country?>(
    save = { mapOf("iso2" to (it?.iso2Code ?: "")) },
    restore = { saved ->
        (saved["iso2"] as? String)
            ?.takeIf { it.isNotEmpty() }
            ?.let(DefaultCountryDataSource::findByIso2)
    },
)

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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.components.PhoneNumberInput
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.model.InputLabelMode
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.MultiCountrySelector
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import com.ezzy.ccp.model.CountryPickerStyle
import com.ezzy.ccp.model.SelectedCountry
import com.ezzy.ccp.sample.theme.CCPTheme
import com.ezzy.ccp.sample.theme.Wheat
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.countryToFlagEmoji

/**
 * The whole demo: a tour of the modern API, and the original kitchen-sink demo of the legacy
 * widgets — the same UI on Android (:app's MainActivity) and iOS (iosApp, via MainViewController).
 *
 * @param onDone Invoked when the legacy phone input's keyboard action fires on a valid number. Each
 *   platform shows its own confirmation (a Toast on Android).
 */
@Composable
fun SampleApp(onDone: () -> Unit = {}) {
    var formatedPhone2 by remember { mutableStateOf("") }
    var country by remember { mutableStateOf<Country?>(null) }
    var unFormatedPhone2 by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<SelectedCountry?>(null) }
    var valid2 by remember { mutableStateOf(false) }
    var setPhone by remember { mutableStateOf<String?>("") }
    val testSetPhones = listOf(
        "+254712345678",  // Kenya
        "+14155552671",   // USA
        "+447911123456",  // UK
        "+919876543210",  // India
        "+819012345678",  // Japan
        "+4915123456789", // Germany
        "+33612345678",   // France
        "+61412345678",   // Australia
        "+5521987654321", // Brazil
        "+27821234567",   // South Africa
        "+34612345678",   // Spain
        "+393471234567",  // Italy
        "+79123456789",   // Russia
        "+8613800138000", // China
        "+971501234567",  // UAE
        "+966551234567",  // Saudi Arabia
        "+201001234567",  // Egypt
        "+48600123456",   // Poland
        "+46701234567",   // Sweden
        "+639123456789"   // Philippines
    )

    val testPhones2 = listOf(
        "254712345678",  // Kenya
        "14155552671",   // USA
        "447911123456",  // UK
        "919876543210",  // India
        "819012345678",  // Japan
        "4915123456789", // Germany
        "33612345678",   // France
        "61412345678",   // Australia
        "5521987654321", // Brazil
        "27821234567",   // South Africa
        "34612345678",   // Spain
        "393471234567",  // Italy
        "79123456789",   // Russia
        "8613800138000", // China
        "971501234567",  // UAE
        "966551234567",  // Saudi Arabia
        "201001234567",  // Egypt
        "48600123456",   // Poland
        "46701234567",   // Sweden
        "639123456789"   // Philippines
    )

    var selectedCountries by rememberSaveable {
        mutableStateOf(emptySet<Country>())
    }
    val kenya = Country(
        iso2Code = "KE",
        iso3Code = "KEN",
        displayName = "Kenya",
        dialCode = "+254",
        flag = "🇰🇪",
        region = CountryRegion.Africa
    )
    val phoneState = rememberPhoneNumberFieldState(
        initialCountry = kenya,
    )
    // The sample is split in two: a tour of the modern API, and the original kitchen-sink
    // demo of the legacy widgets that is kept so the legacy path stays exercised by a real
    // app build rather than only by tests.
    var tab by rememberSaveable { mutableIntStateOf(0) }

    CCPTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // enableEdgeToEdge() draws behind the system bars, so the tab row has to
                // inset itself or it renders underneath the status bar.
                TabRow(
                    selectedTabIndex = tab,
                    modifier = Modifier.statusBarsPadding(),
                ) {
                    Tab(
                        selected = tab == 0,
                        onClick = { tab = 0 },
                        text = { Text("Modern API") },
                    )
                    Tab(
                        selected = tab == 1,
                        onClick = { tab = 1 },
                        text = { Text("Legacy demo") },
                    )
                }
            },
        ) { innerPadding ->
            if (tab == 0) {
                ModernFeaturesScreen(modifier = Modifier.padding(innerPadding))
                return@Scaffold
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Wheat)
                    .padding(innerPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    MultiCountrySelector(
                        selectedCountries = selectedCountries,
                        onSelectionConfirmed = { selectedCountries = it },
                        config = CountryPickerDefaults.multiSelectConfig(
                            minimumSelectionCount = 1,
                            maximumSelectionCount = 5,
                        ),
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    PhoneNumberField(
                        state = phoneState,
                        onValueChange = { value ->
                            value.e164Number
                            value.internationalNumber
                            value.formattedNationalNumber
                            value.validity
                            value.isValid
                        },
                        inputStyle = PhoneNumberInputDefaults.style(
                            size = PhoneFieldSize.Compact,       // or ExtraCompact — scales padding, flag,
                            // chevron, icon buttons and font size together
                            showPrefixDivider = false,           // hides the vertical divider entirely
                            labelMode = InputLabelMode.Hidden,   // hides the visible label (still exposed
                            // to accessibility via accessibilityLabel)
                        )
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    CountrySelector(
                        selectedCountry = country,
                        onCountrySelected = { country = it },
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    PhoneNumberInput(
                        value = setPhone.toString(),
                        onDone = onDone,
                        onValueChange = { (formattedPhone, phoneNumber, country, isValid) ->
                            formatedPhone2 = formattedPhone
                            unFormatedPhone2 = phoneNumber
                            valid2 = isValid
                            selectedCountry = country
                        },
                        ccpConfig = CCPDefaults.defaultConfig(
                            showCountriesHeaderDivider = true,
                            autoDetectCountry = true,
                            phoneInputShape = RoundedCornerShape(8.dp),
                            searchCornerRadius = 10.dp,
                            searchBorderWidth = 1.dp,
                            readOnly = false,
                            countriesSheetShape = RectangleShape,
                            countryPickerStyle = CountryPickerStyle.BottomSheet,
                            showPhonePrefixDivider = false,
                            showLabel = true,
                            phoneFieldSize = PhoneFieldSize.Compact
                        ),
                        colors = CCPDefaults.colors(
                            ccpSheetColor = CCPDefaults.ccpSheetColor(
                                searchBorderColor = Color.Black.copy(alpha = .2f)
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Formated Phone: $formatedPhone2",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "UnFormated Phone: $unFormatedPhone2",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Country: ${selectedCountry?.name}",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Country Code: ${selectedCountry?.code}",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Country Dial Code: ${selectedCountry?.dialCode}",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        // The legacy Country.flag ImageVector is deprecated and always
                        // null; the emoji comes from the ISO code.
                        text = "Country Flag: ${selectedCountry?.code?.countryToFlagEmoji()}",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    val validText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black
                            )
                        ) {
                            append("Valid: ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = if (valid2) Color.Green else Color.Red
                            )
                        ) {
                            append(valid2.toString())
                        }
                    }
                    Text(
                        text = validText
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(onClick = {
                        setPhone = testSetPhones.random()
                    }) {
                        Text("Set Random Country Numbers")
                    }
                }
            }
        }
    }
}

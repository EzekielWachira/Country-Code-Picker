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

import android.content.Context
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezzy.ccp.PhoneViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.Country.Companion.toSelectedCountry
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.countryToFlagEmoji
import java.util.Locale

/**
 * A highly customizable international phone number input component with country selection.
 *
 * This composable provides:
 * - A country selector (with flag and dial code)
 * - A phone number input field that formats the number in real time
 * - Automatic validation based on the selected country
 * - Automatic country detection (via SIM, network, or locale) if enabled
 *
 * Features:
 * - Formats phone numbers as the user types according to the selected country
 * - Provides both formatted and unformatted values to the parent
 * - Allows restricting the list of selectable countries
 * - Supports overriding the detected country with a preselected value
 * - Handles "Done" keyboard action for submission
 *
 * @param modifier Modifier to be applied to the input container.
 * @param phoneHint Placeholder text shown when the input field is empty.
 * @param onPhoneValueChange (Deprecated – use [onValueChange]) Callback invoked whenever the
 * phone number changes. Provides:
 *  - [formatedPhone]: The number in international format (e.g., "+1 415-555-2671")
 *  - [unFormatedPhone]: The number in E.164 format (e.g., "+14155552671")
 *  - [valid]: Whether the number is valid for the selected country
 * @param onValueChange Callback invoked whenever the phone number changes. Provides a [Phone]
 * object containing:
 *  - [formattedPhone]: Internationally formatted phone number
 *  - [phoneNumber]: E.164 number
 *  - [isValid]: Validation result
 *  - [country]: Selected country info
 * @param value Optional initial phone number to prefill the field.
 * Can be in E.164 format (e.g., "+14155552671") or local format (will be parsed).
 * @param setCountry Optional country code (e.g., "US") or country name (e.g., "United States")
 * to preselect. If provided, overrides auto detection.
 * @param countriesToShow Optional whitelist of country codes (e.g., listOf("US", "GB", "KE"))
 * to restrict the countries shown in the selector. Empty = all countries.
 * @param colors Styling configuration for container, border, text, cursor, and hint colors.
 * @param ccpConfig Configuration for behavior and UI of the country code picker (e.g.,
 * corner radius, borders, shapes, autoDetectCountry, etc.).
 * @param onDone Callback invoked when the keyboard "Done" action is pressed. Called only if the
 * current phone number is valid.
 *
 * ### Country auto-detection:
 * If [ccpConfig.autoDetectCountry] is true and [setCountry] is not provided, the country is
 * determined in this priority:
 * 1. SIM country (from TelephonyManager.simCountryIso)
 * 2. Network country (from TelephonyManager.networkCountryIso)
 * 3. Device locale (Locale.getDefault().country)
 * 4. Fallback to "US"
 *
 * ### Example:
 * ```
 * PhoneNumberInput(
 *   value = "+254712345678",
 *   setCountry = "KE",
 *   onValueChange = { phone ->
 *       if (phone.isValid) {
 *           submitPhone(phone.phoneNumber) // E.164 number
 *       }
 *   }
 * )
 * ```
 */

@Composable
fun PhoneNumberInput(
    modifier: Modifier = Modifier,
    phoneHint: String = "Enter phone",
    onPhoneValueChange: (formatedPhone: String, unFormatedPhone: String, valid: Boolean) -> Unit = { _, _, _ -> },
    onValueChange: (Phone) -> Unit = {},
    value: String = "",
    setCountry: String? = null,
    countriesToShow: List<String> = emptyList(),
    colors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig(),
    onDone: () -> Unit = {}
) {
    val viewModel: PhoneViewModel = viewModel()
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
    val simCountry = telephonyManager?.simCountryIso?.lowercase()?.ifEmpty { "us" }
    val keyboardController = LocalSoftwareKeyboardController.current
    val phoneField by viewModel.phoneField.collectAsStateWithLifecycle()

    // Observe state from ViewModel
    val selectedCountry by viewModel.activeCountry.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val formattedPhone by viewModel.formattedPhone.collectAsStateWithLifecycle()
    val unformattedPhone by viewModel.unformattedPhone.collectAsStateWithLifecycle()
    val isValid by viewModel.isValid.collectAsStateWithLifecycle()

    // Initialize country based on props
    LaunchedEffect(setCountry, ccpConfig.autoDetectCountry, simCountry) {
        if (!setCountry.isNullOrEmpty()) {
            viewModel.setActiveCountry(
                countryList.find {
                    it.code.lowercase() == setCountry.lowercase() ||
                            it.name.lowercase() == setCountry.lowercase()
                } ?: countryList.find { it.code == "US" }!!
            )
        } else if (ccpConfig.autoDetectCountry) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val simCountry = telephonyManager?.simCountryIso?.lowercase()
            val networkCountry = telephonyManager?.networkCountryIso?.lowercase()
            val localeCountry = Locale.getDefault().country.lowercase()

            // Prefer SIM country → fallback to network → fallback to locale → fallback to US
            val detectedCountry = when {
                !simCountry.isNullOrEmpty() -> simCountry
                !networkCountry.isNullOrEmpty() -> networkCountry
                else -> localeCountry.ifEmpty { "us" }
            }

            viewModel.setCountryForLocale(detectedCountry)
        } else {
            viewModel.setCountryForLocale("us")
        }
    }

    // Initialize phone number if provided
    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            viewModel.parseAndSetPhoneNumber(value)
        }
    }

    // Notify parent of changes
    LaunchedEffect(formattedPhone, unformattedPhone, isValid) {
        onValueChange(
            Phone(
                formattedPhone = formattedPhone,
                phoneNumber = unformattedPhone,
                isValid = isValid,
                country = selectedCountry?.toSelectedCountry(),
            )
        )
        onPhoneValueChange(formattedPhone, unformattedPhone, isValid)
    }

    Surface(
        modifier = modifier,
        shape = ccpConfig.phoneInputShape,
        color = colors.containerColor,
        border = BorderStroke(
            width = ccpConfig.borderWidth,
            color = colors.borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            SelectedCountryComponent(
                selectedCountry = selectedCountry,
                onSelectCountry = { country ->
                    viewModel.setActiveCountry(country)
                },
                countriesToShow = countriesToShow,
                ccpColors = colors,
                ccpConfig = ccpConfig
            )
            BasicTextField(
                value = phoneField,
                onValueChange = { newValue ->
                    viewModel.updatePhoneField(newValue)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.inputTextColor),
                cursorBrush = SolidColor(colors.cursorColor),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(1f),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (phoneNumber.isEmpty()) {
                            Text(
                                text = phoneHint,
                                style = ccpConfig.phoneHintStyle,
                                color = colors.phoneHintColor
                            )
                        }
                        innerTextField()
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isValid) {
                            context.showToast("Invalid phone number")
                            return@KeyboardActions
                        }
                        keyboardController?.hide()
                        onDone()
                    }
                )
            )
        }
    }
}

/**
 * A composable that displays the selected country with its flag and dial code.
 *
 * This component shows the currently selected country and opens a bottom sheet for country
 * selection when clicked. It automatically initializes with the device's current locale.
 *
 * @param modifier Modifier to be applied to the component
 * @param selectedCountry The currently selected country; defaults to US if null
 * @param viewModel ViewModel that manages country selection state
 * @param onSelectCountry Callback that is triggered when a new country is selected
 * @param searchTextColor Color of the search text in the country selection bottom sheet
 * @param countriesToShow Optional list of country codes to filter the available countries in the bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedCountryComponent(
    modifier: Modifier = Modifier,
    selectedCountry: Country? = countryList.find { it.code == "US" },
    viewModel: PhoneViewModel = viewModel(),
    onSelectCountry: (Country) -> Unit = {},
    countriesToShow: List<String> = emptyList(), // listOf(US, UK, FR, KE ...etc)
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {

    var isCountryBottomSheetVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(true)
    LaunchedEffect(true) {
        val countryCodeLocale = context.resources.configuration.locales[0].country
        viewModel.setCountryForLocale(countryCodeLocale)
    }

    Surface(
        modifier = modifier, onClick = {
            isCountryBottomSheetVisible = true
        },
        shape = ccpConfig.phoneInputShape,
        color = ccpColors.containerColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp, vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ccpConfig.showCountryFlag)
                Text(
                    text = (selectedCountry?.code ?: "US").countryToFlagEmoji() ?: "",
                    fontSize = 18.sp,
                )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = selectedCountry?.dialCode ?: "00",
                style = MaterialTheme.typography.bodyMedium,
                color = ccpColors.countryCodeTextColor
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = EzzyIcons.ChevronDown,
                contentDescription = "down arrow",
                tint = ccpColors.countryChevronColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (isCountryBottomSheetVisible) {
        CountriesBottomSheet(
            sheetState = sheetState,
            onSelectCountries = {
                isCountryBottomSheetVisible = false
                onSelectCountry(it)
            },
            onDismiss = {
                isCountryBottomSheetVisible = false
            },
            countriesToShow = countriesToShow,
            ccpColors = ccpColors,
            ccpConfig = ccpConfig
        )
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@Preview
@Composable
private fun PhoneFieldComponentPreview() {
    PhoneNumberInput()
}

@Preview
@Composable
private fun SelectedCountryComponentPreview() {
    SelectedCountryComponent(selectedCountry = countryList[0])
}
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
 * A customizable international phone number input component.
 *
 * This composable displays a phone input field with a country selector, automatically
 * formatting the phone number based on the selected country's pattern and validating it.
 *
 * @param modifier Modifier to be applied to the component.
 * @param containerColor Background color of the input container.
 * @param cornerRadius Corner radius of the input container.
 * @param phoneHint Placeholder text shown when the input field is empty.
 * @param phoneHintColor Color of the placeholder text.
 * @param phoneHintStyle TextStyle for the placeholder text.
 * @param onPhoneValueChange Callback invoked when the phone number value changes. It provides
 * the formatted phone number (e.g., "+1 415-555-2671"), the unformatted phone number
 * (e.g., "4155552671"), and a boolean indicating if the number is valid according to the
 * selected country's rules.
 * @param borderWidth Width of the border around the input container.
 * @param borderColor Color of the border around the input container.
 * @param cursorColor Color of the input cursor.
 * @param inputTextColor Color of the input text.
 * @param value Optional pre-set phone number to initialize the field with. Should ideally be in
 * E.164 format (e.g., "+14155552671") but can also accept local numbers which will be
 * formatted based on the `setCountry` or detected country.
 * @param setCountry Optional country code (e.g., "US") or name (e.g., "United States") to
 * preselect. Overrides `autoDetectCountry`.
 * @param countriesToShow Optional list of country codes (e.g., listOf("US", "GB", "KE")) to
 * filter the countries available in the country selector bottom sheet. If empty, all
 * countries are shown.
 * @param autoDetectCountry If true and `setCountry` is not provided, attempts to automatically
 * detect the country from the device's SIM card. Defaults to "US" if detection fails.
 * @param showHeader Whether to show the header (title and search bar) in the country selection
 * bottom sheet.
 * @param showCountryFlag Whether to show the country flag emoji in the country selector
 * component next to the dial code.
 * @param onDone Callback invoked when the "Done" action is triggered on the keyboard (e.g., user
 * presses the checkmark). Typically used to submit the form or navigate away. Only called
 * if the phone number is valid.
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
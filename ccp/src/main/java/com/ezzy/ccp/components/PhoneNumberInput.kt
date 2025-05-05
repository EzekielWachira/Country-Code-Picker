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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezzy.ccp.PhoneViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.utils.countryToFlagEmoji
import com.ezzy.ccp.utils.formatAndValidatePhone
import com.ezzy.ccp.utils.parsePhoneNumber

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
    containerColor: Color = Color.White,
    cornerRadius: Dp = 16.dp,
    phoneHint: String = "Enter phone",
    phoneHintColor: Color = Color.Black,
    phoneHintStyle: TextStyle = TextStyle.Default,
    onPhoneValueChange: (formatedPhone: String, unFormatedPhone: String, valid: Boolean) -> Unit = { _, _, _ -> },
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    cursorColor: Color = Color.Black,
    inputTextColor: Color = Color.Black,
    value: String = "",
    setCountry: String? = null, // US/United States, KE/Kenya, FR/France...,
    countriesToShow: List<String> = emptyList(), // listOf(US, UK, FR, KE ...etc)
    autoDetectCountry: Boolean = false,
    showHeader: Boolean = true,
    showCountryFlag: Boolean = true,
    onDone: () -> Unit = {}
) {

    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
    val simCountry = telephonyManager?.simCountryIso?.lowercase()?.ifEmpty { "us" }
    val keyboardController = LocalSoftwareKeyboardController.current

    /**
     * Initialize the selected country based on the following priorities:
     * 1. If [setCountry] is provided, use it as country code or name
     * 2. If [autoDetectCountry] is true, use the device's SIM country
     * 3. Default to "us" (United States) otherwise
     */
    var selectedCountry by remember {
        mutableStateOf<Country?>(
            countryList.find { country ->
                if (!setCountry.isNullOrEmpty()) {
                    country.code.lowercase() == setCountry.lowercase() ||
                            country.name.lowercase() == setCountry.lowercase()
                } else {
                    if (autoDetectCountry) {
                        country.code.lowercase() == (simCountry ?: "us")
                    } else
                        country.code.lowercase() == "us"
                }
            }
        )
    }

    // Track if input is coming from user typing or from prop
    var isUserTyping by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue(value)) }

    // Keep track of the last value received from props to avoid loops
    var lastPropValue by remember { mutableStateOf(value) }
    val unitedStates by remember { mutableStateOf(countryList.find { it.code.lowercase() == "us" }) }

//    var userSelectedCountry by remember { mutableStateOf(false) }


    // Remove the separate LaunchedEffect blocks and use one combined block
    LaunchedEffect(value, Unit) {
        if (value.isNotEmpty() && (!isUserTyping || phoneNumber.text.isEmpty())) {
            // Only update lastPropValue if this isn't the initial formatting
            if (phoneNumber.text.isNotEmpty()) {
                lastPropValue = value
            }

            // Only try to parse complete numbers (with country code)
            if (value.startsWith("+") || value.length >= 8) {
                val (country, localNumber) = parsePhoneNumber(value)
                if (country != null) {
                    selectedCountry = country

                    // Immediately notify parent with the formatted value
                    val (formattedPhone, unformattedPhone, formattedWithoutCountryCode, isValid) =
                        formatAndValidatePhone(
                            localNumber,
                            country.code
                        )
                    phoneNumber =
                        TextFieldValue(
                            formattedWithoutCountryCode,
                            selection = TextRange(formattedWithoutCountryCode.length)
                        )
                    onPhoneValueChange(formattedPhone, unformattedPhone, isValid)
                } else {
                    val dialCode = selectedCountry?.dialCode ?: unitedStates?.dialCode
                    val formattedNumber =
                        if (value.startsWith(dialCode.toString())) value else "$dialCode$value"
                    val (formattedPhone, unformattedPhone, formattedWithoutCountryCode, isValid) =
                        formatAndValidatePhone(
                            formattedNumber,
                            selectedCountry?.code ?: "US"
                        )

                    phoneNumber = TextFieldValue(
                        formattedWithoutCountryCode,
                        selection = TextRange(formattedWithoutCountryCode.length)
                    )
                    // Immediately notify parent with the formatted value
                    onPhoneValueChange(formattedPhone, unformattedPhone, isValid)
                }
            } else {
                // For short inputs, format with selected country
                val countryCode = selectedCountry?.code ?: "US"
                val dialCode = selectedCountry?.dialCode ?: unitedStates?.dialCode

                val formattedNumber = if (value.length >= 5) {
                    "$dialCode$value"
                } else {
                    value
                }
                val (formattedPhone, unformattedPhone, _, isValid) =
                    formatAndValidatePhone(formattedNumber, countryCode)

                // Use the formatted version for display
                phoneNumber = TextFieldValue(value, selection = TextRange(value.length))

                // Only call callback with formatted number if length >= 5
                if (value.length >= 5) {
                    onPhoneValueChange(formattedPhone, unformattedPhone, isValid)
                } else {
                    // For very short inputs, pass the raw value
                    onPhoneValueChange(value, value, false)
                }
            }
        }
    }

    val phoneState by remember {
        derivedStateOf {
            formatAndValidatePhone(phoneNumber.text, selectedCountry?.code ?: "US")
        }
    }
    val (formattedPhone, unformattedPhone, formattedWithoutCountryCode, isValid) = phoneState

    // Format the phone number as the user types
    LaunchedEffect(formattedWithoutCountryCode) {
        if (isUserTyping) {
            // Get current cursor position relative to the formatted text
            val cursorPosition = phoneNumber.selection.start.coerceIn(0, phoneNumber.text.length)
            val oldText = phoneNumber.text

            // Only update if the formatted text is different
            if (formattedWithoutCountryCode != oldText) {
                // Calculate new cursor position
                val newCursorPos = when {
                    // Handle backspace case (keep cursor at same position)
                    oldText.length > formattedWithoutCountryCode.length -> {
                        cursorPosition.coerceAtMost(formattedWithoutCountryCode.length)
                    }
                    // Handle adding characters (move cursor to end of the added/modified section)
                    cursorPosition == oldText.length -> formattedWithoutCountryCode.length
                    // Handle cursor in middle of text
                    else -> {
                        val diff = formattedWithoutCountryCode.length - oldText.length
                        (cursorPosition + diff).coerceIn(0, formattedWithoutCountryCode.length)
                    }
                }

                phoneNumber = phoneNumber.copy(
                    text = formattedWithoutCountryCode,
                    selection = TextRange(newCursorPos)
                )
            }

            // Only update if the number has actually changed to avoid loops
            if (lastPropValue != formattedPhone) {
                onPhoneValueChange(formattedPhone, unformattedPhone, isValid)
                lastPropValue = formattedPhone
            }

            // Reset the flag after processing
            isUserTyping = false
        }
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
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
                    selectedCountry = country
                    // When country changes, reformat the existing number
                    // This will trigger the LaunchedEffect for formattedWithoutCountryCode
                    isUserTyping = true
                },
                countriesToShow = countriesToShow,
                showHeader = showHeader,
                showCountryFlag = showCountryFlag
            )

            BasicTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    isUserTyping = true
                    phoneNumber = newValue
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = inputTextColor),
                cursorBrush = SolidColor(cursorColor),
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
                        if (phoneNumber.text.isEmpty()) {
                            Text(
                                text = phoneHint,
                                style = phoneHintStyle,
                                color = phoneHintColor
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

                        keyboardController?.hide() // Hide keyboard
                        onDone() // Call your success logic
                    }
                )
            )

//            TextField(
//                value = phoneNumber, // Use state variable
//                onValueChange = { newValue ->
//                    isUserTyping = true
//                    // Use the new value directly to respect cursor positioning
//                    phoneNumber = newValue
//                },
//                placeholder = {
//                    Text(
//                        text = phoneHint,
//                        style = phoneHintStyle,
//                        color = phoneHintColor
//                    )
//                },
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    disabledContainerColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent,
//                    cursorColor = cursorColor,
//                    focusedTextColor = inputTextColor,
//                ),
//                modifier = Modifier
//                    .weight(1f)
//                    ,
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Phone,
//                    imeAction = ImeAction.Go
//                ),
//                singleLine = true,
//                textStyle = MaterialTheme.typography.bodyLarge,
//                readOnly = false
//            )
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
    searchTextColor: Color = Color.Black,
    countriesToShow: List<String> = emptyList(), // listOf(US, UK, FR, KE ...etc)
    showHeader: Boolean = true,
    showCountryFlag: Boolean = true
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
        }, shape = MaterialTheme.shapes.small, color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp, vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
//            Icon(
//                imageVector = selectedCountry?.flag ?: EzzyIcons.UnitedStates,
//                contentDescription = selectedCountry?.name + " flag",
//                tint = Color.Unspecified,
//                modifier = Modifier.size(20.dp)
//            )

            if (showCountryFlag)
                Text(
                    text = (selectedCountry?.code ?: "US").countryToFlagEmoji() ?: "",
                    fontSize = 18.sp
                )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = selectedCountry?.dialCode ?: "00",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = EzzyIcons.ChevronDown,
                contentDescription = "down arrow",
                tint = Color.Black.copy(alpha = .5f),
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
            }, cornerRadius = 0.dp,
            searchTextColor = searchTextColor,
            onDismiss = {
                isCountryBottomSheetVisible = false
            },
            countriesToShow = countriesToShow,
            showHeader = showHeader
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
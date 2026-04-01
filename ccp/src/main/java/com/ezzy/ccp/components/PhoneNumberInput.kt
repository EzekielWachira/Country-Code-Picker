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
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.state.PhoneState
import com.ezzy.ccp.state.rememberPhoneState
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.CountryDetector
import com.ezzy.ccp.utils.countryToFlagEmoji

/**
 * A highly customizable international phone number input component with country selection.
 *
 * Delegates all state management to [PhoneState] and country detection to [CountryDetector],
 * keeping this composable focused purely on layout and user interaction.
 *
 * @param modifier Modifier applied to the input container.
 * @param phoneHint Placeholder text shown when the input field is empty.
 * @param onPhoneValueChange Deprecated – use [onValueChange].
 * @param onValueChange Callback invoked on every change, providing a [Phone] with
 * formattedPhone, phoneNumber (E.164), isValid, and country.
 * @param value Optional initial phone number (E.164 or local format).
 * @param setCountry Optional country code (e.g. "US") or name to preselect.
 * @param countriesToShow Whitelist of country codes shown in the selector. Empty = all.
 * @param colors Color configuration.
 * @param ccpConfig Behavior and UI configuration.
 * @param onDone Called when the keyboard "Done" action is pressed and the number is valid.
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
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = rememberPhoneState()

    LaunchedEffect(setCountry, ccpConfig.autoDetectCountry) {
        when {
            !setCountry.isNullOrEmpty() -> {
                val found = countryList.find {
                    it.code.equals(setCountry, ignoreCase = true) ||
                        it.name.equals(setCountry, ignoreCase = true)
                } ?: countryList.find { it.code == "US" }!!
                state.selectCountry(found)
            }
            ccpConfig.autoDetectCountry -> state.setCountryByCode(CountryDetector.detect(context))
            else -> state.setCountryByCode("US")
        }
    }

    LaunchedEffect(value) {
        if (value.isNotEmpty()) state.parseAndSet(value)
    }

    LaunchedEffect(state.formattedPhone, state.unformattedPhone, state.isValid) {
        val phone = state.toPhone()
        onValueChange(phone)
        onPhoneValueChange(phone.formattedPhone, phone.phoneNumber, phone.isValid)
    }

    Surface(
        modifier = modifier,
        shape = ccpConfig.phoneInputShape,
        color = colors.containerColor,
        border = BorderStroke(width = ccpConfig.borderWidth, color = colors.borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            SelectedCountryComponent(
                selectedCountry = state.activeCountry,
                onSelectCountry = state::selectCountry,
                countriesToShow = countriesToShow,
                ccpColors = colors,
                ccpConfig = ccpConfig
            )
            BasicTextField(
                value = state.phoneField,
                onValueChange = state::updatePhoneNumber,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.inputTextColor),
                cursorBrush = SolidColor(colors.cursorColor),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(1f),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (state.phoneNumber.isEmpty()) {
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
                        if (!state.isValid) {
                            context.showToast("Invalid phone number")
                            return@KeyboardActions
                        }
                        keyboardController?.hide()
                        onDone()
                    }
                ),
                readOnly = ccpConfig.readOnly
            )
        }
    }
}

/**
 * Displays the currently selected country (flag + dial code) and opens a [CountriesBottomSheet]
 * when tapped.
 *
 * @param modifier Modifier applied to the surface.
 * @param selectedCountry Country to display; falls back to US if null.
 * @param onSelectCountry Triggered when the user picks a country.
 * @param countriesToShow Whitelist of country codes shown in the sheet.
 * @param ccpColors Color configuration.
 * @param ccpConfig Behavior and UI configuration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedCountryComponent(
    modifier: Modifier = Modifier,
    selectedCountry: Country? = countryList.find { it.code == "US" },
    onSelectCountry: (Country) -> Unit = {},
    countriesToShow: List<String> = emptyList(),
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        modifier = modifier,
        onClick = { isSheetVisible = true },
        shape = ccpConfig.phoneInputShape,
        color = ccpColors.containerColor,
        enabled = !ccpConfig.readOnly
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ccpConfig.showCountryFlag) {
                Text(
                    text = (selectedCountry?.code ?: "US").countryToFlagEmoji() ?: "",
                    fontSize = 18.sp,
                )
            }
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

    if (isSheetVisible) {
        CountriesBottomSheet(
            sheetState = sheetState,
            onSelectCountries = {
                isSheetVisible = false
                onSelectCountry(it)
            },
            onDismiss = { isSheetVisible = false },
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

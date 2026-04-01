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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.CountryPickerStyle
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.state.PhoneState
import com.ezzy.ccp.state.rememberPhoneState
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.CountryDetector
import com.ezzy.ccp.utils.countryToFlagEmoji
import com.ezzy.ccp.utils.getMaxPhoneLength

/**
 * A highly customizable international phone number input component with country selection.
 *
 * State is managed by [PhoneState], which can be hoisted to the caller for external control:
 * ```
 * val phoneState = rememberPhoneState()
 * PhoneNumberInput(state = phoneState, ...)
 * // Elsewhere: phoneState.clearPhone(), phoneState.isValid, phoneState.toPhone()
 * ```
 *
 * @param modifier Modifier applied to the outermost layout (Column containing field + error text).
 * @param state Hoistable state holder. Defaults to an internal [rememberPhoneState].
 * @param phoneHint Placeholder text shown when the input is empty.
 * @param onPhoneValueChange Deprecated – use [onValueChange].
 * @param onValueChange Callback invoked on every change, providing a [Phone] snapshot.
 * @param value Optional initial phone number (E.164 or local format).
 * @param setCountry Optional ISO code or country name to preselect (e.g. "KE", "Kenya").
 * @param countriesToShow Whitelist of ISO codes shown in the country selector. Empty = all.
 * @param countriesExclude Blacklist of ISO codes hidden from the country selector.
 * @param pinnedCountries ISO codes pinned to a "Suggested" section at the top of the sheet.
 * @param isError Whether to show the error border and [errorMessage].
 * @param errorMessage Text displayed below the field when [isError] is true.
 * @param colors Color configuration.
 * @param ccpConfig Behavior and UI configuration.
 * @param onDone Called when the keyboard "Done" action is pressed and the number is valid.
 */
@Composable
fun PhoneNumberInput(
    modifier: Modifier = Modifier,
    state: PhoneState = rememberPhoneState(),
    phoneHint: String = "Enter phone",
    onPhoneValueChange: (formatedPhone: String, unFormatedPhone: String, valid: Boolean) -> Unit = { _, _, _ -> },
    onValueChange: (Phone) -> Unit = {},
    value: String = "",
    setCountry: String? = null,
    countriesToShow: List<String> = emptyList(),
    countriesExclude: List<String> = emptyList(),
    pinnedCountries: List<String> = emptyList(),
    isError: Boolean = false,
    errorMessage: String? = null,
    colors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig(),
    onDone: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

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

    val effectiveBorderColor = if (isError) colors.errorBorderColor else colors.borderColor
    // Always show at least a 1dp border in error state so it's visible even when borderWidth = 0
    val effectiveBorderWidth = if (isError && ccpConfig.borderWidth == 0.dp) 1.dp else ccpConfig.borderWidth

    var dropdownExpanded by remember { mutableStateOf(false) }
    var boxWidthPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth().onSizeChanged { boxWidthPx = it.width }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ccpConfig.phoneInputShape,
                color = colors.containerColor,
                border = BorderStroke(width = effectiveBorderWidth, color = effectiveBorderColor)
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
                        countriesExclude = countriesExclude,
                        pinnedCountries = pinnedCountries,
                        ccpColors = colors,
                        ccpConfig = ccpConfig,
                        onDropdownExpand = if (ccpConfig.countryPickerStyle == CountryPickerStyle.Dropdown) {
                            { dropdownExpanded = true }
                        } else null
                    )
                    BasicTextField(
                        value = state.phoneField,
                        onValueChange = { newValue ->
                            if (ccpConfig.enforceMaxLength) {
                                val maxLen = getMaxPhoneLength(state.activeCountry?.code ?: "US")
                                if (newValue.text.filter { it.isDigit() }.length <= maxLen) {
                                    state.updatePhoneNumber(newValue)
                                }
                            } else {
                                state.updatePhoneNumber(newValue)
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.inputTextColor),
                        cursorBrush = SolidColor(colors.cursorColor),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Phone number input" },
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
                    // Clear button — hidden when there is no text or the field is read-only
                    AnimatedVisibility(
                        visible = ccpConfig.showClearButton &&
                            !ccpConfig.readOnly &&
                            state.phoneNumber.isNotEmpty()
                    ) {
                        IconButton(
                            onClick = state::clearPhone,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = EzzyIcons.Close,
                                contentDescription = "Clear phone number",
                                tint = colors.countryChevronColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Full-width dropdown anchored to the phone input Box, with 16dp start/end margin
            if (ccpConfig.countryPickerStyle == CountryPickerStyle.Dropdown) {
                val dropdownWidth = with(density) { boxWidthPx.toDp() - 32.dp }.coerceAtLeast(0.dp)
                CountriesDropdown(
                    expanded = dropdownExpanded,
                    onDismiss = { dropdownExpanded = false },
                    onSelectCountry = { country ->
                        state.selectCountry(country)
                        dropdownExpanded = false
                    },
                    countriesToShow = countriesToShow,
                    countriesExclude = countriesExclude,
                    pinnedCountries = pinnedCountries,
                    ccpColors = colors,
                    ccpConfig = ccpConfig,
                    modifier = Modifier.requiredWidth(dropdownWidth),
                    dropdownOffset = DpOffset(x = 16.dp, y = 8.dp)
                )
            }
        } // end Box

        // Error message
        AnimatedVisibility(visible = isError && errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = colors.errorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
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
 * @param countriesToShow Whitelist of ISO codes shown in the sheet.
 * @param countriesExclude Blacklist of ISO codes hidden from the sheet.
 * @param pinnedCountries ISO codes pinned to the top "Suggested" section.
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
    countriesExclude: List<String> = emptyList(),
    pinnedCountries: List<String> = emptyList(),
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig(),
    /** When non-null and style is Dropdown, expansion is managed externally — called on button tap. */
    onDropdownExpand: (() -> Unit)? = null,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val countryName = selectedCountry?.name ?: "United States"
    val dialCode = selectedCountry?.dialCode ?: "+1"

    // Box anchors the dropdown to the selector button
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.semantics {
                contentDescription = "$countryName $dialCode, tap to change country"
                role = Role.Button
            },
            onClick = {
                val isExternalDropdown = ccpConfig.countryPickerStyle == CountryPickerStyle.Dropdown &&
                    onDropdownExpand != null
                if (isExternalDropdown) onDropdownExpand!!() else isExpanded = true
            },
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
                    text = "${selectedCountry?.code ?: "US"} $dialCode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ccpColors.countryCodeTextColor
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = EzzyIcons.ChevronDown,
                    contentDescription = null,
                    tint = ccpColors.countryChevronColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Dropdown — only rendered here when not externally controlled by a parent
        if (ccpConfig.countryPickerStyle == CountryPickerStyle.Dropdown && onDropdownExpand == null) {
            CountriesDropdown(
                expanded = isExpanded,
                onDismiss = { isExpanded = false },
                onSelectCountry = { country ->
                    isExpanded = false
                    onSelectCountry(country)
                },
                countriesToShow = countriesToShow,
                countriesExclude = countriesExclude,
                pinnedCountries = pinnedCountries,
                ccpColors = ccpColors,
                ccpConfig = ccpConfig
            )
        }
    }

    // Bottom sheet — rendered outside the Box so it covers the full screen
    if (ccpConfig.countryPickerStyle == CountryPickerStyle.BottomSheet && isExpanded) {
        CountriesBottomSheet(
            sheetState = sheetState,
            onSelectCountries = { country ->
                isExpanded = false
                onSelectCountry(country)
            },
            onDismiss = { isExpanded = false },
            countriesToShow = countriesToShow,
            countriesExclude = countriesExclude,
            pinnedCountries = pinnedCountries,
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

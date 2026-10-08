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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.detection.CountryDetectionResult
import com.ezzy.ccp.countrypicker.detection.rememberDefaultCountryDetector
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.toLegacy
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle as PickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.ui.CountryPickerSheet
import com.ezzy.ccp.countrypicker.ui.EmbeddedPhonePrefix
import com.ezzy.ccp.countrypicker.ui.PhonePrefixDivider
import com.ezzy.ccp.countrypicker.ui.rememberActiveInteractionSource
import com.ezzy.ccp.countrypicker.ui.scaledForPhoneField
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.CountryPickerStyle
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_change_country_a11y
import com.ezzy.ccp.resources.ccp_clear_phone
import com.ezzy.ccp.resources.ccp_country_code_subtitle
import com.ezzy.ccp.resources.ccp_country_code_title
import com.ezzy.ccp.resources.ccp_phone_input_a11y
import com.ezzy.ccp.state.PhoneState
import com.ezzy.ccp.state.rememberPhoneState
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.countryToFlagEmoji
import com.ezzy.ccp.utils.getMaxPhoneLength
import org.jetbrains.compose.resources.stringResource

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
 * @param label Floating label shown notched into the field's outline. Only rendered when
 * [CCPConfig.showLabel] is true (off by default, matching the field's previous no-label look) —
 * pass both to give the field a label.
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
public fun PhoneNumberInput(
    modifier: Modifier = Modifier,
    state: PhoneState = rememberPhoneState(),
    phoneHint: String = "Enter phone",
    label: String? = "Phone number",
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
    val detector = rememberDefaultCountryDetector()

    LaunchedEffect(setCountry, ccpConfig.autoDetectCountry) {
        when {
            !setCountry.isNullOrEmpty() -> {
                val found = countryList.find {
                    it.code.equals(setCountry, ignoreCase = true) ||
                        it.name.equals(setCountry, ignoreCase = true)
                } ?: countryList.find { it.code == "US" }!!
                state.selectCountry(found)
            }
            ccpConfig.autoDetectCountry -> state.setCountryByCode(
                (detector.detectCountry() as? CountryDetectionResult.Detected)?.iso2Code ?: "US",
            )
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

    Column(modifier = modifier) {
        // The Dropdown picker style anchors a separate popup below a compact selector button — a
        // fundamentally different composition from the unified field below, and not the one shown to
        // be broken, so it is left exactly as it was. The BottomSheet style (the default, and what the
        // bug report's screenshots show) is rebuilt onto one shared outline instead of two adjacent
        // Surfaces — see UnifiedLegacyPhoneField.
        if (ccpConfig.countryPickerStyle == CountryPickerStyle.Dropdown) {
            LegacyDropdownPhoneField(
                state = state,
                phoneHint = phoneHint,
                countriesToShow = countriesToShow,
                countriesExclude = countriesExclude,
                pinnedCountries = pinnedCountries,
                colors = colors,
                ccpConfig = ccpConfig,
                effectiveBorderColor = effectiveBorderColor,
                effectiveBorderWidth = effectiveBorderWidth,
                onDone = onDone,
            )
        } else {
            UnifiedLegacyPhoneField(
                state = state,
                phoneHint = phoneHint,
                label = label,
                countriesToShow = countriesToShow,
                countriesExclude = countriesExclude,
                pinnedCountries = pinnedCountries,
                colors = colors,
                ccpConfig = ccpConfig,
                isError = isError,
                onDone = onDone,
            )
        }

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
 * The `CountryPickerStyle.BottomSheet` rendering: a real Material outlined text field — border plus
 * a label notched into it, transparent container — with the flag, dial code, dropdown chevron and a
 * vertical divider as its leading content. This is the fix for both the "two separate rounded boxes"
 * bug and a follow-up regression where the rebuilt field was one filled box with the label drawn as a
 * separate line of text above it, rather than a true outline. Internally this is the same
 * [EmbeddedPhonePrefix]/[PhonePrefixDivider] building blocks
 * [com.ezzy.ccp.countrypicker.ui.PhoneNumberField] uses, so the legacy component is not a second,
 * parallel implementation of the same idea.
 *
 * [label] and the vertical divider are both independently togglable via [CCPConfig.showLabel] and
 * [CCPConfig.showPhonePrefixDivider] — off (no label) and on (divider shown) by default, matching
 * the field's look before either became configurable. [phoneHint] remains the field's placeholder
 * regardless of whether the label is shown.
 */
@Composable
private fun UnifiedLegacyPhoneField(
    state: PhoneState,
    phoneHint: String,
    label: String?,
    countriesToShow: List<String>,
    countriesExclude: List<String>,
    pinnedCountries: List<String>,
    colors: CCPColors,
    ccpConfig: CCPConfig,
    isError: Boolean,
    onDone: () -> Unit,
) {
    val showMessage = rememberTransientMessage()
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var isPrefixOpen by remember { mutableStateOf(false) }
    val borderInteractionSource = rememberActiveInteractionSource(isFocused || isPrefixOpen)

    val pickerConfig = remember(countriesToShow, countriesExclude, pinnedCountries, ccpConfig) {
        legacyCountryPickerConfig(countriesToShow, countriesExclude, pinnedCountries)
    }
    val pickerStyle = legacyPickerStyle(ccpConfig)
    val fieldColors = legacyFieldColors(colors)
    // ccpConfig.borderWidth defaults to 0.dp, meaning "no explicit override" for the older filled-box
    // rendering (which relied on containerColor for contrast, not a border). An outlined field with a
    // transparent container needs a real, visible border by default, so 0.dp falls back to the
    // library's own outline width instead of literally rendering no border. A caller who set a
    // non-zero width explicitly still gets it respected for the unfocused state.
    val unfocusedBorderWidth = if (ccpConfig.borderWidth > 0.dp) ccpConfig.borderWidth else LEGACY_BORDER_WIDTH
    val showLabel = ccpConfig.showLabel && label != null

    // Compact/ExtraCompact scale the flag, chevron, icon buttons, content padding, and font size
    // down together (see PhoneFieldSize) — a smaller field never clips or crowds its own content,
    // because the content shrinks along with it rather than staying fixed inside a squeezed box.
    val contentScale = ccpConfig.phoneFieldSize.contentScale
    val fontScale = ccpConfig.phoneFieldSize.fontScale
    val effectiveValueStyle = MaterialTheme.typography.bodyLarge
        .copy(color = colors.inputTextColor)
        .scaledForPhoneField(fontScale)
    val effectiveHintStyle = ccpConfig.phoneHintStyle.scaledForPhoneField(fontScale)
    val fieldMinHeight = (if (showLabel) LEGACY_MIN_HEIGHT_WITH_LABEL else LEGACY_MIN_HEIGHT_NO_LABEL) * contentScale
    // Resolved outside the semantics lambda: that block is not composable, so it cannot call
    // stringResource itself.
    val phoneInputLabel = stringResource(Res.string.ccp_phone_input_a11y)

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
        textStyle = effectiveValueStyle,
        cursorBrush = SolidColor(colors.cursorColor),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done,
        ),
        singleLine = true,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = fieldMinHeight)
            .semantics {
                contentDescription = phoneInputLabel
                // Advertises the field to Android Autofill and password managers. A filled
                // "+254712345678" is routed through PhoneState.updatePhoneNumber, which adopts the
                // country the number names instead of reading its calling code as subscriber digits.
                contentType = ContentType.PhoneNumber + ContentType.PhoneNumberNational
            },
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = state.phoneNumber,
                innerTextField = innerTextField,
                enabled = !ccpConfig.readOnly,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = borderInteractionSource,
                isError = isError,
                // No explicit style: the label's font size must come from DecorationBox's own
                // ambient TextStyle so it animates between the resting and notched sizes — an
                // explicit, fully-specified style here would freeze it at one size instead. This
                // also means the label does not follow PhoneFieldSize's font scale.
                label = if (showLabel) {
                    { Text(text = label!!) }
                } else {
                    null
                },
                placeholder = { Text(text = phoneHint, style = effectiveHintStyle) },
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmbeddedPhonePrefix(
                            selectedCountry = state.canonicalCountry,
                            onCountrySelected = { country -> state.selectCountry(country) },
                            enabled = !ccpConfig.readOnly,
                            contentMode = if (ccpConfig.showCountryFlag) {
                                PhonePrefixContentMode.FlagAndDialCode
                            } else {
                                PhonePrefixContentMode.DialCodeOnly
                            },
                            // The legacy selector always showed a bare emoji with no backing shape —
                            // Plain style reproduces that exactly rather than the newer tile look.
                            flagSize = LEGACY_FLAG_SIZE * contentScale,
                            flagStyle = CountryFlagStyle.Plain,
                            textStyle = pickerStyle.typography.dialCode
                                .copy(color = colors.countryCodeTextColor)
                                .scaledForPhoneField(fontScale),
                            chevronColor = colors.countryChevronColor,
                            config = pickerConfig,
                            style = pickerStyle,
                            minHeight = pickerStyle.dimensions.minimumTouchTarget * contentScale,
                            onOpenChanged = { isPrefixOpen = it },
                        )

                        PhonePrefixDivider(
                            visible = ccpConfig.showPhonePrefixDivider,
                            height = LEGACY_DIVIDER_HEIGHT * contentScale,
                            style = pickerStyle,
                        )
                    }
                },
                trailingIcon = {
                    // Hidden when there is no text or the field is read-only.
                    AnimatedVisibility(
                        visible = ccpConfig.showClearButton &&
                            !ccpConfig.readOnly &&
                            state.phoneNumber.isNotEmpty(),
                    ) {
                        IconButton(
                            onClick = state::clearPhone,
                            modifier = Modifier.size(CLEAR_BUTTON_SIZE * contentScale),
                        ) {
                            Icon(
                                imageVector = EzzyIcons.Close,
                                contentDescription = stringResource(Res.string.ccp_clear_phone),
                                tint = colors.countryChevronColor,
                                modifier = Modifier.size(CLEAR_ICON_SIZE * contentScale),
                            )
                        }
                    }
                },
                colors = fieldColors,
                contentPadding = OutlinedTextFieldDefaults.contentPadding(
                    top = DECORATION_VERTICAL_PADDING * contentScale,
                    bottom = DECORATION_VERTICAL_PADDING * contentScale,
                ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = !ccpConfig.readOnly,
                        isError = isError,
                        interactionSource = borderInteractionSource,
                        colors = fieldColors,
                        shape = ccpConfig.phoneInputShape,
                        focusedBorderThickness = LEGACY_FOCUSED_BORDER_WIDTH,
                        unfocusedBorderThickness = unfocusedBorderWidth,
                    )
                },
            )
        },
        keyboardActions = KeyboardActions(
            onDone = {
                if (!state.isValid) {
                    showMessage("Invalid phone number")
                    return@KeyboardActions
                }
                keyboardController?.hide()
                onDone()
            },
        ),
        readOnly = ccpConfig.readOnly,
    )
}

/**
 * The `CountryPickerStyle.Dropdown` rendering — unchanged from before this redesign. A compact
 * selector button with a popup anchored below it is a different composition from a unified outlined
 * field and was not implicated in the reported visual bug, so it is preserved exactly.
 */
@Composable
private fun LegacyDropdownPhoneField(
    state: PhoneState,
    phoneHint: String,
    countriesToShow: List<String>,
    countriesExclude: List<String>,
    pinnedCountries: List<String>,
    colors: CCPColors,
    ccpConfig: CCPConfig,
    effectiveBorderColor: Color,
    effectiveBorderWidth: Dp,
    onDone: () -> Unit,
) {
    val showMessage = rememberTransientMessage()
    val keyboardController = LocalSoftwareKeyboardController.current
    var dropdownExpanded by remember { mutableStateOf(false) }
    var boxWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    // Resolved outside the semantics lambda, which is not composable.
    val legacyPhoneInputLabel = stringResource(Res.string.ccp_phone_input_a11y)

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
                    onDropdownExpand = { dropdownExpanded = true },
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
                        .semantics {
                            contentDescription = legacyPhoneInputLabel
                            contentType = ContentType.PhoneNumber + ContentType.PhoneNumberNational
                        },
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
                                showMessage("Invalid phone number")
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
                            contentDescription = stringResource(Res.string.ccp_clear_phone),
                            tint = colors.countryChevronColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Full-width dropdown anchored to the phone input Box, with 16dp start/end margin
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
}

private val LEGACY_FLAG_SIZE = 18.dp
private val LEGACY_BORDER_WIDTH = 1.dp
private val LEGACY_FOCUSED_BORDER_WIDTH = 2.dp
private val LEGACY_MIN_HEIGHT_WITH_LABEL = 68.dp
private val LEGACY_MIN_HEIGHT_NO_LABEL = 60.dp
private val LEGACY_DIVIDER_HEIGHT = 24.dp
private val CLEAR_BUTTON_SIZE = 36.dp
private val CLEAR_ICON_SIZE = 16.dp
private val DECORATION_VERTICAL_PADDING = 16.dp

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
public fun SelectedCountryComponent(
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

    // Resolved outside the semantics lambda, which is not composable.
    val changeCountryLabel = stringResource(Res.string.ccp_change_country_a11y, countryName, dialCode)

    // Box anchors the dropdown to the selector button
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.semantics {
                contentDescription = changeCountryLabel
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

    // Bottom sheet — rendered outside the Box so it covers the full screen.
    //
    // This is the migration point: the sheet is now the country-picker library's
    // CountryPickerSheet rather than the older CountriesBottomSheet, so this component gains ranked
    // search (ISO alpha-3, dial codes, aliases, accent folding), region filters, grouped sections and
    // the full 236-country dataset without any change to its own public API. The legacy
    // CountriesBottomSheet remains available but deprecated.
    if (ccpConfig.countryPickerStyle == CountryPickerStyle.BottomSheet && isExpanded) {
        LegacyCountryPickerSheet(
            selectedCountryCode = selectedCountry?.code,
            onSelectCountry = { country ->
                isExpanded = false
                onSelectCountry(country)
            },
            onDismiss = { isExpanded = false },
            countriesToShow = countriesToShow,
            countriesExclude = countriesExclude,
            pinnedCountries = pinnedCountries,
            ccpConfig = ccpConfig,
        )
    }
}

/**
 * Bridges the legacy `PhoneNumberInput` parameters onto the new [CountryPickerSheet].
 *
 * Translates the old list-of-ISO-code parameters into a [CountryPickerConfig] and maps the canonical
 * [com.ezzy.ccp.countrypicker.model.Country] the sheet returns back to the legacy [Country] the
 * callback expects. Keeping this translation in one place is what lets the old API stay byte-for-byte
 * source-compatible while the implementation underneath changes completely.
 */
@Composable
private fun LegacyCountryPickerSheet(
    selectedCountryCode: String?,
    onSelectCountry: (Country) -> Unit,
    onDismiss: () -> Unit,
    countriesToShow: List<String>,
    countriesExclude: List<String>,
    pinnedCountries: List<String>,
    ccpConfig: CCPConfig,
) {
    val config = remember(countriesToShow, countriesExclude, pinnedCountries) {
        legacyCountryPickerConfig(countriesToShow, countriesExclude, pinnedCountries)
    }

    val selected = remember(selectedCountryCode) {
        DefaultCountryDataSource.findByIso2(selectedCountryCode)?.let(::setOf).orEmpty()
    }

    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = selected,
    )

    // The legacy component opened its sheet by flipping a boolean; the new state holder needs to be
    // told the sheet is showing so its pending selection is seeded from the confirmed one.
    LaunchedEffect(Unit) { pickerState.open() }

    CountryPickerSheet(
        state = pickerState,
        onCountrySelected = { country -> onSelectCountry(country.toLegacy()) },
        onDismiss = onDismiss,
        title = UiText.resource(Res.string.ccp_country_code_title),
        subtitle = UiText.resource(Res.string.ccp_country_code_subtitle),
        style = legacyPickerStyle(ccpConfig),
    )
}

/**
 * Translates the legacy allow/exclude/pinned-list parameters into a [CountryPickerConfig] — the one
 * place this mapping happens, shared by [LegacyCountryPickerSheet] (used by [SelectedCountryComponent]
 * and the Dropdown style) and [UnifiedLegacyPhoneField]'s embedded prefix, so the two call sites
 * cannot drift into interpreting the legacy lists differently.
 */
private fun legacyCountryPickerConfig(
    countriesToShow: List<String>,
    countriesExclude: List<String>,
    pinnedCountries: List<String>,
): CountryPickerConfig = CountryPickerConfig(
    // An empty legacy whitelist meant "all countries", so it maps to null rather than to an empty
    // allow-set, which in the new config means "nothing is allowed".
    allowedCountryCodes = countriesToShow.takeIf { it.isNotEmpty() }?.toSet(),
    excludedCountryCodes = countriesExclude.toSet(),
    suggestedCountryCodes = pinnedCountries,
)

/**
 * The ambient picker style with the legacy row options applied — whether rows show their flag and
 * dial code — the presentational half of what [legacyCountryPickerConfig] does for behaviour.
 */
@Composable
private fun legacyPickerStyle(ccpConfig: CCPConfig): PickerStyle {
    val base = CountryPickerTheme.style
    return remember(base, ccpConfig.showDialCodeCountryItem, ccpConfig.showFlagCountryItem) {
        base.copy(
            layout = base.layout.copy(
                showDialCode = ccpConfig.showDialCodeCountryItem,
                flagStyle = if (ccpConfig.showFlagCountryItem) base.layout.flagStyle else CountryFlagStyle.Hidden,
            ),
        )
    }
}

/**
 * Maps [CCPColors] onto Material's outlined-field colors. The container is always transparent: the
 * legacy field reads as an outlined text field — a border with the label notched into it — not a
 * filled box.
 */
@Composable
private fun legacyFieldColors(colors: CCPColors) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = colors.inputTextColor,
    unfocusedTextColor = colors.inputTextColor,
    disabledTextColor = colors.inputTextColor,
    errorTextColor = colors.inputTextColor,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    cursorColor = colors.cursorColor,
    errorCursorColor = colors.errorColor,
    focusedBorderColor = colors.cursorColor,
    unfocusedBorderColor = colors.borderColor,
    disabledBorderColor = colors.borderColor,
    errorBorderColor = colors.errorColor,
    focusedLabelColor = colors.cursorColor,
    unfocusedLabelColor = colors.phoneHintColor,
    disabledLabelColor = colors.phoneHintColor,
    errorLabelColor = colors.errorColor,
    focusedPlaceholderColor = colors.phoneHintColor,
    unfocusedPlaceholderColor = colors.phoneHintColor,
)

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

/**
 * A brief, non-blocking message for the legacy component's "Done with an invalid number" case: a
 * Toast on Android. iOS has no system equivalent, and the field already shows its error state, so
 * there it does nothing.
 */
@Composable
internal expect fun rememberTransientMessage(): (String) -> Unit

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

package com.ezzy.ccp.countrypicker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.phone.PhoneNumberFormatter
import com.ezzy.ccp.countrypicker.phone.PhoneNumberValidator
import com.ezzy.ccp.countrypicker.phone.PhoneVerificationController
import com.ezzy.ccp.countrypicker.phone.PhoneVerificationState
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.PhoneNumberFieldState
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryFlagConfig
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputStyle
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons

/**
 * An international phone number field: country prefix and national number input inside **one**
 * unified outlined container.
 *
 * ### One outline, not two
 * The prefix (flag, dial code, chevron) is not a separate bordered pill glued to the number field —
 * it is a bare clickable region ([EmbeddedPhonePrefix]) rendered as the leading content of a real
 * Material outlined text field ([OutlinedTextFieldDefaults]), separated from the number editor only
 * by a subtle [PhonePrefixDivider]. Building on the actual outlined-field decoration (rather than a
 * hand-rolled bordered [androidx.compose.material3.Surface]) is what gives the field a transparent
 * container and a label that sits *in* the border itself — animating between resting in the value's
 * position and floating in a notch cut into the top border — instead of a filled box with the label
 * drawn as a separate line of text above it. A standalone dial-code pill is still available and
 * correct on its own via `CountrySelector(variant = CountrySelectorVariant.DialCode)` /
 * [PhoneCountryCodeSelector] — the distinction is embedded-in-a-field vs. standalone-control, not a
 * visual inconsistency to paper over.
 *
 * ### What the caller gets
 * [onValueChange] delivers a complete [PhoneNumberValue] — formatted forms, `e164Number`, and validity —
 * so submission code never concatenates a dial code onto digits. That concatenation is wrong for every
 * country with a national trunk prefix, and it is the bug this API exists to prevent.
 *
 * ### Country changes preserve the number
 * Switching country keeps the typed digits, re-formats them for the new region, and re-runs validation.
 * A number valid in one region is never carried over as still-valid in another — see
 * [PhoneNumberFieldState.selectCountry].
 *
 * ### Verification
 * Optional and fully decoupled. Pass a [verificationController] and the field reports verification
 * status and invalidates it whenever the number changes; pass nothing and none of that machinery exists.
 * The library ships no verification backend — see
 * [com.ezzy.ccp.countrypicker.phone.PhoneNumberVerificationHandler].
 *
 * @param state Field state. Hoist it to read the number or clear the field from outside.
 * @param onValueChange Called whenever the number or country changes.
 * @param label Visible floating label. Rendered only when [PhoneNumberInputStyle.labelMode] is
 *   [com.ezzy.ccp.countrypicker.model.InputLabelMode.Floating]; ignored (but still used for
 *   accessibility, via [accessibilityLabel]'s fallback) when `Hidden`.
 * @param accessibilityLabel The label exposed to accessibility services, independent of whether the
 *   visible label renders. Defaults to [label] — hiding the visible label never leaves the number
 *   editor unlabeled to a screen reader; pass a different value only if the accessible name should
 *   read differently from the visible text.
 * @param inputStyle Label mode, flag presentation, prefix content and divider visibility — see
 *   [PhoneNumberInputStyle].
 * @param showHelperText Show the live "Formats live for Kenya · e.g. +254 712 345 678" helper.
 * @param isError Forces the error treatment. Combined with the field's own validation, so a host can
 *   surface a server-side rejection without suppressing local validation.
 * @param errorMessage Overrides the derived validation message.
 * @param validateWhileTyping Show validation errors before the field loses focus. Off by default:
 *   flagging an incomplete number as invalid on the third digit is accurate and hostile.
 * @param onDone Invoked when the keyboard action fires and the number is valid.
 */
@Composable
fun PhoneNumberField(
    onValueChange: (PhoneNumberValue) -> Unit,
    modifier: Modifier = Modifier,
    state: PhoneNumberFieldState = rememberPhoneNumberFieldState(
        initialCountry = DefaultCountryDataSource.countries.first(),
    ),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: UiText? = UiText.resource(R.string.ccp_phone_number),
    accessibilityLabel: UiText? = label,
    placeholder: UiText? = null,
    inputStyle: PhoneNumberInputStyle = PhoneNumberInputDefaults.style(),
    config: CountryPickerConfig = CountryPickerDefaults.phoneConfig(),
    showHelperText: Boolean = true,
    showClearButton: Boolean = true,
    isError: Boolean = false,
    errorMessage: UiText? = null,
    validateWhileTyping: Boolean = false,
    verificationController: PhoneVerificationController? = null,
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    onDone: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // The label falls back to a generic resource only when the caller passed neither label nor an
    // explicit accessibilityLabel — a hidden visual label must never leave the editor unlabeled.
    val effectiveAccessibilityLabel = (accessibilityLabel ?: label)?.resolve()
        ?: stringResource(R.string.ccp_phone_input_label)

    // True while the embedded country sheet is open, so the outline reads as "focused" for either of
    // the field's two interactive regions, not just the text editor. Fed into a synthetic interaction
    // source (see rememberActiveInteractionSource) rather than the text field's own real one, so the
    // border/label respond to it without the field's real focus state being touched by it.
    var isPrefixOpen by remember { mutableStateOf(false) }
    val borderInteractionSource = rememberActiveInteractionSource(isFocused || isPrefixOpen)

    // Report every change upward, including country switches, so the caller's value is never stale.
    LaunchedEffect(state.value) {
        onValueChange(state.value)
        // Editing the number invalidates any verification already obtained for the old one.
        verificationController?.onPhoneNumberChanged(state.value)
    }

    // Errors appear once the field has been touched (or the host forces them), never mid-typing.
    val showValidationError = (validateWhileTyping || state.hasBeenTouched) &&
        state.value.validity.isError
    val hasError = isError || showValidationError
    val derivedError = errorMessage
        ?: PhoneNumberValidator.errorMessage(state.value, treatIncompleteAsError = state.hasBeenTouched)

    val showVisibleLabel = label != null && inputStyle.hasVisibleLabel

    // Compact/ExtraCompact scale the flag, chevron, icon buttons, content padding, and font size
    // down together (see PhoneFieldSize) — a smaller field never clips or crowds its own content,
    // because the content shrinks along with it rather than staying fixed inside a squeezed box.
    val effectiveDimensions = dimensions.scaledForPhoneField(inputStyle.size)
    val effectiveFlagConfig = inputStyle.flagConfig.scaledForPhoneField(inputStyle.size)
    val effectiveTypography = typography.scaledForPhoneField(inputStyle.size.fontScale)
    val contentPaddingScale = inputStyle.size.contentScale

    // A floating label still needs a touch more reserved height than a hidden one even inside a real
    // outlined field — these are floors via defaultMinSize below, not fixed sizes: the field is free
    // to size itself larger from its own content and typography.
    val fieldMinHeight = (
        if (showVisibleLabel) dimensions.phoneFieldMinHeightWithLabel else dimensions.phoneFieldMinHeightNoLabel
        ) * contentPaddingScale

    val fieldColors = phoneFieldColors(colors)

    Column(modifier = modifier) {
        BasicTextField(
            value = state.textFieldValue,
            onValueChange = state::onTextChanged,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = effectiveTypography.selectorValue.copy(color = colors.selectorContent),
            cursorBrush = SolidColor(colors.selectorFocusedBorder),
            singleLine = true,
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    state.markTouched()
                    if (state.value.isValid) {
                        keyboardController?.hide()
                        onDone()
                    }
                },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = fieldMinHeight)
                .semantics { contentDescription = effectiveAccessibilityLabel },
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = state.textFieldValue.text,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = borderInteractionSource,
                    isError = hasError,
                    // No explicit style: the label's font size must come from DecorationBox's own
                    // ambient TextStyle so it animates between the resting and notched sizes — an
                    // explicit, fully-specified style here would freeze it at one size instead. This
                    // also means the label does not follow PhoneFieldSize.fontScale: it keeps
                    // Material's own bodyLarge/bodySmall sizing regardless of the field's size.
                    label = if (showVisibleLabel) {
                        { Text(text = label!!.resolve()) }
                    } else {
                        null
                    },
                    placeholder = {
                        Text(
                            // The placeholder is the region's own example number, so it doubles as a
                            // format hint rather than generic filler.
                            text = placeholder?.resolve()
                                ?: PhoneNumberFormatter.exampleNationalNumber(state.country).orEmpty(),
                            style = effectiveTypography.selectorValue,
                        )
                    },
                    leadingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            EmbeddedPhonePrefix(
                                selectedCountry = state.country,
                                onCountrySelected = state::selectCountry,
                                enabled = enabled && !readOnly,
                                contentMode = inputStyle.prefixContentMode,
                                showDropdownIcon = inputStyle.showDropdownIcon,
                                flagConfig = effectiveFlagConfig,
                                config = config,
                                recentCountryStore = recentCountryStore,
                                repository = repository,
                                colors = colors,
                                shapes = shapes,
                                dimensions = effectiveDimensions,
                                typography = effectiveTypography,
                                motion = motion,
                                flagContent = flagContent,
                                onOpenChanged = { isPrefixOpen = it },
                            )

                            PhonePrefixDivider(
                                visible = inputStyle.showPrefixDivider,
                                fieldHeight = effectiveDimensions.selectorDialMinHeight,
                                colors = colors,
                                motion = motion,
                            )
                        }
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VerificationStatusIcon(
                                controller = verificationController,
                                value = state.value,
                                colors = colors,
                                dimensions = effectiveDimensions,
                                motion = motion,
                            )

                            AnimatedVisibility(
                                visible = showClearButton && !readOnly && state.nationalDigits.isNotEmpty(),
                                enter = scaleIn(motion.selectionSpring) + fadeIn(motion.fadeIn),
                                exit = scaleOut(motion.fadeOut) + fadeOut(motion.fadeOut),
                            ) {
                                IconButton(
                                    onClick = state::clear,
                                    modifier = Modifier.size(effectiveDimensions.iconButtonSize),
                                ) {
                                    Icon(
                                        imageVector = EzzyIcons.Close,
                                        contentDescription = stringResource(R.string.ccp_clear_phone),
                                        tint = colors.chevron,
                                        modifier = Modifier.size(CLEAR_ICON_SIZE * contentPaddingScale),
                                    )
                                }
                            }
                        }
                    },
                    colors = fieldColors,
                    contentPadding = OutlinedTextFieldDefaults.contentPadding(
                        top = DECORATION_VERTICAL_PADDING * contentPaddingScale,
                        bottom = DECORATION_VERTICAL_PADDING * contentPaddingScale,
                    ),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = hasError,
                            interactionSource = borderInteractionSource,
                            colors = fieldColors,
                            shape = shapes.selectorOutlined,
                            focusedBorderThickness = dimensions.selectorFocusedBorderWidth,
                            unfocusedBorderThickness = dimensions.selectorBorderWidth,
                        )
                    },
                )
            },
        )

        PhoneFieldHelperText(
            country = state.country,
            showHelper = showHelperText,
            hasError = hasError,
            errorMessage = derivedError,
            colors = colors,
            typography = effectiveTypography,
            motion = motion,
        )
    }
}

/**
 * The live helper line, or the validation error when there is one.
 *
 * The helper text is generated from the selected country's libphonenumber metadata, so it updates the
 * instant the country changes and contains no country-specific literal anywhere in the library.
 */
@Composable
private fun PhoneFieldHelperText(
    country: Country,
    showHelper: Boolean,
    hasError: Boolean,
    errorMessage: UiText?,
    colors: CountryPickerColors,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    val text: UiText? = when {
        hasError && errorMessage != null -> errorMessage
        showHelper -> PhoneNumberValidator.helperText(country)
        else -> null
    }

    AnimatedContent(
        targetState = text,
        transitionSpec = { fadeIn(motion.fadeIn) togetherWith fadeOut(motion.fadeOut) },
        label = "phoneHelper",
    ) { target ->
        if (target == null) return@AnimatedContent
        Row(
            modifier = Modifier.padding(start = LABEL_START_PADDING, top = HELPER_TOP_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HELPER_ICON_SPACING),
        ) {
            if (hasError) {
                Icon(
                    imageVector = PickerIcons.Alert,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier.size(HELPER_ICON_SIZE),
                )
            }
            Text(
                text = target.resolve(),
                style = typography.helperText,
                color = if (hasError) colors.error else colors.selectorSecondaryContent,
            )
        }
    }
}

/**
 * A tick inside the field once the current number is verified.
 *
 * Deliberately checks the controller against *this* number rather than trusting a `Verified` state:
 * the state alone would keep showing a tick after the user edited a digit.
 */
@Composable
private fun VerificationStatusIcon(
    controller: PhoneVerificationController?,
    value: PhoneNumberValue,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    motion: CountryPickerMotion,
) {
    if (controller == null) return
    val verified = controller.isVerified(value)
    val inProgress = controller.state.isInProgress

    AnimatedVisibility(
        visible = verified || inProgress,
        enter = scaleIn(motion.selectionSpring) + fadeIn(motion.fadeIn),
        exit = scaleOut(motion.fadeOut) + fadeOut(motion.fadeOut),
    ) {
        if (inProgress) {
            androidx.compose.material3.CircularProgressIndicator(
                color = colors.selectorFocusedBorder,
                strokeWidth = SPINNER_STROKE,
                modifier = Modifier.size(SPINNER_SIZE),
            )
        } else {
            Icon(
                imageVector = PickerIcons.CheckCircle,
                contentDescription = stringResource(R.string.ccp_verify_verified),
                tint = colors.success,
                modifier = Modifier.size(dimensions.chevronSize),
            )
        }
    }
}

/** True when [state] means the field should be treated as verified for [value]. */
internal fun PhoneVerificationState.isVerifiedFor(value: PhoneNumberValue): Boolean =
    this is PhoneVerificationState.Verified && e164Number == value.e164Number

/**
 * Maps [colors] onto Material's [androidx.compose.material3.TextFieldColors], shared by
 * [PhoneNumberField] and the legacy `UnifiedLegacyPhoneField` (which bridges its own [CCPColors]
 * into a [CountryPickerColors] first) so the "what the outline actually looks like" mapping exists
 * in exactly one place.
 *
 * The container colors are always transparent: this field reads as a Material outlined text field
 * (border plus a label notched into it), not a filled box, so nothing here should paint a background.
 */
@Composable
internal fun phoneFieldColors(colors: CountryPickerColors) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = colors.selectorContent,
    unfocusedTextColor = colors.selectorContent,
    disabledTextColor = colors.selectorContent,
    errorTextColor = colors.selectorContent,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    cursorColor = colors.selectorFocusedBorder,
    errorCursorColor = colors.error,
    focusedBorderColor = colors.selectorFocusedBorder,
    unfocusedBorderColor = colors.selectorBorder,
    disabledBorderColor = colors.selectorBorder,
    errorBorderColor = colors.error,
    focusedLabelColor = colors.selectorFocusedBorder,
    unfocusedLabelColor = colors.selectorLabel,
    disabledLabelColor = colors.selectorLabel,
    errorLabelColor = colors.error,
    focusedPlaceholderColor = colors.selectorLabel,
    unfocusedPlaceholderColor = colors.selectorLabel,
)

/**
 * Scales [CountryPickerDimensions.chevronSize], [CountryPickerDimensions.iconButtonSize],
 * [CountryPickerDimensions.selectorDialMinHeight] and [CountryPickerDimensions.minimumTouchTarget]
 * by [PhoneFieldSize.contentScale] — shared by [PhoneNumberField] and the legacy
 * `UnifiedLegacyPhoneField` so the "what actually gets smaller" list exists in one place. A no-op
 * for [PhoneFieldSize.Regular].
 *
 * Scaling [CountryPickerDimensions.minimumTouchTarget] down is a deliberate trade-off, not an
 * oversight: [PhoneFieldSize.Compact]/[PhoneFieldSize.ExtraCompact] exist specifically so a field can
 * be shorter than the usual 48dp accessibility floor, and leaving the prefix's own touch-target
 * floor at 48dp regardless would make it taller than the field containing it.
 */
internal fun CountryPickerDimensions.scaledForPhoneField(size: PhoneFieldSize): CountryPickerDimensions {
    if (size == PhoneFieldSize.Regular) return this
    val scale = size.contentScale
    return copy(
        chevronSize = chevronSize * scale,
        iconButtonSize = iconButtonSize * scale,
        selectorDialMinHeight = selectorDialMinHeight * scale,
        minimumTouchTarget = minimumTouchTarget * scale,
    )
}

/** Scales [CountryFlagConfig.size] by [PhoneFieldSize.contentScale]. A no-op for [PhoneFieldSize.Regular]. */
internal fun CountryFlagConfig.scaledForPhoneField(size: PhoneFieldSize): CountryFlagConfig =
    if (size == PhoneFieldSize.Regular) this else copy(size = this.size * size.contentScale)

/**
 * Scales the phone field's value/label/dial-code/helper font sizes by [fontScale]. A no-op for
 * `1f` ([PhoneFieldSize.Regular]'s scale).
 */
internal fun CountryPickerTypography.scaledForPhoneField(fontScale: Float): CountryPickerTypography {
    if (fontScale == 1f) return this
    return copy(
        selectorValue = selectorValue.scaledForPhoneField(fontScale),
        dialCodeValue = dialCodeValue.scaledForPhoneField(fontScale),
        helperText = helperText.scaledForPhoneField(fontScale),
    )
}

/**
 * Scales this style's font size by [scale], leaving an unspecified size alone. Shared by
 * [PhoneNumberField] (via [CountryPickerTypography.scaledForPhoneField]) and the legacy
 * `UnifiedLegacyPhoneField`, which scales its own plain [TextStyle]s (not routed through
 * [CountryPickerTypography]) with this directly.
 */
internal fun TextStyle.scaledForPhoneField(scale: Float) =
    if (fontSize.isUnspecified) this else copy(fontSize = fontSize * scale)

private val LABEL_START_PADDING = 4.dp
private val HELPER_TOP_PADDING = 6.dp
private val HELPER_ICON_SPACING = 6.dp
private val HELPER_ICON_SIZE = 16.dp
private val CLEAR_ICON_SIZE = 16.dp
private val SPINNER_SIZE = 18.dp
private val SPINNER_STROKE = 2.dp
private val DECORATION_VERTICAL_PADDING = 16.dp

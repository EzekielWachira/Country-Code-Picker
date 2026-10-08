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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.util.lerp
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.data.compositionLocale
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryPickerBidi
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
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
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputStyle
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_clear_phone
import com.ezzy.ccp.resources.ccp_number_type_landline
import com.ezzy.ccp.resources.ccp_number_type_mobile
import com.ezzy.ccp.resources.ccp_number_type_mobile_or_landline
import com.ezzy.ccp.resources.ccp_number_type_toll_free
import com.ezzy.ccp.resources.ccp_number_type_voip
import com.ezzy.ccp.resources.ccp_phone_input_label
import com.ezzy.ccp.resources.ccp_phone_number
import com.ezzy.ccp.resources.ccp_phone_state_valid
import com.ezzy.ccp.resources.ccp_phone_state_valid_type
import com.ezzy.ccp.resources.ccp_verify_verified
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * An international phone number field: the country prefix and the number in one field.
 *
 * ### Built to be filled in correctly
 * - **Floating label.** The label rests where the number goes and lifts to the top of the field as
 *   soon as it has focus or a value.
 * - **Ghost digits.** The country's example number shows faintly where the number goes, and the
 *   user's typing fills it in from the left — so the expected length and grouping are visible before
 *   the first digit, and how many digits remain is visible throughout.
 * - **Progress.** A hairline along the bottom of the field fills as digits arrive and turns to the
 *   success color once the number is valid.
 * - **Confirmation.** A valid number earns a check mark and a badge naming its kind — Mobile,
 *   Landline — so a user entering a landline where a mobile is wanted sees it before submitting.
 * - **Gentle validation.** Errors wait until the field has been left (or [validateWhileTyping] is
 *   set): flagging an incomplete number on its third digit is accurate and hostile.
 *
 * Each of these can be switched off through [inputStyle].
 *
 * ### What the caller gets
 * [onValueChange] delivers a complete [PhoneNumberValue] — formatted forms, `e164Number` and validity
 * — so submission code never concatenates a dial code onto digits. That concatenation is wrong for
 * every country with a national trunk prefix, and it is the bug this API exists to prevent.
 *
 * ### Country changes keep the number
 * Switching country keeps the typed digits, re-formats them for the new region and re-validates
 * them. A number valid in one region is never carried over as still valid in another — see
 * [PhoneNumberFieldState.selectCountry]. A pasted or autofilled international number (`+44…`)
 * switches the country itself.
 *
 * ### Verification
 * Optional and decoupled. Pass a [verificationController] and the field shows progress and a verified
 * mark, and drops verification as soon as the number changes. The library ships no verification
 * backend — see [com.ezzy.ccp.countrypicker.phone.PhoneNumberVerificationHandler].
 *
 * @param state Field state. Hoist it to read the number or clear the field from outside.
 * @param onValueChange Called whenever the number or country changes.
 * @param variant The field's container — [CountrySelectorVariant.Elevated], `Outlined`, `Filled`,
 *   `Underlined` or `Card`. The pill variants are drawn as `Elevated`.
 * @param label The floating label. Hidden by [PhoneNumberInputStyle.labelMode] = `Hidden`, but still
 *   announced, through [accessibilityLabel].
 * @param accessibilityLabel The name announced for the number editor. Defaults to [label]; hiding the
 *   visible label never leaves the editor unnamed.
 * @param placeholder Shown in an empty field instead of the example number.
 * @param inputStyle The phone field's own options — label, prefix, size, ghost digits, progress,
 *   badges. See [PhoneNumberInputStyle].
 * @param showHelperText A live helper line generated from the country's metadata: "Formats live for
 *   Kenya · e.g. +254 712 345 678".
 * @param isError Forces the error treatment — for a server-side rejection. Combined with the field's
 *   own validation rather than replacing it.
 * @param errorMessage Overrides the derived validation message.
 * @param onDone Called when the keyboard action fires on a valid number.
 */
@Composable
public fun PhoneNumberField(
    onValueChange: (PhoneNumberValue) -> Unit,
    modifier: Modifier = Modifier,
    state: PhoneNumberFieldState = rememberPhoneNumberFieldState(
        initialCountry = DefaultCountryDataSource.defaultInitialCountry(compositionLocale()),
    ),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    variant: CountrySelectorVariant = CountrySelectorVariant.Elevated,
    label: UiText? = UiText.resource(Res.string.ccp_phone_number),
    accessibilityLabel: UiText? = label,
    placeholder: UiText? = null,
    inputStyle: PhoneNumberInputStyle = PhoneNumberInputDefaults.style(),
    config: CountryPickerConfig = CountryPickerDefaults.phoneConfig(),
    showHelperText: Boolean = true,
    showClearButton: Boolean = true,
    autofillEnabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: UiText? = null,
    validateWhileTyping: Boolean = false,
    verificationController: PhoneVerificationController? = null,
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    onDone: () -> Unit = {},
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    var prefixOpen by remember { mutableStateOf(false) }

    LaunchedEffect(state.value) {
        onValueChange(state.value)
        // Editing the number invalidates any verification obtained for the old one.
        verificationController?.onPhoneNumberChanged(state.value)
    }

    val value = state.value
    val showValidationError = (validateWhileTyping || state.hasBeenTouched) && value.validity.isError
    val hasError = isError || showValidationError
    val message = errorMessage ?: PhoneNumberValidator.errorMessage(
        value = value,
        treatIncompleteAsError = state.hasBeenTouched,
        // So a rejected landline reads "Enter a mobile number" rather than the generic message.
        allowedNumberTypes = state.allowedNumberTypes,
    )
    val verified = verificationController?.isVerified(value) == true
    val verifying = verificationController?.state?.isInProgress == true

    val size = inputStyle.size
    val fieldVariant = if (variant.isPill) CountrySelectorVariant.Elevated else variant
    val typography = style.typography
    val numberStyle = typography.phoneNumber.scaledForPhoneField(size.fontScale)
    val showLabel = label != null && inputStyle.hasVisibleLabel
    val active = focused || prefixOpen
    val accessibleName = (accessibilityLabel ?: label)?.resolve() ?: stringResource(Res.string.ccp_phone_input_label)
    val errorText = if (hasError) message?.resolve() else null
    val trailingPadding by animateDpAsState(
        targetValue = (if (focused && showClearButton) 4.dp else style.dimensions.fieldHorizontalPadding) * size.contentScale,
        animationSpec = style.motion.dp,
        label = "phoneTrailingPadding",
    )
    // The check, badge and verified mark are silent; the editor's state says the same in words.
    val typeLabel = value.numberType?.labelRes()?.let { stringResource(it) }
    val stateText = when {
        verified -> stringResource(Res.string.ccp_verify_verified)
        value.isValid && typeLabel != null -> stringResource(Res.string.ccp_phone_state_valid_type, typeLabel)
        value.isValid -> stringResource(Res.string.ccp_phone_state_valid)
        else -> null
    }

    CountryPickerTheme(style) {
        Column(modifier = modifier) {
            PickerFieldContainer(
                variant = fieldVariant,
                active = active,
                tone = when {
                    hasError -> FieldTone.Error
                    verified -> FieldTone.Success
                    else -> FieldTone.Default
                },
                enabled = enabled,
                style = style,
                minHeight = (if (showLabel) style.dimensions.fieldMinHeight else style.dimensions.fieldMinHeight - 6.dp) * size.contentScale,
                contentPadding = PaddingValues(
                    start = (style.dimensions.fieldHorizontalPadding - 6.dp) * size.contentScale,
                    // Tight while editing, where the clear button's touch target supplies the margin.
                    end = trailingPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp * size.contentScale),
                overlay = if (inputStyle.showProgress) {
                    { NumberProgress(value, hasError, fullWidth = fieldVariant == CountrySelectorVariant.Underlined, style = style) }
                } else {
                    null
                },
            ) {
                EmbeddedPhonePrefix(
                    selectedCountry = state.country,
                    onCountrySelected = state::selectCountry,
                    enabled = enabled && !readOnly,
                    contentMode = inputStyle.prefixContentMode,
                    showDropdownIcon = inputStyle.showDropdownIcon,
                    flagSize = style.dimensions.flagSizeCompact * size.contentScale + 4.dp,
                    textStyle = typography.dialCode.scaledForPhoneField(size.fontScale),
                    config = config,
                    recentCountryStore = recentCountryStore,
                    repository = repository,
                    style = style,
                    minHeight = style.dimensions.minimumTouchTarget * size.contentScale,
                    flagContent = flagContent,
                    onOpenChanged = { prefixOpen = it },
                )
                PhonePrefixDivider(
                    visible = inputStyle.showPrefixDivider,
                    height = 24.dp * size.contentScale,
                    style = style,
                )
                NumberEditor(
                    state = state,
                    enabled = enabled,
                    readOnly = readOnly,
                    label = label.takeIf { showLabel }?.resolve(),
                    placeholder = placeholder?.resolve(),
                    lifted = active || state.textFieldValue.text.isNotEmpty() || !showLabel,
                    active = active,
                    hasError = hasError,
                    inputStyle = inputStyle,
                    numberStyle = numberStyle,
                    labelStyle = typography.fieldLabel.scaledForPhoneField(size.fontScale),
                    style = style,
                    interactionSource = interaction,
                    focusRequester = focusRequester,
                    modifier = Modifier.weight(1f),
                    editorModifier = Modifier.semantics {
                        contentDescription = accessibleName
                        if (errorText != null) error(errorText)
                        if (stateText != null) stateDescription = stateText
                        // Advertises the field to autofill and password managers. Both content types
                        // are declared because providers store either shape; a filled "+254712345678"
                        // adopts its own country — see PhoneNumberFieldState.
                        if (autofillEnabled) {
                            contentType = ContentType.PhoneNumber + ContentType.PhoneNumberNational
                        }
                    },
                    onDone = {
                        state.markTouched()
                        if (state.value.isValid) {
                            keyboard?.hide()
                            onDone()
                        }
                    },
                )
                TrailingStatus(
                    value = value,
                    typeLabel = typeLabel,
                    verified = verified,
                    verifying = verifying,
                    inputStyle = inputStyle,
                    style = style,
                )
                // Only while editing, as on iOS: at rest the field shows its result, not its tools,
                // and the number keeps the room.
                AnimatedVisibility(
                    visible = showClearButton && focused && enabled && !readOnly && state.nationalDigits.isNotEmpty() && !verified,
                    enter = scaleIn(style.motion.selection, initialScale = 0.6f) + fadeIn(style.motion.fadeIn),
                    exit = scaleOut(style.motion.fadeOut, targetScale = 0.6f) + fadeOut(style.motion.fadeOut),
                ) {
                    PickerIconButton(
                        icon = PickerIcons.Close,
                        contentDescription = stringResource(Res.string.ccp_clear_phone),
                        onClick = {
                            state.clear()
                            focusRequester.requestFocus()
                        },
                        visualSize = 22.dp * size.contentScale,
                        iconSize = 12.dp * size.contentScale,
                        modifier = Modifier.size(style.dimensions.minimumTouchTarget * size.contentScale.coerceAtLeast(0.85f)),
                    )
                }
            }

            FieldHelperText(
                text = when {
                    hasError -> message
                    showHelperText -> PhoneNumberValidator.helperText(state.country)
                    else -> null
                },
                tone = if (hasError) FieldTone.Error else FieldTone.Default,
                style = style,
            )
        }
    }
}

/**
 * The floating label over the number editor with its ghost digits.
 *
 * The editor never moves; only the label does. Resting, it is centered in the field at the number's
 * size; lifted, it sits above the number at label size. Its size and position are interpolated
 * together, so it travels rather than jumping between two states.
 */
@Composable
private fun NumberEditor(
    state: PhoneNumberFieldState,
    enabled: Boolean,
    readOnly: Boolean,
    label: String?,
    placeholder: String?,
    lifted: Boolean,
    active: Boolean,
    hasError: Boolean,
    inputStyle: PhoneNumberInputStyle,
    numberStyle: TextStyle,
    labelStyle: TextStyle,
    style: CountryPickerStyle,
    interactionSource: MutableInteractionSource,
    focusRequester: FocusRequester,
    modifier: Modifier,
    editorModifier: Modifier,
    onDone: () -> Unit,
) {
    val colors = style.colors
    val keyboard = LocalSoftwareKeyboardController.current
    val lift by animateFloatAsState(if (lifted) 1f else 0f, style.motion.layout, label = "labelLift")
    val labelColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.textDisabled
            hasError -> colors.error
            active -> colors.accent
            else -> colors.textSecondary
        },
        animationSpec = style.motion.color,
        label = "labelColor",
    )
    val text = state.textFieldValue.text
    val example = remember(state.country) { PhoneNumberFormatter.exampleNationalNumber(state.country) }
    // A grouped national number is weak-direction digits and spaces; in an RTL locale the groups
    // would reorder. Only the content is pinned LTR — the field stays where the layout puts it.
    val editorStyle = numberStyle.copy(color = if (enabled) colors.textPrimary else colors.textDisabled, textDirection = CountryPickerBidi.LTR)
    // The hint fades in over the second half of the label's lift, so the two never overlap.
    val hintAlpha = ((lift - 0.5f) * 2f).coerceIn(0f, 1f)
    val hint: AnnotatedString? = when {
        hintAlpha <= 0f -> null
        text.isEmpty() && placeholder != null -> AnnotatedString(placeholder)
        // Empty, the real example is the most useful hint; once typing starts, the rest of it is
        // ghosted as zeros, so it shows how much is left without suggesting digits nobody entered.
        text.isEmpty() && example != null -> AnnotatedString(example)
        inputStyle.showGhostDigits && example != null -> ghostDigits(text, example, colors.textTertiary.copy(alpha = GHOST_ALPHA))
        else -> null
    }

    Layout(
        modifier = modifier.pointerInput(enabled) {
            // A tap anywhere in the number area — the label included — focuses the editor. Not a
            // semantics click: the editor itself is the accessible target.
            if (enabled) {
                detectTapGestures {
                    focusRequester.requestFocus()
                    keyboard?.show()
                }
            }
        },
        content = {
            if (label != null) {
                Text(
                    text = label,
                    style = lerp(numberStyle.copy(fontWeight = labelStyle.fontWeight), labelStyle, lift),
                    color = if (lift < 0.02f && !active) colors.textTertiary else labelColor,
                    maxLines = 1,
                    // Hidden from screen readers, which hear the label as the editor's own name,
                    // but still part of the tree for tests and tooling.
                    modifier = Modifier.layoutId(LABEL_ID).semantics { hideFromAccessibility() },
                )
            }
            BasicTextField(
                value = state.textFieldValue,
                onValueChange = state::onTextChanged,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = editorStyle,
                cursorBrush = SolidColor(if (hasError) colors.error else colors.accent),
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                modifier = editorModifier
                    .layoutId(EDITOR_ID)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (hint != null) {
                            Text(
                                text = hint,
                                style = editorStyle.copy(color = colors.textTertiary),
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier
                                    .graphicsLayer { alpha = hintAlpha }
                                    .clearAndSetSemantics {},
                            )
                        }
                        inner()
                    }
                },
            )
        },
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val editor = measurables.first { it.layoutId == EDITOR_ID }.measure(loose.copy(minWidth = constraints.minWidth))
        val labelPlaceable = measurables.firstOrNull { it.layoutId == LABEL_ID }?.measure(loose)
        val reserved = if (labelPlaceable != null) labelStyle.lineHeightPx(this) + LABEL_GAP.roundToPx() else 0
        val width = maxOf(editor.width, constraints.minWidth)
        val height = maxOf(reserved + editor.height, constraints.minHeight)
        layout(width, height) {
            val editorY = if (labelPlaceable != null) reserved + (height - reserved - editor.height) / 2 else (height - editor.height) / 2
            editor.placeRelative(0, editorY)
            if (labelPlaceable != null) {
                // Resting, the label is centered on the whole field — level with the prefix beside
                // it — not on the editor, which sits below the room reserved for the lifted label.
                val resting = (height - labelPlaceable.height) / 2
                val liftedY = editorY - reserved
                labelPlaceable.placeRelative(0, lerp(resting.toFloat(), liftedY.toFloat(), lift).toInt())
            }
        }
    }
}

/**
 * What is left of the example number after the user's digits: the typed part transparent (the
 * editor draws it), the rest faint, with its digits as zeros. Aligned by digit, not by character,
 * so the grouping always continues from where the typed text ends; with tabular figures a zero is
 * exactly a digit wide, so the ghost lines up with the number it completes.
 */
internal fun ghostDigits(typed: String, example: String, ghost: Color): AnnotatedString? {
    val typedDigits = typed.count(Char::isDigit)
    var seen = 0
    var cut = -1
    if (typedDigits == 0) cut = 0
    else {
        for ((index, char) in example.withIndex()) {
            if (char.isDigit()) seen++
            if (seen == typedDigits) {
                cut = index + 1
                break
            }
        }
    }
    if (cut < 0 || cut >= example.length) return null
    return buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Transparent)) { append(typed) }
        withStyle(SpanStyle(color = ghost)) {
            append(example.substring(cut).map { if (it.isDigit()) GHOST_DIGIT else it }.joinToString(""))
        }
    }
}

/** The valid check and number-type badge, or verification progress and the verified mark. */
@Composable
private fun RowScope.TrailingStatus(
    value: PhoneNumberValue,
    typeLabel: String?,
    verified: Boolean,
    verifying: Boolean,
    inputStyle: PhoneNumberInputStyle,
    style: CountryPickerStyle,
) {
    val colors = style.colors
    val showBadge = inputStyle.showNumberType && value.isValid && typeLabel != null && !verified && !verifying
    // Emitted straight into the field's row: a hidden AnimatedVisibility emits nothing, so a hidden
    // badge or check leaves no stray gap behind.
    AnimatedVisibility(
        visible = showBadge,
        enter = fadeIn(style.motion.fadeIn) + expandHorizontally(style.motion.size, expandFrom = Alignment.End),
        exit = fadeOut(style.motion.fadeOut) + shrinkHorizontally(style.motion.size, shrinkTowards = Alignment.End),
    ) {
        // The badge carries the check itself, so a valid number costs the field one element, not two.
        PickerBadge(
            text = typeLabel.orEmpty(),
            container = colors.successSoft,
            content = colors.success,
            icon = PickerIcons.Check,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
    AnimatedVisibility(
        visible = verifying || verified || (inputStyle.showValidIndicator && value.isValid && !showBadge),
        enter = scaleIn(style.motion.selection, initialScale = 0.4f) + fadeIn(style.motion.fadeIn),
        exit = scaleOut(style.motion.fadeOut, targetScale = 0.4f) + fadeOut(style.motion.fadeOut),
    ) {
        Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
            when {
                verifying -> CircularProgressIndicator(color = colors.accent, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                verified -> Icon(
                    PickerIcons.CheckCircle,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(22.dp),
                )
                else -> {
                    // Starts undrawn so the check draws itself in as it scales up.
                    var drawn by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { drawn = true }
                    AnimatedCheckMark(visible = drawn, color = colors.success, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

/** The hairline along the bottom of the field that fills as digits arrive. */
@Composable
private fun BoxScope.NumberProgress(value: PhoneNumberValue, hasError: Boolean, fullWidth: Boolean, style: CountryPickerStyle) {
    val colors = style.colors
    val expected = remember(value.country) { PhoneNumberFormatter.expectedNationalDigits(value.country) }
    val digits = value.nationalNumber.count(Char::isDigit)
    val target = when {
        value.isValid -> 1f
        digits == 0 -> 0f
        else -> (digits.toFloat() / expected).coerceIn(0f, 0.94f)
    }
    val fraction by animateFloatAsState(target, style.motion.layout, label = "phoneProgress")
    val color by animateColorAsState(
        targetValue = when {
            hasError -> colors.error
            value.isValid -> colors.success
            else -> colors.accent
        },
        animationSpec = style.motion.color,
        label = "phoneProgressColor",
    )
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            // Inset clear of the corners so it reads as a gauge, not a border — except under an
            // underlined field, where it fills the underline itself.
            .padding(horizontal = if (fullWidth) 0.dp else PROGRESS_INSET)
            .fillMaxWidth()
            .height(PROGRESS_HEIGHT)
            .drawBehind {
                if (fraction <= 0f) return@drawBehind
                val width = size.width * fraction
                val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, 0f),
                    size = Size(width, size.height),
                    cornerRadius = CornerRadius(size.height / 2),
                )
            },
    )
}

private fun PhoneNumberType.labelRes(): StringResource? = when (this) {
    PhoneNumberType.Mobile -> Res.string.ccp_number_type_mobile
    PhoneNumberType.FixedLine -> Res.string.ccp_number_type_landline
    PhoneNumberType.FixedLineOrMobile -> Res.string.ccp_number_type_mobile_or_landline
    PhoneNumberType.TollFree -> Res.string.ccp_number_type_toll_free
    PhoneNumberType.Voip -> Res.string.ccp_number_type_voip
    else -> null
}

/** True when this verification state means the field should be treated as verified for [value]. */
internal fun PhoneVerificationState.isVerifiedFor(value: PhoneNumberValue): Boolean =
    this is PhoneVerificationState.Verified && e164Number == value.e164Number

/**
 * Scales the font size by [scale] for a compact [com.ezzy.ccp.countrypicker.theme.PhoneFieldSize],
 * leaving an unspecified size alone.
 */
internal fun TextStyle.scaledForPhoneField(scale: Float): TextStyle =
    if (scale == 1f || fontSize.isUnspecified) {
        this
    } else {
        copy(fontSize = fontSize * scale, lineHeight = if (lineHeight.isUnspecified) lineHeight else lineHeight * scale)
    }

private fun TextStyle.lineHeightPx(density: Density): Int = with(density) {
    (if (lineHeight.isUnspecified) fontSize * 1.3f else lineHeight).roundToPx()
}

private const val GHOST_DIGIT = '0'
private const val GHOST_ALPHA = 0.5f
private const val LABEL_ID = "label"
private const val EDITOR_ID = "editor"
private val LABEL_GAP: Dp = 1.dp
private val PROGRESS_HEIGHT: Dp = 2.dp
private val PROGRESS_INSET: Dp = 18.dp

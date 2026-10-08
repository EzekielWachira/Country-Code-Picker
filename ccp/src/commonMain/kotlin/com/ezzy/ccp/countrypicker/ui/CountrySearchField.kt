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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_cancel
import com.ezzy.ccp.resources.ccp_clear_search
import com.ezzy.ccp.resources.ccp_search_hint
import com.ezzy.ccp.resources.ccp_search_label
import org.jetbrains.compose.resources.stringResource

/**
 * The search field at the top of the picker.
 *
 * At rest it is a quiet card; focused, its border turns to the accent and a soft ring blooms around
 * it, so where the keyboard is typing is never in doubt. The clear button appears only once there is
 * something to clear, and [onCancel], when given, adds a Cancel action beside the field while it is
 * focused — the familiar way to back out of a search on iOS, and a convenient one everywhere.
 *
 * Filtering is the caller's job and is expected to be synchronous: there is no search button and no
 * debounce, because local search over a few hundred countries is instant.
 *
 * @param onCancel Clears the query and leaves the field. `null` hides the Cancel action.
 * @param autoFocus Focus the field, and so raise the keyboard, as soon as it appears.
 */
@Composable
public fun CountrySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: UiText = UiText.resource(Res.string.ccp_search_hint),
    onFocusChanged: (Boolean) -> Unit = {},
    onSearchAction: () -> Unit = {},
    onCancel: (() -> Unit)? = null,
    autoFocus: Boolean = false,
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    val colors = style.colors
    val dimensions = style.dimensions
    val shapes = style.shapes
    val motion = style.motion
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchLabel = stringResource(Res.string.ccp_search_label)

    LaunchedEffect(isFocused) { onFocusChanged(isFocused) }
    LaunchedEffect(autoFocus) { if (autoFocus) focusRequester.requestFocus() }

    // A white card reads on a tinted canvas; on a canvas the same color as cards, a sunken fill does.
    val restingContainer = if (colors.background == colors.surface) colors.surfaceSunken else colors.surface
    val hasDepth = colors.background != colors.surface
    val border by animateColorAsState(
        targetValue = when {
            isFocused -> colors.accent
            hasDepth -> colors.hairline
            else -> colors.surfaceSunken
        },
        animationSpec = motion.color,
        label = "searchBorder",
    )
    val ring by animateFloatAsState(if (isFocused) 1f else 0f, motion.layout, label = "searchRing")
    val iconTint by animateColorAsState(
        targetValue = if (isFocused) colors.accent else colors.textTertiary,
        animationSpec = motion.color,
        label = "searchIcon",
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(dimensions.searchFieldHeight)
                .focusRing(ring, dimensions.focusRingWidth, colors.focusRing, shapes.searchField)
                .then(if (hasDepth) Modifier.pickerShadow(style.elevation.searchField, shapes.searchField, colors.shadow) else Modifier)
                .background(restingContainer, shapes.searchField)
                .border(if (isFocused) dimensions.fieldFocusedBorderWidth else dimensions.fieldBorderWidth, border, shapes.searchField)
                .padding(start = 14.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = PickerIcons.Search,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(19.dp),
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = style.typography.search.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accent),
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Results are already filtered; hiding the keyboard reveals them.
                        keyboardController?.hide()
                        onSearchAction()
                    },
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .semantics { contentDescription = searchLabel },
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        // Fading rather than removing the placeholder keeps the text metrics stable,
                        // so the first typed character does not shift the baseline.
                        val placeholderAlpha by animateFloatAsState(
                            targetValue = if (query.isEmpty()) 1f else 0f,
                            animationSpec = motion.fadeIn,
                            label = "searchPlaceholder",
                        )
                        if (placeholderAlpha > 0f) {
                            SingleLineText(
                                text = placeholder.resolve(),
                                style = style.typography.search,
                                color = colors.textTertiary,
                                modifier = Modifier.alpha(placeholderAlpha),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = scaleIn(motion.selection) + fadeIn(motion.fadeIn),
                exit = scaleOut(motion.layout) + fadeOut(motion.fadeOut),
            ) {
                PickerIconButton(
                    icon = PickerIcons.Close,
                    contentDescription = stringResource(Res.string.ccp_clear_search),
                    onClick = { onQueryChange("") },
                    tint = colors.surface,
                    container = colors.textTertiary,
                    visualSize = 20.dp,
                    iconSize = 11.dp,
                )
            }
        }

        if (onCancel != null) {
            AnimatedVisibility(
                visible = isFocused,
                enter = expandHorizontally(motion.size) + fadeIn(motion.fadeIn),
                exit = shrinkHorizontally(motion.size) + fadeOut(motion.fadeOut),
            ) {
                PickerButton(
                    onClick = {
                        onCancel()
                        focusManager.clearFocus()
                    },
                    kind = PickerButtonKind.Text,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 12.dp, end = 4.dp),
                ) {
                    Text(stringResource(Res.string.ccp_cancel))
                }
            }
        }
    }
}

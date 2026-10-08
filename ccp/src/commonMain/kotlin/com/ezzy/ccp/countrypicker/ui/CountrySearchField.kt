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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Search
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_clear_search
import com.ezzy.ccp.resources.ccp_search_hint
import com.ezzy.ccp.resources.ccp_search_label
import org.jetbrains.compose.resources.stringResource

/**
 * The sheet's search field.
 *
 * Morphs from filled-at-rest to outlined-when-focused, matching the design: the container color stays
 * and a primary-colored stroke animates in. Both the color *and* the stroke width animate, so focus is
 * not signalled by hue alone.
 *
 * ### Keyboard
 * The IME action is [ImeAction.Search] and dismisses the keyboard, revealing the results the user just
 * filtered — search is already live on every keystroke, so "Search" has nothing left to submit and
 * getting the keyboard out of the way is the useful behaviour.
 *
 * @param query Current text.
 * @param onQueryChange Called on every keystroke. Filtering is synchronous and undebounced; see
 *   [com.ezzy.ccp.countrypicker.data.CountrySearchEngine].
 * @param onFocusChanged Reported so the sheet can expand and hide the region chips while searching.
 * @param autoFocus Requests focus on first composition. Off by default: opening a sheet straight into
 *   a keyboard hides most of the list, and users who came to browse have to dismiss it first.
 */
@Composable
public fun CountrySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: UiText = UiText.resource(Res.string.ccp_search_hint),
    onFocusChanged: (Boolean) -> Unit = {},
    onSearchAction: () -> Unit = {},
    autoFocus: Boolean = false,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    // Resolved outside the semantics lambda: that block is not a composable scope.
    val searchLabel = stringResource(Res.string.ccp_search_label)

    // Report focus upward so the sheet can react (expand, hide chips) without owning the field.
    androidx.compose.runtime.LaunchedEffect(isFocused) { onFocusChanged(isFocused) }
    androidx.compose.runtime.LaunchedEffect(autoFocus) {
        if (autoFocus) focusRequester.requestFocus()
    }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) colors.searchFocusedBorder else Color.Transparent,
        animationSpec = motion.colorSpec,
        label = "searchBorderColor",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) dimensions.searchBorderWidth else 0.dp,
        animationSpec = motion.dpSpec,
        label = "searchBorderWidth",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shapes.searchField,
        color = colors.searchContainer,
    ) {
        Row(
            modifier = Modifier
                .border(borderWidth, borderColor, shapes.searchField)
                .defaultMinSize(minHeight = dimensions.searchFieldHeight)
                .padding(start = dimensions.searchHorizontalMargin, end = SEARCH_END_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.searchContentSpacing),
        ) {
            Icon(
                imageVector = EzzyIcons.Search,
                contentDescription = null,
                tint = colors.searchPlaceholder,
                modifier = Modifier.size(SEARCH_ICON_SIZE),
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = typography.searchInput.copy(color = colors.searchContent),
                cursorBrush = SolidColor(colors.searchFocusedBorder),
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
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
                        // Fading the placeholder's alpha rather than adding or removing it keeps the
                        // text metrics stable, so the first typed character does not shift the
                        // baseline.
                        val placeholderAlpha by animateFloatAsState(
                            targetValue = if (query.isEmpty()) 1f else 0f,
                            animationSpec = motion.floatSpec,
                            label = "searchPlaceholderAlpha",
                        )
                        if (placeholderAlpha > 0f) {
                            Text(
                                text = placeholder.resolve(),
                                style = typography.searchInput,
                                color = colors.searchPlaceholder,
                                modifier = Modifier.alpha(placeholderAlpha),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = scaleIn(motion.selectionSpring) + fadeIn(motion.fadeIn),
                exit = scaleOut(motion.fadeOut) + fadeOut(motion.fadeOut),
            ) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(dimensions.iconButtonSize),
                ) {
                    Icon(
                        imageVector = EzzyIcons.Close,
                        contentDescription = stringResource(Res.string.ccp_clear_search),
                        tint = colors.searchPlaceholder,
                        modifier = Modifier.size(CLEAR_ICON_SIZE),
                    )
                }
            }
        }
    }
}

private val SEARCH_ICON_SIZE = 22.dp
private val CLEAR_ICON_SIZE = 20.dp
private val SEARCH_END_PADDING = 4.dp

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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryLoadState
import com.ezzy.ccp.countrypicker.state.CountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons
import kotlinx.coroutines.launch

/**
 * The country selection bottom sheet.
 *
 * A standard Material 3 [ModalBottomSheet], which is what gets predictive back, drag-to-dismiss, the
 * scrim, and correct insets for free. The sheet's *motion* is left entirely to Material — animating it
 * by hand is how sheets end up stuck half-open when the keyboard opens mid-drag.
 *
 * Reusable on its own: a host that wants the picker inside its own flow, without a
 * [CountrySelector] field, can call this directly with its own [CountryPickerState].
 *
 * ### Keyboard behaviour
 * `imePadding()` on the content keeps the footer actions and the search field above the keyboard, so
 * Confirm never ends up underneath it.
 *
 * ### Single vs. multi
 * In single-select, tapping a row commits immediately and (per
 * [com.ezzy.ccp.countrypicker.state.CountryPickerConfig.closeOnSingleSelection]) closes the sheet after
 * a brief pause, so the user sees the check mark land before the sheet leaves. In multi-select, a
 * footer appears with Cancel and Confirm, and nothing reaches the caller until Confirm.
 *
 * @param state Picker state, usually from [com.ezzy.ccp.countrypicker.state.rememberCountryPickerState].
 * @param onCountrySelected Single-select commit.
 * @param onSelectionConfirmed Multi-select commit. Called only from Confirm.
 * @param onDismiss Cancel/dismiss. Pending changes are discarded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPickerSheet(
    state: CountryPickerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCountrySelected: (Country) -> Unit = {},
    onSelectionConfirmed: (Set<Country>) -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    title: UiText = UiText.resource(R.string.ccp_select_country),
    subtitle: UiText? = UiText.resource(R.string.ccp_select_country_subtitle),
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
    emptyContent: (@Composable (String) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val config = state.config
    val pendingSelection by state.pendingSelection
    val isSearching by state.isSearching
    val listState = rememberLazyListState()

    /** Animates the sheet out, then reports the dismissal — so back, scrim and close all look alike. */
    fun animateDismiss(after: () -> Unit = {}) {
        scope.launch {
            sheetState.hide()
            onDismiss()
            after()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = colors.sheetContainer,
        contentColor = colors.sheetContent,
        shape = shapes.sheet,
        scrimColor = colors.scrim,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DRAG_HANDLE_TOP_PADDING, bottom = DRAG_HANDLE_BOTTOM_PADDING),
                contentAlignment = Alignment.Center,
            ) {
                val handleLabel = stringResource(R.string.ccp_drag_handle)
                Box(
                    modifier = Modifier
                        .size(width = DRAG_HANDLE_WIDTH, height = DRAG_HANDLE_HEIGHT)
                        .clip(shapes.selectorPill)
                        .background(colors.dragHandle)
                        .semantics { contentDescription = handleLabel },
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Without an explicit height fraction, Column sizes to its unweighted children and
                // SheetBody's weight(1f) below has no real space to distribute — the list would then
                // measure at (near) zero height instead of filling the sheet. The design's own
                // "expanded" sheet mode is effectively this: a tall, generous list area for search
                // and browsing rather than a short peek height.
                .fillMaxHeight(SHEET_HEIGHT_FRACTION)
                // Keeps the search field and footer clear of the keyboard.
                .imePadding(),
        ) {
            SheetHeader(
                title = title,
                subtitle = subtitle,
                selectionCount = pendingSelection.size,
                showReset = config.isMultiSelect && pendingSelection.isNotEmpty(),
                onReset = state::resetPendingSelection,
                onClose = { animateDismiss() },
                colors = colors,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
            )

            if (config.showCurrentSelection && !config.isMultiSelect) {
                CurrentSelectionCard(
                    country = state.confirmedSelection.firstOrNull(),
                    flagShape = if (config.flagsVisible) config.flagShape else
                        com.ezzy.ccp.countrypicker.theme.CountryFlagShape.Hidden,
                    colors = colors,
                    shapes = shapes,
                    dimensions = dimensions,
                    typography = typography,
                    motion = motion,
                    flagContent = flagContent,
                )
            }

            if (config.showSearch && state.loadState !is CountryLoadState.Loading) {
                CountrySearchField(
                    query = state.searchQuery,
                    onQueryChange = state::updateSearchQuery,
                    onFocusChanged = state::onSearchFocusChanged,
                    modifier = Modifier.padding(
                        horizontal = dimensions.searchHorizontalMargin,
                        vertical = SEARCH_VERTICAL_MARGIN,
                    ),
                    colors = colors,
                    shapes = shapes,
                    dimensions = dimensions,
                    typography = typography,
                    motion = motion,
                )
            }

            // Region chips are hidden while searching: relevance-ranked results already cut across
            // regions, so leaving a region filter visible would misrepresent what is on screen.
            AnimatedVisibility(
                visible = config.showRegionFilters &&
                    !isSearching &&
                    state.loadState is CountryLoadState.Loaded,
                enter = fadeIn(motion.fadeIn) + slideInVertically(motion.listItemPlacement) { -it },
                exit = fadeOut(motion.fadeOut) + slideOutVertically(motion.listItemPlacement) { -it },
            ) {
                val regions by state.availableRegions
                CountryRegionFilters(
                    selectedRegion = state.selectedRegion,
                    onRegionSelected = state::selectRegion,
                    regions = regions,
                    modifier = Modifier.padding(bottom = CHIPS_BOTTOM_PADDING),
                    colors = colors,
                    shapes = shapes,
                    dimensions = dimensions,
                    typography = typography,
                    motion = motion,
                )
            }

            SearchResultCount(
                visible = config.showResultCount && isSearching,
                count = state.resultCount.value,
                region = state.selectedRegion,
                colors = colors,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
            )

            SheetBody(
                state = state,
                listState = listState,
                onCountryClick = { country ->
                    state.toggleCountry(country)
                    if (!config.isMultiSelect) {
                        onCountrySelected(country)
                        scope.launch { recentCountryStore.recordSelection(country.iso2Code) }
                        if (config.closeOnSingleSelection) {
                            // A short pause lets the check mark animate in before the sheet leaves;
                            // dismissing instantly makes the selection feel unacknowledged.
                            scope.launch {
                                kotlinx.coroutines.delay(SELECTION_DISMISS_DELAY_MILLIS)
                                sheetState.hide()
                                onDismiss()
                            }
                        }
                    } else if (config.autoConfirmOnMaximum &&
                        config.isAtMaximum(state.pendingSelection.value.size)
                    ) {
                        state.confirmSelection()?.let { confirmed ->
                            onSelectionConfirmed(confirmed)
                            confirmed.forEach {
                                scope.launch { recentCountryStore.recordSelection(it.iso2Code) }
                            }
                            animateDismiss()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = colors,
                shapes = shapes,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
                flagContent = flagContent,
                listItemContent = listItemContent,
                emptyContent = emptyContent,
            )

            SelectionFeedback(
                message = state.feedbackMessage,
                colors = colors,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
            )

            if (config.isMultiSelect && state.loadState is CountryLoadState.Loaded) {
                val canConfirm by state.canConfirm
                MultiSelectFooter(
                    selectionCount = pendingSelection.size,
                    canConfirm = canConfirm,
                    onCancel = { animateDismiss() },
                    onConfirm = {
                        state.confirmSelection()?.let { confirmed ->
                            onSelectionConfirmed(confirmed)
                            confirmed.forEach {
                                scope.launch { recentCountryStore.recordSelection(it.iso2Code) }
                            }
                            animateDismiss()
                        }
                    },
                    colors = colors,
                    shapes = shapes,
                    dimensions = dimensions,
                    typography = typography,
                    motion = motion,
                )
            } else {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

/** Title, optional count, subtitle, Reset and Close. */
@Composable
private fun SheetHeader(
    title: UiText,
    subtitle: UiText?,
    selectionCount: Int,
    showReset: Boolean,
    onReset: () -> Unit,
    onClose: () -> Unit,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensions.sheetHeaderStartPadding,
                end = HEADER_END_PADDING,
                bottom = HEADER_BOTTOM_PADDING,
            ),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(HEADER_SPACING),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val titleText = title.resolve()
            // Animating on the count keeps "Select country" stable while only the suffix changes.
            AnimatedContent(
                targetState = selectionCount,
                transitionSpec = { fadeIn(motion.fadeIn) togetherWith fadeOut(motion.fadeOut) },
                label = "sheetTitleCount",
            ) { count ->
                Text(
                    text = if (count > 1) {
                        stringResource(R.string.ccp_title_with_count, titleText, count)
                    } else {
                        titleText
                    },
                    style = typography.sheetTitle,
                    color = colors.sheetContent,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() },
                )
            }

            if (subtitle != null) {
                Text(
                    text = subtitle.resolve(),
                    style = typography.sheetSubtitle,
                    color = colors.sheetSecondaryContent,
                    modifier = Modifier.padding(top = SUBTITLE_TOP_PADDING),
                )
            }
        }

        AnimatedVisibility(
            visible = showReset,
            enter = fadeIn(motion.fadeIn),
            exit = fadeOut(motion.fadeOut),
        ) {
            TextButton(onClick = onReset) {
                Text(
                    text = stringResource(R.string.ccp_reset),
                    style = typography.buttonLabel,
                    color = colors.sectionLabel,
                )
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.size(dimensions.iconButtonSize),
        ) {
            Icon(
                imageVector = EzzyIcons.Close,
                contentDescription = stringResource(R.string.ccp_close),
                tint = colors.sheetSecondaryContent,
                // EzzyIcons.Close's ImageVector declares a 512dp intrinsic size (an artifact of the
                // vector-asset import, not a deliberate design size). Icon() falls back to a vector's
                // own intrinsic size whenever it isn't given one explicitly, so leaving this off is
                // exactly what made the close button balloon over the whole header.
                modifier = Modifier.size(CLOSE_ICON_SIZE),
            )
        }
    }
}

/**
 * The "Current selection" card.
 *
 * Filled surface, no border — the tonal fill already separates it from the sheet, and adding a stroke
 * makes it compete with the search field below it for visual weight.
 */
@Composable
private fun CurrentSelectionCard(
    country: Country?,
    flagShape: com.ezzy.ccp.countrypicker.theme.CountryFlagShape,
    colors: CountryPickerColors,
    shapes: CountryPickerShapes,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    AnimatedVisibility(
        visible = country != null,
        enter = fadeIn(motion.fadeIn),
        exit = fadeOut(motion.fadeOut),
    ) {
        if (country == null) return@AnimatedVisibility
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensions.sheetHorizontalPadding,
                    vertical = CARD_VERTICAL_MARGIN,
                )
                .clip(shapes.currentSelectionCard)
                .background(colors.currentSelectionContainer)
                .padding(dimensions.currentSelectionPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CARD_CONTENT_SPACING),
        ) {
            CountryFlag(
                country = country,
                size = CARD_FLAG_SIZE,
                shape = flagShape,
                colors = colors,
                dimensions = dimensions,
                motion = motion,
                flagContent = flagContent,
            )
            Column {
                Text(
                    text = stringResource(R.string.ccp_current_selection),
                    style = typography.countryMetadata,
                    color = colors.sheetSecondaryContent,
                )
                Text(
                    text = country.displayName,
                    style = typography.countryName,
                    color = colors.sheetContent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * "12 results" while searching.
 *
 * A polite live region, so a screen reader mentions the count when it settles without interrupting the
 * user mid-keystroke.
 */
@Composable
private fun SearchResultCount(
    visible: Boolean,
    count: Int,
    region: com.ezzy.ccp.countrypicker.model.CountryRegion?,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    AnimatedVisibility(
        visible = visible && count > 0,
        enter = fadeIn(motion.fadeIn),
        exit = fadeOut(motion.fadeOut),
    ) {
        val base = androidx.compose.ui.platform.LocalContext.current.resources
            .getQuantityString(R.plurals.ccp_result_count, count, count)
        val text = if (region != null) {
            stringResource(R.string.ccp_result_count_in_region, base, stringResource(region.labelRes))
        } else {
            base
        }
        AnimatedContent(
            targetState = text,
            transitionSpec = { fadeIn(motion.fadeIn) togetherWith fadeOut(motion.fadeOut) },
            label = "resultCount",
        ) { value ->
            Text(
                text = value,
                style = typography.resultCount,
                color = colors.sheetSecondaryContent,
                modifier = Modifier
                    .padding(
                        start = dimensions.rowHorizontalPadding,
                        end = dimensions.rowHorizontalPadding,
                        bottom = COUNT_BOTTOM_PADDING,
                    )
                    .semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

/** Chooses between the list and the loading/error/empty states. */
@Composable
private fun SheetBody(
    state: CountryPickerState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onCountryClick: (Country) -> Unit,
    colors: CountryPickerColors,
    shapes: CountryPickerShapes,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
    modifier: Modifier = Modifier,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
    emptyContent: (@Composable (String) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val isEmptyByConfig by state.isEmptyByConfiguration
    val isEmptyBySearch by state.isEmptyBySearch

    // Scroll back to the top whenever the result set changes identity, so a new query does not leave
    // the user looking at the middle of a list they have not seen the start of.
    LaunchedEffect(state.searchQuery, state.selectedRegion) {
        if (listState.firstVisibleItemIndex > 0) listState.scrollToItem(0)
    }

    Box(modifier = modifier) {
        when {
            state.loadState is CountryLoadState.Loading -> CountryLoadingState(
                colors = colors,
                dimensions = dimensions,
            )

            state.loadState is CountryLoadState.Error -> CountryErrorState(
                onRetry = { scope.launch { state.retryLoad() } },
                offline = (state.loadState as CountryLoadState.Error).offline,
                colors = colors,
                shapes = shapes,
                typography = typography,
            )

            isEmptyByConfig -> CountryNoneAvailableState(
                colors = colors,
                shapes = shapes,
                typography = typography,
            )

            isEmptyBySearch -> if (emptyContent != null) {
                emptyContent(state.searchQuery)
            } else {
                CountrySearchEmptyState(
                    query = state.searchQuery,
                    onClearSearch = state::clearSearch,
                    colors = colors,
                    shapes = shapes,
                    typography = typography,
                )
            }

            else -> CountryList(
                state = state,
                onCountryClick = onCountryClick,
                listState = listState,
                colors = colors,
                shapes = shapes,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
                flagContent = flagContent,
                listItemContent = listItemContent,
            )
        }
    }
}

/** The "you can select at most N" notice. */
@Composable
private fun SelectionFeedback(
    message: UiText?,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(motion.fadeIn) + slideInVertically(motion.listItemPlacement) { it },
        exit = fadeOut(motion.fadeOut),
    ) {
        Text(
            text = message?.resolve().orEmpty(),
            style = typography.helperText,
            color = colors.error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensions.rowHorizontalPadding,
                    vertical = FEEDBACK_VERTICAL_PADDING,
                )
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

/** Cancel / Confirm footer for multi-select. */
@Composable
private fun MultiSelectFooter(
    selectionCount: Int,
    canConfirm: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    colors: CountryPickerColors,
    shapes: CountryPickerShapes,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    Column {
        HorizontalDivider(color = colors.regionChipBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = dimensions.sheetHorizontalPadding,
                    vertical = FOOTER_VERTICAL_PADDING,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.ccp_cancel),
                    style = typography.buttonLabel,
                    color = colors.sectionLabel,
                )
            }

            // Fading the disabled state rather than snapping it makes the button's availability
            // legible as the count crosses the minimum.
            val confirmAlpha by animateFloatAsState(
                targetValue = if (canConfirm) 1f else DISABLED_BUTTON_ALPHA,
                animationSpec = motion.floatSpec,
                label = "confirmAlpha",
            )

            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                shape = shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.checkboxChecked.copy(alpha = confirmAlpha),
                ),
            ) {
                AnimatedContent(
                    targetState = selectionCount,
                    transitionSpec = { fadeIn(motion.fadeIn) togetherWith fadeOut(motion.fadeOut) },
                    label = "confirmCount",
                ) { count ->
                    Text(
                        text = if (count > 0) {
                            stringResource(R.string.ccp_confirm_count, count)
                        } else {
                            stringResource(R.string.ccp_confirm)
                        },
                        style = typography.buttonLabel,
                    )
                }
            }
        }
    }
}

/**
 * Multi-selection variant of [CountryPickerSheet], for hosts driving the sheet directly.
 *
 * Kept as a thin wrapper so a multi-select call site does not have to remember to pass
 * `onSelectionConfirmed` instead of `onCountrySelected`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCountryPickerSheet(
    state: CountryPickerState,
    onSelectionConfirmed: (Set<Country>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    title: UiText = UiText.resource(R.string.ccp_select_country),
    subtitle: UiText? = UiText.resource(R.string.ccp_select_country_subtitle),
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    CountryPickerSheet(
        state = state,
        onDismiss = onDismiss,
        modifier = modifier,
        onSelectionConfirmed = onSelectionConfirmed,
        sheetState = sheetState,
        recentCountryStore = recentCountryStore,
        title = title,
        subtitle = subtitle,
        colors = colors,
        shapes = shapes,
        dimensions = dimensions,
        typography = typography,
        motion = motion,
        flagContent = flagContent,
        listItemContent = listItemContent,
    )
}

/**
 * Fraction of screen height the sheet's content occupies.
 *
 * Matches the design's "expanded" sheet mode — tall enough that search results and a long country
 * list have real room, short of full-height so the scrim above stays visible as a dismiss target.
 */
private const val SHEET_HEIGHT_FRACTION = 0.86f

private val DRAG_HANDLE_WIDTH = 32.dp
private val DRAG_HANDLE_HEIGHT = 4.dp
private val DRAG_HANDLE_TOP_PADDING = 6.dp
private val DRAG_HANDLE_BOTTOM_PADDING = 6.dp
/**
 * Visual size of the close glyph inside its 48dp [CountryPickerDimensions.iconButtonSize] touch
 * target — standard Material close-icon sizing. The touch target itself stays 48dp; only the glyph
 * is capped here.
 */
private val CLOSE_ICON_SIZE = 24.dp
private val HEADER_END_PADDING = 8.dp
private val HEADER_BOTTOM_PADDING = 8.dp
private val HEADER_SPACING = 8.dp
private val SUBTITLE_TOP_PADDING = 2.dp
private val CARD_VERTICAL_MARGIN = 8.dp
private val CARD_CONTENT_SPACING = 12.dp
private val CARD_FLAG_SIZE = 26.dp
private val SEARCH_VERTICAL_MARGIN = 4.dp
private val CHIPS_BOTTOM_PADDING = 10.dp
private val COUNT_BOTTOM_PADDING = 6.dp
private val FEEDBACK_VERTICAL_PADDING = 4.dp
private val FOOTER_VERTICAL_PADDING = 12.dp
private const val DISABLED_BUTTON_ALPHA = 0.5f

/** Pause before dismissing after a single selection, so the check mark is visible. */
private const val SELECTION_DISMISS_DELAY_MILLIS = 160L

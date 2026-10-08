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
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryLoadState
import com.ezzy.ccp.countrypicker.state.CountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryListStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.PickerPresentation
import com.ezzy.ccp.countrypicker.theme.QuickPicksStyle
import com.ezzy.ccp.countrypicker.theme.SheetHeaderStyle
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_close
import com.ezzy.ccp.resources.ccp_confirm
import com.ezzy.ccp.resources.ccp_confirm_count
import com.ezzy.ccp.resources.ccp_drag_handle
import com.ezzy.ccp.resources.ccp_reset
import com.ezzy.ccp.resources.ccp_result_count
import com.ezzy.ccp.resources.ccp_result_count_in_region
import com.ezzy.ccp.resources.ccp_select_country
import com.ezzy.ccp.resources.ccp_select_country_subtitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The country picker, presented over the current screen.
 *
 * How it is presented follows the style's
 * [com.ezzy.ccp.countrypicker.theme.CountryPickerLayout.presentation]: by default a bottom sheet on
 * phones and a centered dialog once the window is wide enough to make a sheet look stranded — tablets,
 * foldables, desktop. The content is the same [CountryPickerPanel] either way.
 *
 * ### Single vs. multiple selection
 * In single selection a tap commits immediately and — per
 * [com.ezzy.ccp.countrypicker.state.CountryPickerConfig.closeOnSingleSelection] — the picker closes
 * a beat later, so the check mark visibly lands first. In multiple selection a confirmation bar
 * appears, and nothing reaches the caller until Confirm.
 *
 * Reusable on its own: a host that wants the picker without a [CountrySelector] field can call this
 * with its own [CountryPickerState].
 *
 * @param onCountrySelected Single-selection commit.
 * @param onSelectionConfirmed Multiple-selection commit, from Confirm only.
 * @param onDismiss Close, back, scrim tap or drag down. Pending changes are discarded.
 */
@Composable
public fun CountryPickerSheet(
    state: CountryPickerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCountrySelected: (Country) -> Unit = {},
    onSelectionConfirmed: (Set<Country>) -> Unit = {},
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    title: UiText = UiText.resource(Res.string.ccp_select_country),
    subtitle: UiText? = UiText.resource(Res.string.ccp_select_country_subtitle),
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
    emptyContent: (@Composable (String) -> Unit)? = null,
) {
    val layout = style.layout
    val windowWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    val presentation = when (layout.presentation) {
        PickerPresentation.Adaptive ->
            if (windowWidth >= layout.wideScreenBreakpoint) PickerPresentation.Dialog else PickerPresentation.BottomSheet
        else -> layout.presentation
    }

    CountryPickerTheme(style) {
        when (presentation) {
            PickerPresentation.Dialog -> PickerDialog(onDismiss = onDismiss, style = style, modifier = modifier) {
                CountryPickerPanel(
                    state = state,
                    onCountrySelected = onCountrySelected,
                    onSelectionConfirmed = onSelectionConfirmed,
                    onClose = onDismiss,
                    recentCountryStore = recentCountryStore,
                    title = title,
                    subtitle = subtitle,
                    style = style,
                    flagContent = flagContent,
                    listItemContent = listItemContent,
                    emptyContent = emptyContent,
                )
            }
            else -> PickerBottomSheet(
                onDismiss = onDismiss,
                fullScreen = presentation == PickerPresentation.FullScreenSheet,
                style = style,
                modifier = modifier,
            ) { close ->
                CountryPickerPanel(
                    state = state,
                    onCountrySelected = onCountrySelected,
                    onSelectionConfirmed = onSelectionConfirmed,
                    onClose = close,
                    recentCountryStore = recentCountryStore,
                    title = title,
                    subtitle = subtitle,
                    style = style,
                    flagContent = flagContent,
                    listItemContent = listItemContent,
                    emptyContent = emptyContent,
                )
            }
        }
    }
}

/** [CountryPickerSheet] for multiple selection, so a call site cannot pass the wrong callback. */
@Composable
public fun MultiCountryPickerSheet(
    state: CountryPickerState,
    onSelectionConfirmed: (Set<Country>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    title: UiText = UiText.resource(Res.string.ccp_select_country),
    subtitle: UiText? = UiText.resource(Res.string.ccp_select_country_subtitle),
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    CountryPickerSheet(
        state = state,
        onDismiss = onDismiss,
        modifier = modifier,
        onSelectionConfirmed = onSelectionConfirmed,
        recentCountryStore = recentCountryStore,
        title = title,
        subtitle = subtitle,
        style = style,
        flagContent = flagContent,
        listItemContent = listItemContent,
    )
}

/**
 * The complete picker — header, search, region filter, quick picks, list, every loading and empty
 * state, and the multiple-selection bar — with no container around it.
 *
 * [CountryPickerSheet] wraps this in a sheet or dialog. Use it directly to put the picker inline: on
 * a full-screen route, inside a pane of a two-pane layout, or in an onboarding step.
 *
 * @param onClose Invoked by the close button, and after a single selection when the config closes on
 *   selection. `null` hides the close button and keeps the panel in place after selection.
 */
@Composable
public fun CountryPickerPanel(
    state: CountryPickerState,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    onSelectionConfirmed: (Set<Country>) -> Unit = {},
    onClose: (() -> Unit)? = null,
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    title: UiText = UiText.resource(Res.string.ccp_select_country),
    subtitle: UiText? = UiText.resource(Res.string.ccp_select_country_subtitle),
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
    emptyContent: (@Composable (String) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val haptics = rememberPickerHaptics()
    val config = state.config
    val layout = style.layout
    val pending by state.pendingSelection
    val isSearching by state.isSearching
    val listState = rememberLazyListState()
    val loaded = state.loadState is CountryLoadState.Loaded

    // Recent and suggested countries live in the carousel when there is one, and stay in place in
    // the full list rather than being pulled up into their own sections.
    SideEffect { state.quickPicksInList = layout.quickPicks == QuickPicksStyle.Sections }

    fun commit(country: Country) {
        val wasPending = state.isPending(country)
        state.toggleCountry(country)
        if (!config.isMultiSelect) {
            haptics.select()
            onCountrySelected(country)
            scope.launch { recentCountryStore.recordSelection(country.iso2Code) }
            if (config.closeOnSingleSelection && onClose != null) {
                // A short pause lets the check mark land before the picker leaves; closing
                // instantly makes the selection feel unacknowledged.
                scope.launch {
                    delay(style.motion.selectionDismissDelayMillis)
                    onClose()
                }
            }
            return
        }
        val nowPending = state.isPending(country)
        when {
            // The tap was refused: the selection is full.
            !wasPending && !nowPending -> haptics.limitReached()
            else -> haptics.toggle(nowPending)
        }
        if (config.autoConfirmOnMaximum && config.isAtMaximum(state.pendingSelection.value.size)) {
            state.confirmSelection()?.let { confirmed ->
                onSelectionConfirmed(confirmed)
                confirmed.forEach { scope.launch { recentCountryStore.recordSelection(it.iso2Code) } }
                onClose?.invoke()
            }
        }
    }

    fun confirm() {
        state.confirmSelection()?.let { confirmed ->
            haptics.select()
            onSelectionConfirmed(confirmed)
            confirmed.forEach { scope.launch { recentCountryStore.recordSelection(it.iso2Code) } }
            onClose?.invoke()
        }
    }

    // A new query or region is a new result set: start at its top.
    LaunchedEffect(state.searchQuery, state.selectedRegion) {
        if (listState.firstVisibleItemIndex > 0) listState.scrollToItem(0)
    }

    // The selection-limit notice explains itself, then gets out of the way.
    LaunchedEffect(state.feedbackMessage) {
        if (state.feedbackMessage != null) {
            delay(FEEDBACK_MILLIS)
            state.consumeFeedback()
        }
    }

    val scrolled by remember { derivedStateOf { listState.canScrollBackward } }
    val dividerAlpha by animateFloatAsState(if (scrolled) 1f else 0f, style.motion.layout, label = "headerDivider")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(style.colors.background)
            // Keeps the search field and the confirmation bar above the keyboard.
            .imePadding(),
    ) {
        PanelHeader(
            title = title,
            subtitle = subtitle,
            count = if (config.isMultiSelect) pending.size else 0,
            showReset = config.isMultiSelect && pending.isNotEmpty(),
            onReset = state::resetPendingSelection,
            onClose = onClose,
            style = style,
        )

        if (config.showSearch && state.loadState !is CountryLoadState.Loading) {
            CountrySearchField(
                query = state.searchQuery,
                onQueryChange = state::updateSearchQuery,
                onFocusChanged = state::onSearchFocusChanged,
                onCancel = state::clearSearch,
                style = style,
                modifier = Modifier.padding(
                    start = style.dimensions.sheetHorizontalPadding,
                    end = style.dimensions.sheetHorizontalPadding,
                    top = 4.dp,
                    bottom = 12.dp,
                ),
            )
        }

        // Region filters step aside while searching: ranked results already cut across regions,
        // and a visible filter would misrepresent what is on screen.
        val regions by state.availableRegions
        AnimatedVisibility(
            visible = layout.showRegionFilters && !isSearching && loaded && regions.size > 1,
            enter = fadeIn(style.motion.fadeIn) + expandVertically(style.motion.size),
            exit = fadeOut(style.motion.fadeOut) + shrinkVertically(style.motion.size),
        ) {
            val counts by state.regionCounts
            CountryRegionFilters(
                selectedRegion = state.selectedRegion,
                onRegionSelected = state::selectRegion,
                regions = regions,
                counts = counts,
                style = style,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(0.75.dp)
                .alpha(dividerAlpha)
                .background(style.colors.hairline),
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            PanelBody(
                state = state,
                listState = listState,
                onCountryClick = ::commit,
                bottomInset = if (config.isMultiSelect && loaded) CONFIRM_BAR_CLEARANCE else 28.dp,
                style = style,
                flagContent = flagContent,
                listItemContent = listItemContent,
                emptyContent = emptyContent,
            )

            Column(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SelectionFeedback(state.feedbackMessage, style)
                if (config.isMultiSelect && loaded) {
                    val canConfirm by state.canConfirm
                    ConfirmBar(
                        selection = pending.toList(),
                        canConfirm = canConfirm,
                        onConfirm = ::confirm,
                        style = style,
                    )
                }
            }
        }
    }
}

/** Title, subtitle, live selection count, Reset and Close. */
@Composable
private fun PanelHeader(
    title: UiText,
    subtitle: UiText?,
    count: Int,
    showReset: Boolean,
    onReset: () -> Unit,
    onClose: (() -> Unit)?,
    style: CountryPickerStyle,
) {
    val colors = style.colors
    val large = style.layout.headerStyle == SheetHeaderStyle.Large
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = style.dimensions.sheetHorizontalPadding, end = 8.dp, top = if (large) 4.dp else 0.dp, bottom = if (large) 14.dp else 8.dp),
        verticalAlignment = if (large) Alignment.Top else Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(top = if (large) 6.dp else 0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = title.resolve(),
                    style = if (large) style.typography.title else style.typography.countryNameSelected,
                    color = colors.textPrimary,
                    maxLines = 2,
                    modifier = Modifier.weight(1f, fill = false).semantics { heading() },
                )
                CountBadge(count, style)
            }
            if (subtitle != null && large) {
                Text(
                    text = subtitle.resolve(),
                    style = style.typography.subtitle,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
        AnimatedVisibility(visible = showReset, enter = fadeIn(style.motion.fadeIn), exit = fadeOut(style.motion.fadeOut)) {
            PickerButton(onClick = onReset, kind = PickerButtonKind.Text, contentPadding = PaddingValues(horizontal = 10.dp)) {
                Text(stringResource(Res.string.ccp_reset))
            }
        }
        if (onClose != null) {
            PickerIconButton(
                icon = PickerIcons.Close,
                contentDescription = stringResource(Res.string.ccp_close),
                onClick = onClose,
            )
        }
    }
}

/** The live count beside the title in multiple selection: a pill whose number rolls as it changes. */
@Composable
private fun CountBadge(count: Int, style: CountryPickerStyle) {
    AnimatedVisibility(
        visible = count > 0,
        enter = scaleIn(style.motion.selection) + fadeIn(style.motion.fadeIn),
        exit = scaleOut(style.motion.layout) + fadeOut(style.motion.fadeOut),
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = 24.dp)
                .widthIn(min = 24.dp)
                .background(style.colors.accent, CircleShape)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    val up = targetState > initialState
                    (slideInVertically(style.motion.offset) { if (up) it else -it } + fadeIn(style.motion.fadeIn)) togetherWith
                        (slideOutVertically(style.motion.offset) { if (up) -it else it } + fadeOut(style.motion.fadeOut))
                },
                label = "headerCount",
            ) { value ->
                Text(text = value.toString(), style = style.typography.tileLabel, color = style.colors.onAccent)
            }
        }
    }
}

/** The list, or whichever loading, error or empty state applies. */
@Composable
private fun PanelBody(
    state: CountryPickerState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onCountryClick: (Country) -> Unit,
    bottomInset: androidx.compose.ui.unit.Dp,
    style: CountryPickerStyle,
    flagContent: (@Composable (Country) -> Unit)?,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)?,
    emptyContent: (@Composable (String) -> Unit)?,
) {
    val scope = rememberCoroutineScope()
    val isEmptyByConfig by state.isEmptyByConfiguration
    val isEmptyBySearch by state.isEmptyBySearch
    val isSearching by state.isSearching
    val layout = style.layout
    val scroll = rememberScrollState()

    when {
        state.loadState is CountryLoadState.Loading -> CountryLoadingState(style = style)

        state.loadState is CountryLoadState.Error -> Box(Modifier.fillMaxSize().verticalScroll(scroll)) {
            CountryErrorState(
                onRetry = { scope.launch { state.retryLoad() } },
                offline = (state.loadState as CountryLoadState.Error).offline,
                style = style,
            )
        }

        isEmptyByConfig -> Box(Modifier.fillMaxSize().verticalScroll(scroll)) { CountryNoneAvailableState(style = style) }

        isEmptyBySearch -> Box(Modifier.fillMaxSize().verticalScroll(scroll)) {
            if (emptyContent != null) {
                emptyContent(state.searchQuery)
            } else {
                val suggestions by state.searchSuggestions
                CountrySearchEmptyState(
                    query = state.searchQuery,
                    onClearSearch = state::clearSearch,
                    suggestions = suggestions,
                    onSuggestionClick = { country ->
                        state.clearSearch()
                        onCountryClick(country)
                    },
                    style = style,
                )
            }
        }

        else -> {
            val picks by state.quickPicks
            val pending by state.pendingSelection
            val showCarousel = layout.quickPicks == QuickPicksStyle.Carousel &&
                !isSearching && state.selectedRegion == null && picks.isNotEmpty()
            val showCount = layout.showResultCount && isSearching
            // Items added ahead of the sections shift the rail's lazy-list indices by this much.
            val leadingItems = (if (showCarousel) 1 else 0) + (if (showCount) 1 else 0)
            val selected = if (state.config.isMultiSelect || pending.isNotEmpty()) pending else state.confirmedSelection
            // The rail indexes the alphabetical list, which is not what is on screen while a
            // search is narrowing it — so it is hidden then, rather than offering dead jumps.
            val index = rememberCountryListIndex(state.sections.value)
            val showRail = layout.showAlphabetIndex && !isSearching && index.letters.size > 1
            val railClearance = if (showRail) style.railClearance() else 0.dp

            CountryList(
                state = state,
                onCountryClick = onCountryClick,
                listState = listState,
                contentPadding = PaddingValues(bottom = bottomInset, end = railClearance),
                style = style,
                flagContent = flagContent,
                listItemContent = listItemContent,
                leadingContent = {
                    if (showCarousel) {
                        item(key = "quick_picks", contentType = "quick_picks") {
                            CountryQuickPicks(
                                picks = picks,
                                selectedCountries = selected,
                                onPick = onCountryClick,
                                style = style,
                                flagContent = flagContent,
                                // The carousel runs edge to edge rather than inside the rows' margin,
                                // so it needs the rest of the rail's width on top of the list's inset.
                                modifier = Modifier.padding(
                                    top = 2.dp,
                                    bottom = 4.dp,
                                    end = if (showRail) style.dimensions.indexRailWidth + RAIL_GAP - railClearance else 0.dp,
                                ),
                            )
                        }
                    }
                    if (showCount) {
                        item(key = "result_count", contentType = "result_count") {
                            ResultCount(state.resultCount.value, state.selectedRegion, style)
                        }
                    }
                },
            )

            if (showRail) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                    CountryIndexRail(
                        letters = index.letters,
                        onLetterSelected = { letter ->
                            index.lazyIndexOf(letter)?.let { target ->
                                scope.launch { listState.scrollToItem(target + leadingItems) }
                            }
                        },
                        style = style,
                        modifier = Modifier.padding(top = 8.dp, bottom = bottomInset),
                    )
                }
            }
        }
    }
}

/** "12 results in Africa", announced politely as it settles. */
@Composable
private fun ResultCount(count: Int, region: com.ezzy.ccp.countrypicker.model.CountryRegion?, style: CountryPickerStyle) {
    if (count == 0) return
    val base = pluralStringResource(Res.plurals.ccp_result_count, count, count)
    val text = if (region != null) {
        stringResource(Res.string.ccp_result_count_in_region, base, stringResource(region.labelRes))
    } else {
        base
    }
    CapsLabel(
        text = text,
        style = style.typography.sectionLabel,
        color = style.colors.textSecondary,
        modifier = Modifier
            .padding(start = style.dimensions.groupHorizontalMargin + 4.dp, top = 2.dp, bottom = 8.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
    )
}

/** The "you can select at most N" notice, floating above the confirmation bar. */
@Composable
private fun SelectionFeedback(message: UiText?, style: CountryPickerStyle) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(style.motion.fadeIn) + slideInVertically(style.motion.offset) { it / 2 },
        exit = fadeOut(style.motion.fadeOut),
    ) {
        val text = message?.resolve().orEmpty()
        Text(
            text = text,
            style = style.typography.helper,
            color = style.colors.warning,
            modifier = Modifier
                .padding(bottom = 8.dp, start = 24.dp, end = 24.dp)
                .pickerShadow(style.elevation.tile, style.shapes.chip, style.colors.shadow)
                .background(style.colors.surfaceRaised, style.shapes.chip)
                .background(style.colors.warningSoft, style.shapes.chip)
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

/**
 * The multiple-selection bar: the chosen flags stacked like avatars, how many are chosen, and
 * Confirm — floating above the list so the selection is always in view.
 */
@Composable
private fun ConfirmBar(
    selection: List<Country>,
    canConfirm: Boolean,
    onConfirm: () -> Unit,
    style: CountryPickerStyle,
) {
    val colors = style.colors
    val shape = style.shapes.floatingBar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .pickerShadow(style.elevation.floatingBar, shape, colors.shadow)
            .background(colors.surfaceRaised, shape)
            .border(0.75.dp, colors.hairline, shape)
            .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // The selection as faces, not words: the title's badge and the button already state the
        // count, and a third "4 countries selected" would only crowd — and truncate — the bar.
        StackedFlags(countries = selection, ringColor = colors.surfaceRaised, max = BAR_FLAGS)
        Spacer(Modifier.weight(1f))
        PickerButton(onClick = onConfirm, enabled = canConfirm) {
            Text(
                if (selection.isEmpty()) {
                    stringResource(Res.string.ccp_confirm)
                } else {
                    stringResource(Res.string.ccp_confirm_count, selection.size)
                },
            )
        }
    }
}

/** A Material bottom sheet — predictive back, drag to dismiss, scrim and insets for free. */
@Composable
private fun PickerBottomSheet(
    onDismiss: () -> Unit,
    fullScreen: Boolean,
    style: CountryPickerStyle,
    modifier: Modifier,
    content: @Composable (close: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val close: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier.then(if (fullScreen) Modifier.statusBarsPadding() else Modifier),
        containerColor = style.colors.background,
        contentColor = style.colors.textPrimary,
        shape = style.shapes.sheet,
        scrimColor = style.colors.scrim,
        dragHandle = {
            val label = stringResource(Res.string.ccp_drag_handle)
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(width = 38.dp, height = 5.dp)
                        .clip(CircleShape)
                        .background(style.colors.textTertiary.copy(alpha = 0.5f))
                        .semantics { contentDescription = label },
                )
            }
        },
    ) {
        Box(Modifier.fillMaxHeight(if (fullScreen) 1f else style.layout.sheetHeightFraction)) {
            content(close)
        }
    }
}

/** A centered, elevated card for wide windows, scaling in from the middle. */
@Composable
private fun PickerDialog(
    onDismiss: () -> Unit,
    style: CountryPickerStyle,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val visible = remember { MutableTransitionState(false).apply { targetState = true } }
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            AnimatedVisibility(
                visibleState = visible,
                enter = fadeIn(style.motion.fadeIn) + scaleIn(style.motion.layout, initialScale = 0.94f),
            ) {
                Box(
                    modifier = modifier
                        .widthIn(max = style.layout.dialogMaxWidth)
                        .heightIn(max = style.layout.dialogMaxHeight)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .pickerShadow(style.elevation.dialog, style.shapes.dialog, style.colors.shadow)
                        .clip(style.shapes.dialog)
                        .background(style.colors.background)
                        .padding(top = 18.dp),
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Extra trailing inset for the list while the A–Z rail is shown, so no row, highlight or carousel
 * tile runs underneath it. Grouped and card lists already sit inside a margin; plain rows nearly
 * reach the edge.
 */
private fun CountryPickerStyle.railClearance(): androidx.compose.ui.unit.Dp {
    val rowEndInset = if (layout.listStyle == CountryListStyle.Plain) PLAIN_ROW_INSET else dimensions.groupHorizontalMargin
    return (dimensions.indexRailWidth + RAIL_GAP - rowEndInset).coerceAtLeast(0.dp)
}

private val RAIL_GAP = 4.dp

/** List padding that keeps the last rows clear of the floating confirmation bar. */
private val CONFIRM_BAR_CLEARANCE = 104.dp

/** Flags the confirmation bar stacks before folding the rest into a "+N" disc. */
private const val BAR_FLAGS = 5

/** How long the selection-limit notice stays up. */
private const val FEEDBACK_MILLIS = 2600L

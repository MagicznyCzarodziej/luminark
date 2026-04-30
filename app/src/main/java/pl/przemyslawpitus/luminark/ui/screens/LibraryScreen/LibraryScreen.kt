package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import pl.przemyslawpitus.luminark.ui.modifiers.action
import pl.przemyslawpitus.luminark.ui.modifiers.block
import pl.przemyslawpitus.luminark.ui.modifiers.blockWhen
import pl.przemyslawpitus.luminark.ui.modifiers.dpadHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.Text
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.ui.TestTags
import pl.przemyslawpitus.luminark.ui.components.EntriesList.EntriesList
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.components.EntriesList.NameDisplayStrategy
import pl.przemyslawpitus.luminark.ui.components.Poster.Poster
import pl.przemyslawpitus.luminark.ui.navigation.Destination

/**
 * Holds the focus-navigation state for the entries list.
 *
 * Each composable entry registers its FocusRequester here via DisposableEffect,
 * so that external components (TopBar, Sidebar, Letters) can programmatically
 * scroll to and focus any entry by index.
 *
 * Recreated via remember(uiState.entries) whenever the entries list changes
 * (e.g. after filtering), which resets focusRequesters and _focusedIndex.
 */
class EntriesListState(
    val entries: List<ListEntryUiModel>,
) {
    /** Set by EntriesList via SideEffect — gives access to the LazyColumn scroll state. */
    internal var lazyListState: LazyListState? = null

    /** Index of the currently focused entry. Used to restore focus after navigating away. */
    internal val _focusedIndex = mutableIntStateOf(0)

    /**
     * Map of entry index -> FocusRequester, populated by each visible entry's DisposableEffect.
     * Only currently composed (on-screen) entries have entries here.
     */
    internal val focusRequesters = mutableMapOf<Int, FocusRequester>()

    /** The first letter of the currently focused entry's sort name (for letter-tracking). */
    val activeLetter: Char?
        get() = entries.getOrNull(_focusedIndex.intValue)
            ?.name?.sortName?.first()?.uppercaseChar()

    /** Called by each entry's onFocusChanged to update the tracked index. */
    internal fun onEntryFocused(index: Int) {
        _focusedIndex.intValue = index
    }

    /** Find the first entry whose sort name starts with the given letter. */
    fun findEntryIndexForLetter(char: Char): Int {
        return if (char == '#') 0
        else entries.indexOfFirst { it.name.sortName.startsWith(char, ignoreCase = true) }
    }
}


@Composable
fun LibraryScreen(
    navController: NavController,
) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val posterPath by viewModel.posterPath.collectAsState()

    val scope = rememberCoroutineScope()

    val symbolsListState = rememberLazyListState()
    val letterFocusRequesters = remember { mutableMapOf<Char, FocusRequester>() }
    var focusedLetterIndex by remember { mutableIntStateOf(-1) }

    // Recreated when entries change (filter applied) — old focusRequesters are discarded,
    // and DisposableEffect in EntriesList re-registers on the new instance.
    val entriesListState = remember(uiState.entries) {
        EntriesListState(uiState.entries)
    }

    val librarySymbols = uiState.entries.map { it.name.sortName.first().uppercaseChar() }.distinct()
    val symbols = listOf('#') + ('A'..'Z').intersect(librarySymbols)

    val activeLetter = entriesListState.activeLetter
    val activeLetterIndex by remember(activeLetter, symbols) {
        derivedStateOf { symbols.indexOf(activeLetter) }
    }

    /**
     * Cancels any in-flight focus coroutine before launching a new one.
     * Without this, rapid D-pad inputs cause older retry loops to steal focus
     * back to outdated targets (e.g. pressing Right then quickly Down would
     * fight over which entry to focus).
     */
    val activeFocusJob = remember { object { var job: Job? = null } }

    // ── Focus helpers ────────────────────────────────────────────────

    /**
     * Scroll the entries list to [index] and request focus on that entry.
     *
     * Uses index-2 for scrollToItem to prevent BringIntoView from nudging
     * the list after focus lands (the focused item needs some items above it
     * to be visible, otherwise Compose auto-scrolls to fully reveal it).
     *
     * The retry loop (60 x 50ms) handles the case where the target entry
     * hasn't been composed yet after scrolling — LazyColumn composes items
     * asynchronously, so the FocusRequester may not be registered immediately.
     */
    fun scrollAndFocusEntry(index: Int) {
        activeFocusJob.job?.cancel()
        activeFocusJob.job = scope.launch {
            val listState = entriesListState.lazyListState ?: return@launch
            val isVisible = listState.layoutInfo.visibleItemsInfo.any { it.index == index }
            if (!isVisible) {
                listState.scrollToItem(maxOf(0, index - 2))
            }
            entriesListState.focusRequesters[index]?.let { req ->
                try { req.requestFocus(); return@launch } catch (_: Exception) {}
            }
            repeat(60) { attempt ->
                delay(50)
                val req = entriesListState.focusRequesters[index]
                if (req != null) {
                    try { req.requestFocus(); return@launch } catch (_: Exception) {}
                }
            }
        }
    }

    /** Scroll the alphabet column to [index] and request focus on that letter.
     *  Negative index (e.g. no active letter found) falls back to the first letter. */
    fun scrollAndFocusLetter(index: Int) {
        val safeIndex = maxOf(0, index)
        activeFocusJob.job?.cancel()
        activeFocusJob.job = scope.launch {
            val halfViewport = symbolsListState.layoutInfo.viewportSize.height / 2
            symbolsListState.scrollToItem(safeIndex, -halfViewport)
            repeat(60) { attempt ->
                delay(50)
                val letter = symbols.getOrNull(safeIndex) ?: return@launch
                val req = letterFocusRequesters[letter]
                if (req != null) {
                    try { req.requestFocus(); return@launch } catch (_: Exception) {}
                }
            }
        }
    }

    var previousActiveLetterIndex by remember { mutableIntStateOf(-1) }

    // Auto-scroll the alphabet column to keep the active letter visible
    // as the user scrolls through entries. The "changed" guard prevents
    // re-animating when scrolling through multiple entries starting with
    // the same letter (which would cause visible jittering).
    LaunchedEffect(activeLetterIndex) {
        if (activeLetterIndex < 0) return@LaunchedEffect
        if (activeLetterIndex == previousActiveLetterIndex) return@LaunchedEffect
        previousActiveLetterIndex = activeLetterIndex
        val halfViewport = symbolsListState.layoutInfo.viewportSize.height / 2
        symbolsListState.animateScrollToItem(activeLetterIndex, -halfViewport)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            handleNavigationEvent(event, navController)
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ładowanie...")
        }
    } else {
        Box {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                if (posterPath != null) {
                    Poster(
                        rootRelativeDirectoryPath = posterPath!!,
                        modifier = Modifier.fillMaxWidth(0.37f)
                    )
                }
                // Alphabet Column
                //
                // D-pad rules:
                // - Left: blocked via focusProperties — nothing useful to the left
                // - Up/Down at edges: blocked via onPreviewKeyEvent (conditional on position)
                //   Note: focusProperties { up/down = Cancel } can't be used here because
                //   it prevents LazyColumn from scrolling to reveal off-screen items above/below
                // - Right: custom behavior (scroll to + focus the last focused entry)
                LazyColumn(
                    modifier = Modifier
                        .testTag(TestTags.LETTERS_COLUMN)
                        .height(300.dp)
                        .width(28.dp)
                        .padding(start = 8.dp)
                        .dpadHandler(
                            onLeft = block(),
                            onUp = blockWhen { focusedLetterIndex == 0 },
                            onDown = blockWhen { focusedLetterIndex == symbols.lastIndex },
                            onRight = action { scrollAndFocusEntry(entriesListState._focusedIndex.intValue) },
                        )
                        .focusGroup()
                        .align(Alignment.CenterVertically),
                    state = symbolsListState,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    itemsIndexed(symbols) { index, letter ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        val isActive = letter == activeLetter
                        val letterFocusRequester = remember { FocusRequester() }
                        letterFocusRequesters[letter] = letterFocusRequester

                        if (isFocused) {
                            focusedLetterIndex = index
                        }

                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                            TextButton(
                                onClick = {
                                    val entryIndex = entriesListState.findEntryIndexForLetter(letter)
                                    if (entryIndex >= 0) {
                                        entriesListState._focusedIndex.intValue = entryIndex
                                        scrollAndFocusEntry(entryIndex)
                                    }
                                },
                                contentPadding = PaddingValues(0.dp),
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .testTag(TestTags.letterTag(index))
                                    .semantics { selected = isActive }
                                    .focusRequester(letterFocusRequester)
                                    .heightIn(min = 1.dp)
                                    .background(
                                        color = when {
                                            isFocused -> Color.White
                                            isActive -> Color.White.copy(alpha = 0.3f)
                                            else -> Color.Transparent
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Text(
                                    text = letter.toString(),
                                    color = when {
                                        isFocused -> Color.Black
                                        isActive -> Color.White
                                        else -> Color.White.copy(alpha = 0.5f)
                                    }
                                )
                            }
                        }
                    }
                }
                Column {
                    TopBar(
                        onFilterChanged = viewModel::filterEntries,
                        // Called when user presses Down from a TopBar button —
                        // returns focus to the last focused entry in the list.
                        onNavigateDown = {
                            scrollAndFocusEntry(entriesListState._focusedIndex.intValue)
                        },
                    )
                    EntriesList(
                        entries = uiState.entries,
                        nameDisplayStrategy = NameDisplayStrategy.LIBRARY,
                        modifier = Modifier
                            .testTag(TestTags.ENTRIES_LIST)
                            .padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                            )
                            // Left from entries: move focus to the matching letter in the alphabet
                            .dpadHandler(
                                onLeft = action { scrollAndFocusLetter(activeLetterIndex) },
                            )
                            .focusGroup(),
                        state = entriesListState
                    )
                }
            }
            // Sidebar is positioned as an overlay at the right edge.
            // D-pad Right from entries does NOT spatially navigate to the sidebar
            // because the sidebar Box shares the same x-region as the entries list.
            // Instead, the sidebar captures focus when Compose's spatial search
            // reaches the right edge on its own.
            Sidebar(
                tags = uiState.tags,
                rebuildLibrary = { viewModel.rebuildLibrary() },
                filterByTag = viewModel::filterByTag,
                // Called when user presses Left/Right from a sidebar item —
                // returns focus to the last focused entry.
                onExitSidebar = {
                    scrollAndFocusEntry(entriesListState._focusedIndex.intValue)
                },
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

private fun handleNavigationEvent(
    event: NavigationEvent,
    navController: NavController
) {
    when (event) {
        is NavigationEvent.ToFilmSeries -> navController.navigate(Destination.FilmSeries(event.filmSeriesId.id))
        is NavigationEvent.ToMediaGrouping -> navController.navigate(Destination.MediaGrouping(event.groupingId.id))
        is NavigationEvent.ToSeries -> navController.navigate(Destination.Series(event.seriesId.id))
    }
}
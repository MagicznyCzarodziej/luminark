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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.Text
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.ui.components.EntriesList.EntriesList
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.components.EntriesList.NameDisplayStrategy
import pl.przemyslawpitus.luminark.ui.components.Poster.Poster
import pl.przemyslawpitus.luminark.ui.navigation.Destination

class EntriesListState(
    val entries: List<ListEntryUiModel>,
) {
    internal var lazyListState: LazyListState? = null
    internal val _focusedIndex = mutableIntStateOf(0)
    internal val focusRequesters = mutableMapOf<Int, FocusRequester>()

    val activeLetter: Char?
        get() = entries.getOrNull(_focusedIndex.intValue)
            ?.name?.sortName?.first()?.uppercaseChar()

    internal fun onEntryFocused(index: Int) {
        _focusedIndex.intValue = index
    }

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

    val entriesListState = remember(uiState.entries) {
        EntriesListState(uiState.entries)
    }

    val librarySymbols = uiState.entries.map { it.name.sortName.first().uppercaseChar() }.distinct()
    val symbols = listOf('#') + ('A'..'Z').intersect(librarySymbols)

    val activeLetter = entriesListState.activeLetter
    val activeLetterIndex by remember(activeLetter, symbols) {
        derivedStateOf { symbols.indexOf(activeLetter) }
    }

    // Tracks the active focus-navigation coroutine so a newer request cancels stale ones
    val activeFocusJob = remember { object { var job: Job? = null } }

    // ── Focus helpers ────────────────────────────────────────────────

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

    fun scrollAndFocusLetter(index: Int) {
        activeFocusJob.job?.cancel()
        activeFocusJob.job = scope.launch {
            val halfViewport = symbolsListState.layoutInfo.viewportSize.height / 2
            symbolsListState.scrollToItem(index, -halfViewport)
            repeat(60) { attempt ->
                delay(50)
                val letter = symbols.getOrNull(index) ?: return@launch
                val req = letterFocusRequesters[letter]
                if (req != null) {
                    try { req.requestFocus(); return@launch } catch (_: Exception) {}
                }
            }
        }
    }

    var previousActiveLetterIndex by remember { mutableIntStateOf(-1) }

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
                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .width(28.dp)
                        .padding(start = 8.dp)
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            when (event.key) {
                                Key.DirectionUp -> focusedLetterIndex == 0
                                Key.DirectionDown -> focusedLetterIndex == symbols.lastIndex
                                Key.DirectionLeft -> true
                                Key.DirectionRight -> {
                                    scrollAndFocusEntry(entriesListState._focusedIndex.intValue)
                                    true
                                }
                                else -> false
                            }
                        }
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
                        onNavigateDown = {
                            scrollAndFocusEntry(entriesListState._focusedIndex.intValue)
                        },
                    )
                    EntriesList(
                        entries = uiState.entries,
                        nameDisplayStrategy = NameDisplayStrategy.LIBRARY,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                            )
                            .onPreviewKeyEvent { event ->
                                if (event.type == KeyEventType.KeyDown
                                    && event.key == Key.DirectionLeft
                                ) {
                                    val letterIndex = if (activeLetterIndex >= 0) activeLetterIndex else 0
                                    scrollAndFocusLetter(letterIndex)
                                    true
                                } else false
                            }
                            .focusGroup(),
                        state = entriesListState
                    )
                }
            }
            Sidebar(
                rebuildLibrary = { viewModel.rebuildLibrary() },
                filterByTag = viewModel::filterByTag,
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
package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.ui.components.EntriesList.EntriesList
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.components.EntriesList.NameDisplayStrategy
import pl.przemyslawpitus.luminark.ui.components.Poster.Poster
import pl.przemyslawpitus.luminark.ui.navigation.Destination

class EntriesListState(
    val entries: List<ListEntryUiModel>,
    private val coroutineScope: CoroutineScope
) {
    internal var lazyListState: LazyListState? = null
    internal val focusRequesters = mutableMapOf<Int, FocusRequester>()

    fun scrollToLetter(char: Char) {
        val index =
            if (char == '#') 0
            else {
                entries.indexOfFirst {
                    it.name.sortName.startsWith(char, ignoreCase = true)
                }
            }

        if (index != -1) {
            coroutineScope.launch {
                lazyListState?.scrollToItem(index)
                delay(100)
                focusRequesters[index]?.requestFocus()
            }
        }
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

    val entriesListState = remember(uiState.entries) {
        EntriesListState(uiState.entries, scope)
    }

    val librarySymbols = uiState.entries.map { it.name.name.first().uppercaseChar() }.distinct()
    val symbols = listOf('#') + ('A'..'Z').intersect(librarySymbols)

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
                        .focusRestorer()
                        .align(Alignment.CenterVertically),
                    state = symbolsListState,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(symbols) { letter ->
                        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()

                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) { // Remove margin between buttons
                            TextButton(
                                onClick = { entriesListState.scrollToLetter(letter) },
                                contentPadding = PaddingValues(0.dp),
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .heightIn(min = 1.dp)
                                    .background(
                                        color = if (isFocused) Color.White else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Text(
                                    text = letter.toString(),
                                    color = if (isFocused) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
                Column {
                    TopBar(
                        onFilterChanged = viewModel::filterEntries
                    )
                    EntriesList(
                        entries = uiState.entries,
                        nameDisplayStrategy = NameDisplayStrategy.LIBRARY,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
                            .focusRestorer(),
                        state = entriesListState
                    )
                }
            }
            Sidebar(
                rebuildLibrary = { viewModel.rebuildLibrary() },
                modifier = Modifier.align(Alignment.CenterEnd)
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
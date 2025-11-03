package pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.FilmView
import pl.przemyslawpitus.luminark.ui.SeasonView
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.ClickableLibraryListEntry
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries.FilmEntry

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MediaGroupingScreen(
    navController: NavController,
) {
    val viewModel: MediaGroupingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToMediaGroupingSeason -> println("TODO Navigate to new screen ${event.seasonId}")
            }
        }
    }

    if (uiState.isLoading) {
        Text("Loading...")
    } else {
        Column {
            Text(
                "Media Groupings: ${uiState.mediaGrouping!!.name.name}" + (uiState.mediaGrouping!!.name.alternativeName?.let { "(${it})" }
                    ?: ""),
                fontSize = 30.sp,
                color = Color(0xFFF5F5F5),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
            ) {
                itemsIndexed(uiState.mediaGrouping!!.entries) { index, entry ->
                    val focusRequester = remember { FocusRequester() }

                    when (entry) {
                        is FilmView -> {
                            ClickableLibraryListEntry(
                                focusRequester = focusRequester,
                                lastFocusedIndex = lastFocusedIndex,
                                onFocusChange = { isFocused: Boolean ->
                                    if (isFocused) {
                                        lastFocusedIndex = index
                                    }
                                },
                                index = index,
                                onEntryClick = {
                                    viewModel.playVideo(entry.videoFiles.first().absolutePath)
                                },
                            ) {
                                FilmEntry(entry)
                            }
                        }

                        is SeasonView -> {
                            ClickableLibraryListEntry(
                                focusRequester = focusRequester,
                                lastFocusedIndex = lastFocusedIndex,
                                onFocusChange = { isFocused: Boolean ->
                                    if (isFocused) {
                                        lastFocusedIndex = index
                                    }
                                },
                                index = index,
                                onEntryClick = {
                                    viewModel.onMediaGroupingSeasonClick(entry)
                                },
                            ) {
                                Text(
                                    text = entry.name,
                                    color = Color(0xFFF5F5F5),
                                    fontSize = 22.sp,
                                )
                            }
                        }
                    }

                    if (index == lastFocusedIndex) {
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                }
            }
        }
    }
}
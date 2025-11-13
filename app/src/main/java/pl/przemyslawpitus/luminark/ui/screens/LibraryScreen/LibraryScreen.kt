package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.components.EntriesList.EntriesList
import pl.przemyslawpitus.luminark.ui.components.Poster.Poster
import pl.przemyslawpitus.luminark.ui.navigation.Destination

@Composable
fun LibraryScreen(
    navController: NavController,
) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val posterPath by viewModel.posterPath.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToFilmSeries -> navController.navigate(Destination.FilmSeries(event.filmSeriesId.id))
                is NavigationEvent.ToMediaGrouping -> navController.navigate(Destination.MediaGrouping(event.groupingId.id))
                is NavigationEvent.ToSeries -> navController.navigate(Destination.Series(event.seriesId.id))
            }
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
                Column {
                    TopBar()
                    EntriesList(
                        entries = uiState.entries,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
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

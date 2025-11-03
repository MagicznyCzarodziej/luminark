package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.LibraryList

@Composable
fun LibraryScreen(
    navController: NavController,
) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToFilmSeries -> navController.navigate(Destination.Series.createRoute(event.filmSeriesId.id))
                is NavigationEvent.ToMediaGrouping -> navController.navigate(Destination.MediaGrouping.createRoute(event.groupingId.id))
                is NavigationEvent.ToSeries -> navController.navigate(Destination.Series.createRoute(event.seriesId.id))
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
        Column {
            Text(
                text = "Biblioteka",
                fontSize = 30.sp,
                color = Color(0xFFF5F5F5),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            LibraryList(
                entries = uiState.entries,
                onEntryClick = viewModel::onEntryClick
            )
        }
    }
}
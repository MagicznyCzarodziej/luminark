package pl.przemyslawpitus.luminark.ui.screens.SeriesScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayout
import pl.przemyslawpitus.luminark.ui.navigation.Destination

@Composable
fun SeriesScreen(
    navController: NavController,
) {
    val viewModel: SeriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToSeriesEpisodes -> navController.navigate(Destination.Season.createRoute(event.episodesGroupId.id))
            }
        }
    }

    if (uiState.isLoading) {
        Text("Loading...")
    } else {
        ListWithPosterLayout(
            posterData = uiState.posterBytes,
            entries = uiState.entries!!,
            title = uiState.name!!.name,
            subtitle = uiState.name!!.alternativeName,
            breadcrumbs = "Biblioteka",
            tags = uiState.tags,
        )
    }
}
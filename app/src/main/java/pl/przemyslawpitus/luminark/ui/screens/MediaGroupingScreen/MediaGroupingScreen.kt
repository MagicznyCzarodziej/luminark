package pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayout

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MediaGroupingScreen(
    navController: NavController,
) {
    val viewModel: MediaGroupingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToMediaGroupingSeason -> println("TODO Navigate to new screen ${event.seasonId}") // TODO
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
            breadcrumbs = uiState.breadcrumbs!!,
            tags = uiState.tags,
        )
    }
}
package pl.przemyslawpitus.luminark.ui.screens.EpisodesScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayout

@Composable
fun EpisodesScreen() {
    val viewModel: EpisodesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Text("Loading...")
    } else {
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
}
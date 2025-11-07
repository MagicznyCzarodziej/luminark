package pl.przemyslawpitus.luminark.ui.screens.FilmSeriesScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayout
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayoutProps

@Composable
fun FilmSeriesScreen() {
    val viewModel: FilmSeriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.isLoading) {
        ListWithPosterLayout(
            ListWithPosterLayoutProps(
                posterPath = uiState.posterPath,
                entries = uiState.entries!!,
                title = uiState.name!!.name,
                subtitle = uiState.name!!.alternativeName,
                breadcrumbs = uiState.breadcrumbs!!,
                tags = uiState.tags,
            )
        )
    }
}
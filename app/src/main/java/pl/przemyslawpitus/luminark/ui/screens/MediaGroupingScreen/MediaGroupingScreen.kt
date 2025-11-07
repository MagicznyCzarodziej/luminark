package pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayout
import pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout.ListWithPosterLayoutProps
import pl.przemyslawpitus.luminark.ui.navigation.Destination

@Composable
fun MediaGroupingScreen(
    navController: NavController,
) {
    val viewModel: MediaGroupingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToMediaGroupingEpisodesGroup -> navController.navigate(
                    Destination.MediaGroupingEpisodesGroup(
                        event.mediaGroupingId.id,
                        event.episodesGroupId.id,
                    )
                )
            }
        }
    }

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
package pl.przemyslawpitus.luminark.ui.screens.MediaGroupingEpisodesGroupScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.EpisodesGroup
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.MediaGrouping
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterFetcher
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class MediaGroupingEpisodesGroupUiState(
    val entries: List<ListEntryUiModel>? = null,
    val name: Name? = null,
    val tags: Set<String> = emptySet(),
    val posterPath: PosterFetcher.PosterPath? = null,
    val breadcrumbs: String? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class MediaGroupingEpisodesGroupViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {

    private val route = savedStateHandle.toRoute<Destination.MediaGroupingEpisodesGroup>()
    private val mediaGroupingId = route.mediaGroupingId
    private val episodesGroupId = route.episodesGroupId

    private val _uiState = MutableStateFlow(MediaGroupingEpisodesGroupUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadEpisodesGroup(episodesGroupId)
    }

    private fun loadEpisodesGroup(episodesGroupId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val mediaGrouping = libraryRepository.getTopLevelEntries()
                .filterIsInstance<MediaGrouping>()
                .find { it.id.id == mediaGroupingId}!!

            val mediaGroupingEpisodesGroup = mediaGrouping.entries
                .filterIsInstance<EpisodesGroup>()
                .find { it.id.id == episodesGroupId }

            val entries = mediaGroupingEpisodesGroup!!.episodes.map {
                ListEntryUiModel(
                    name = it.name,
                    type = ListEntryUiModel.Type.Single,
                    onClick = { playVideo(it.rootRelativePath) },
                    onFocus = { }
                )
            }

            _uiState.value = MediaGroupingEpisodesGroupUiState(
                entries = entries,
                posterPath = PosterFetcher.PosterPath(mediaGroupingEpisodesGroup.rootRelativePosterPath),
                isLoading = false,
                name = mediaGroupingEpisodesGroup.name,
                breadcrumbs = "Biblioteka / ${mediaGrouping.name.name}",
                tags = mediaGrouping.tags,
            )
        }
    }
}
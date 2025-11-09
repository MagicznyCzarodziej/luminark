package pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.EntryId
import pl.przemyslawpitus.luminark.domain.library.EpisodesGroup
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.MediaGrouping
import pl.przemyslawpitus.luminark.domain.library.MediaGroupingFilm
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterFetcher
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class MediaGroupingUiState(
    val entries: List<ListEntryUiModel>? = null,
    val name: Name? = null,
    val tags: Set<String> = emptySet(),
    val posterPath: PosterFetcher.PosterPath? = null,
    val breadcrumbs: String? = null,
    val isLoading: Boolean = true,
)

sealed class NavigationEvent {
    data class ToMediaGroupingEpisodesGroup(val mediaGroupingId: EntryId, val episodesGroupId: EntryId) :
        NavigationEvent()
}

@HiltViewModel
class MediaGroupingViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {

    private val route = savedStateHandle.toRoute<Destination.MediaGrouping>()
    private val mediaGroupingId = route.mediaGroupingId

    private val _uiState = MutableStateFlow(MediaGroupingUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadMediaGrouping(mediaGroupingId)
    }

    private fun loadMediaGrouping(mediaGroupingId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val mediaGrouping = libraryRepository.getTopLevelEntries()
                .filterIsInstance<MediaGrouping>()
                .find { it.id.id == mediaGroupingId }

            val entries = mediaGrouping!!.entries.map {
                when (it) {
                    is EpisodesGroup -> ListEntryUiModel(
                        name = it.name,
                        type = ListEntryUiModel.Type.PlayablesGroup(it.episodes.size),
                        onClick = { onMediaGroupingEpisodesGroupClick(mediaGrouping.id, it.id) },
                        onFocus = { }
                    )

                    is MediaGroupingFilm -> ListEntryUiModel(
                        name = it.name,
                        type = ListEntryUiModel.Type.Single,
                        onClick = { playVideo(it.rootRelativePath) },
                        onFocus = { }
                    )

                    else -> {
                        throw RuntimeException()
                    }
                }
            }

            _uiState.value = MediaGroupingUiState(
                entries = entries,
                posterPath = PosterFetcher.PosterPath(mediaGrouping.rootRelativePath),
                isLoading = false,
                name = mediaGrouping.name,
                breadcrumbs = "Biblioteka",
                tags = mediaGrouping.tags,
            )
        }
    }

    private fun onMediaGroupingEpisodesGroupClick(mediaGroupingId: EntryId, episodesGroupId: EntryId) {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.ToMediaGroupingEpisodesGroup(mediaGroupingId, episodesGroupId))
        }
    }
}
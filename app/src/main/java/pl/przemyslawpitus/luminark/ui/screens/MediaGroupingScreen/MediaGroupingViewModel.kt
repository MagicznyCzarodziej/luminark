package pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.EntryId
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.ui.MediaGroupingView
import pl.przemyslawpitus.luminark.ui.SeasonView
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class MediaGroupingUiState(
    val mediaGrouping: MediaGroupingView? = null,
    val isLoading: Boolean = true,
)

sealed class NavigationEvent {
    data class ToMediaGroupingSeason(val seasonId: EntryId) : NavigationEvent()
}


@HiltViewModel
class MediaGroupingViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {

    private val _uiState = MutableStateFlow(MediaGroupingUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        val mediaGroupingId: String? = savedStateHandle[Destination.MediaGrouping.groupingIdArg]
        if (mediaGroupingId != null) {
            loadMediaGrouping(mediaGroupingId)
        }
    }

    private fun loadMediaGrouping(mediaGroupingId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val mediaGrouping = libraryRepository.getTopLevelEntries()
                .filterIsInstance<MediaGroupingView>()
                .find { it.id.id == mediaGroupingId }

            _uiState.value = MediaGroupingUiState(mediaGrouping = mediaGrouping, isLoading = false)
        }
    }

    fun onMediaGroupingSeasonClick(season: SeasonView) {
        viewModelScope.launch {
           _navigationEvent.send(NavigationEvent.ToMediaGroupingSeason(season.id))
        }
    }
}
package pl.przemyslawpitus.luminark.ui.screens.EpisodesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.ui.EpisodeView
import pl.przemyslawpitus.luminark.ui.SeriesView
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class EpisodesUiState(
    val episodes: List<EpisodeView>? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class EpisodesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {

    private val _uiState = MutableStateFlow(EpisodesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val seasonId: String? = savedStateHandle[Destination.Season.seasonIdArg]
        if (seasonId != null) {
            loadSeason(seasonId)
        }
    }

    private fun loadSeason(seasonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val episodes = libraryRepository.getTopLevelEntries()
                .filterIsInstance<SeriesView>()
                .flatMap { it.seasons }
                .find { it.id.id == seasonId }
                ?.episodes ?: emptyList()

            _uiState.value = EpisodesUiState(episodes = episodes, isLoading = false)
        }
    }
}
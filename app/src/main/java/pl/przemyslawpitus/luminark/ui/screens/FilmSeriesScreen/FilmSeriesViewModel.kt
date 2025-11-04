package pl.przemyslawpitus.luminark.ui.screens.FilmSeriesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.ui.FilmSeriesView
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class FilmSeriesUiState(
    val filmSeriesView: FilmSeriesView? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class FilmSeriesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {

    private val _uiState = MutableStateFlow(FilmSeriesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val filmSeriesId: String? = savedStateHandle[Destination.FilmSeries.filmSeriesIdArg]
        if (filmSeriesId != null) {
            loadFilmSeries(filmSeriesId)
        }
    }

    private fun loadFilmSeries(filmSeriesId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val filmSeriesView = libraryRepository.getTopLevelEntries()
                .filterIsInstance<FilmSeriesView>()
                .find { it.id.id == filmSeriesId }

            _uiState.value = FilmSeriesUiState(filmSeriesView = filmSeriesView, isLoading = false)
        }
    }
}
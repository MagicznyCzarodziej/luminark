package pl.przemyslawpitus.luminark.ui.screens.FilmSeriesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.FilmSeries
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterFetcher
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class FilmSeriesUiState(
    val entries: List<ListEntryUiModel>? = null,
    val name: Name? = null,
    val tags: Set<String> = emptySet(),
    val posterPath: PosterFetcher.PosterPath? = null,
    val breadcrumbs: String? = null,
    val isLoading: Boolean = true,
)
@HiltViewModel
class FilmSeriesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {

    private val route = savedStateHandle.toRoute<Destination.FilmSeries>()
    private val filmSeriesId = route.filmSeriesId

    private val _uiState = MutableStateFlow(FilmSeriesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFilmSeries(filmSeriesId)
    }

    private fun loadFilmSeries(filmSeriesId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val filmSeries = libraryRepository.getTopLevelEntries()
                .filterIsInstance<FilmSeries>()
                .find { it.id.id == filmSeriesId }

            val entries = filmSeries!!.films.map {
                ListEntryUiModel(
                    name = it.name,
                    type = ListEntryUiModel.Type.Single,
                    onClick = { playVideo(it.videoFiles.first().absolutePath) },
                    onFocus = { }
                )
            }

            _uiState.value = FilmSeriesUiState(
                entries = entries,
                posterPath = PosterFetcher.PosterPath(filmSeries.rootRelativePath),
                isLoading = false,
                name = filmSeries.name,
                breadcrumbs = "Biblioteka / ${filmSeries.name.name}",
                tags = emptySet(),
            )
        }
    }
}
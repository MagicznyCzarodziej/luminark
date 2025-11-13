package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.EntryId
import pl.przemyslawpitus.luminark.domain.library.FilmSeries
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.MediaGrouping
import pl.przemyslawpitus.luminark.domain.library.Series
import pl.przemyslawpitus.luminark.domain.library.StandaloneFilm
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject

data class LibraryUiState(
    val entries: List<ListEntryUiModel> = emptyList(),
    val isLoading: Boolean = true,
)

sealed class NavigationEvent {
    data class ToSeries(val seriesId: EntryId) : NavigationEvent()
    data class ToFilmSeries(val filmSeriesId: EntryId) : NavigationEvent()
    data class ToMediaGrouping(val groupingId: EntryId) : NavigationEvent()
}

const val LIBRARY_ROOT_PATH = "/Filmy"

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val videoPlayer: VideoPlayer,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = libraryRepository.entries
        .map { entries ->
            LibraryUiState(
                entries = entries.map {
                    ListEntryUiModel(
                        name = it.name,
                        type = when (it) {
                            is StandaloneFilm -> ListEntryUiModel.Type.Single
                            is Series -> ListEntryUiModel.Type.Series(it.seasons.size)
                            is MediaGrouping -> ListEntryUiModel.Type.Grouping(it.entries.size)
                            is FilmSeries -> ListEntryUiModel.Type.PlayablesGroup(it.films.size)
                        },
                        onClick = {
                            viewModelScope.launch {
                                when (it) {
                                    is Series -> _navigationEvent.send(NavigationEvent.ToSeries(it.id))

                                    is StandaloneFilm -> videoPlayer.playVideo(it.videoFiles.first().absolutePath)

                                    is MediaGrouping -> _navigationEvent.send(NavigationEvent.ToMediaGrouping(it.id))

                                    is FilmSeries -> _navigationEvent.send(NavigationEvent.ToFilmSeries(it.id))
                                }
                            }
                        },
                        onFocus = { onEntryFocused(it) }
                    )
                },
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            // Rozpoczyna kolekcjonowanie, gdy UI jest aktywne i utrzymuje je przez 5s po zniknięciu UI
            started = SharingStarted.WhileSubscribed(5_000),
            // Stan początkowy, zanim cokolwiek przepłynie z repozytorium
            initialValue = LibraryUiState(isLoading = true)
        )

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _posterPath = MutableStateFlow<Path?>(null)
    val posterPath: StateFlow<Path?> = _posterPath

    init {
        viewModelScope.launch {
            libraryRepository.initialize(Paths.get(LIBRARY_ROOT_PATH))
        }
    }

    fun rebuildLibrary() {
        viewModelScope.launch {
            libraryRepository.initialize(Paths.get(LIBRARY_ROOT_PATH), true)
        }
    }

    private fun onEntryFocused(entry: LibraryEntry) {
        viewModelScope.launch {
            _posterPath.value = entry.rootRelativePath
        }
    }
}

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
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.ui.FilmSeriesView
import pl.przemyslawpitus.luminark.ui.FilmView
import pl.przemyslawpitus.luminark.ui.MediaGroupingView
import pl.przemyslawpitus.luminark.ui.SeriesView
import pl.przemyslawpitus.luminark.ui.TopLevelLibraryEntry
import javax.inject.Inject

data class LibraryUiState(
    val entries: List<TopLevelLibraryEntry> = emptyList(),
    val isLoading: Boolean = true
)

sealed class NavigationEvent {
    data class ToSeries(val seriesId: EntryId) : NavigationEvent()
    data class ToFilmSeries(val filmSeriesId: EntryId) : NavigationEvent()
    data class ToMediaGrouping(val groupingId: EntryId) : NavigationEvent()
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val videoPlayer: VideoPlayer,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = libraryRepository.entries
        .map { entries ->
            LibraryUiState(
                entries = entries,
                isLoading = false
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

    init {
        viewModelScope.launch {
            libraryRepository.initialize()
        }
    }

    fun onEntryClick(entry: TopLevelLibraryEntry) {
        viewModelScope.launch {
            when (entry) {
                is SeriesView -> _navigationEvent.send(NavigationEvent.ToSeries(entry.id))

                is FilmView -> videoPlayer.playVideo(entry.videoFiles.first().absolutePath)

                is MediaGroupingView -> _navigationEvent.send(NavigationEvent.ToMediaGrouping(entry.id))

                is FilmSeriesView -> _navigationEvent.send(NavigationEvent.ToFilmSeries(entry.id))
            }
        }
    }
}

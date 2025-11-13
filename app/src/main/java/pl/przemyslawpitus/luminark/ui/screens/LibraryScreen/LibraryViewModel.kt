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
    val uiState: StateFlow<LibraryUiState> = libraryRepository.entries
        .map(::mapToUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
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

    private fun mapToUiState(entries: List<LibraryEntry>): LibraryUiState {
        return LibraryUiState(
            entries = entries.map { entry ->
                entry.toListEntryUiModel(
                    onEntryClick = ::onEntryClicked,
                    onEntryFocus = ::onEntryFocused
                )
            },
            isLoading = false,
        )
    }

    private fun onEntryClicked(entry: LibraryEntry) {
        viewModelScope.launch {
            when (entry) {
                is Series -> _navigationEvent.send(NavigationEvent.ToSeries(entry.id))
                is StandaloneFilm -> videoPlayer.playVideo(entry.videoFiles.first().absolutePath)
                is MediaGrouping -> _navigationEvent.send(NavigationEvent.ToMediaGrouping(entry.id))
                is FilmSeries -> _navigationEvent.send(NavigationEvent.ToFilmSeries(entry.id))
            }
        }
    }

    private fun onEntryFocused(entry: LibraryEntry) {
        _posterPath.value = entry.rootRelativePosterPath
    }
}

fun LibraryEntry.toListEntryUiModel(
    onEntryClick: (LibraryEntry) -> Unit,
    onEntryFocus: (LibraryEntry) -> Unit
): ListEntryUiModel {
    return ListEntryUiModel(
        name = this.name,
        type = when (this) {
            is StandaloneFilm -> ListEntryUiModel.Type.Single
            is Series -> ListEntryUiModel.Type.Series(this.seasons.size)
            is MediaGrouping -> ListEntryUiModel.Type.Grouping(this.entries.size)
            is FilmSeries -> ListEntryUiModel.Type.PlayablesGroup(this.films.size)
        },
        onClick = { onEntryClick(this) },
        onFocus = { onEntryFocus(this) },
    )
}

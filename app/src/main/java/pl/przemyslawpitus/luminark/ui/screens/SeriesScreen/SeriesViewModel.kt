package pl.przemyslawpitus.luminark.ui.screens.SeriesScreen

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.R
import pl.przemyslawpitus.luminark.domain.library.EntryId
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.Series
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class SeriesUiState(
    val entries: List<ListEntryUiModel>? = null,
    val name: Name? = null,
    val tags: Set<String> = emptySet(),
    val posterBytes: ByteArray? = null,
    val isLoading: Boolean = true,
)

sealed class NavigationEvent {
    data class ToSeriesEpisodes(val episodesGroupId: EntryId) : NavigationEvent()
}

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val posterProvider: ImageFilePosterProvider,
    private val application: Application,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        val seriesId: String? = savedStateHandle[Destination.Series.seriesIdArg]
        if (seriesId != null) {
            loadSeries(seriesId)
        }
    }

    private fun loadSeries(seriesId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val series = libraryRepository.getTopLevelEntries()
                .filterIsInstance<Series>()
                .find { it.id.id == seriesId }!!

            val entries = series.seasons.map {
                ListEntryUiModel(
                    name = it.name,
                    type = ListEntryUiModel.Type.PlayablesGroup(it.episodes.size),
                    onClick = { onSeasonClicked(it.id) },
                    onFocus = { }
                )
            }

            val supportedFileExtensions = application.resources.getStringArray(R.array.poster_image_extensions).toSet()

            val posterBytes = posterProvider.findPosterImage(
                series.rootRelativePath, supportedFileExtensions
            )

            _uiState.value = SeriesUiState(
                entries = entries,
                posterBytes = posterBytes,
                isLoading = false,
                name = series.name,
                tags = series.tags,
            )
        }
    }

    private fun onSeasonClicked(episodesGroupId: EntryId) {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.ToSeriesEpisodes(episodesGroupId))
        }
    }
}

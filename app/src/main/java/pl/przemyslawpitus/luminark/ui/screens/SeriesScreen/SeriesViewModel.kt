package pl.przemyslawpitus.luminark.ui.screens.SeriesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.ui.SeriesView
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import javax.inject.Inject

data class SeriesUiState(
    val series: SeriesView? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesUiState())
    val uiState = _uiState.asStateFlow()

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
                .filterIsInstance<SeriesView>()
                .find { it.id.id == seriesId }!!

            _uiState.value = SeriesUiState(series = series, isLoading = false)
        }
    }
}

package pl.przemyslawpitus.luminark.ui.screens.FilmSeriesScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import javax.inject.Inject

@HiltViewModel
class FilmSeriesViewModel @Inject constructor(
    videoPlayer: VideoPlayer,
) : ViewModel(),
    VideoPlayer by videoPlayer {
}
package pl.przemyslawpitus.luminark.infrastructure.mock

import android.content.Context
import android.widget.Toast
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import timber.log.Timber
import java.nio.file.Path

class MockVideoPlayer(
    private val context: Context,
) : VideoPlayer {

    override fun playVideo(absolutePath: Path) {
        val message = "Mock play: $absolutePath"
        Timber.d(message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

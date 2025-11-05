package pl.przemyslawpitus.luminark.infrastructure.smb

import android.content.Context
import android.content.Intent
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import java.nio.file.Path

class SmbVideoPlayer(
    private val smbFileRepository: SmbFileRepository,
    private val context: Context,
): VideoPlayer {
    override fun playVideo(absolutePath: Path) {
        val basePath = smbFileRepository.getBasePath()

        val fileUriBuilder = basePath.buildUpon()
        absolutePath.forEach { segment ->
            fileUriBuilder.appendPath(segment.toString())
        }

        val fileUri = fileUriBuilder.build()

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(fileUri, "video/mkv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}
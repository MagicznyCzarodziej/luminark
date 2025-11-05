package pl.przemyslawpitus.luminark.domain

import java.nio.file.Path

interface VideoPlayer {
    fun playVideo(absolutePath: Path)
}
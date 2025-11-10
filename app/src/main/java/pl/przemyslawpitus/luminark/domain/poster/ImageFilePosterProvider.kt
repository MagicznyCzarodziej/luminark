package pl.przemyslawpitus.luminark.domain.poster

import pl.przemyslawpitus.luminark.domain.fileSystem.FileRepository
import java.nio.file.Path

class ImageFilePosterProvider(
    private val fileRepository: FileRepository,
    private val posterFileName: String,
    private val supportedFileExtensions: Set<String>,
) {
    suspend fun findPosterImage(directoryAbsolutePath: Path): ByteArray? {
        return supportedFileExtensions.firstNotNullOfOrNull { findPosterFileForExtension(directoryAbsolutePath, it) }
    }

    private suspend fun findPosterFileForExtension(directoryAbsolutePath: Path, extension: String): ByteArray? {
        val posterImagePath = directoryAbsolutePath.resolve("$posterFileName.$extension")

        return fileRepository.useReadFileStream(posterImagePath) { it.readBytes() }
    }
}
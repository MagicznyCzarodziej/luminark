package pl.przemyslawpitus.luminark.domain.poster

import pl.przemyslawpitus.luminark.domain.FileRepository
import java.nio.file.Path

const val POSTER_IMAGE_FILE_NAME_WITHOUT_EXTENSION = "poster"

class ImageFilePosterProvider(
    private val fileRepository: FileRepository,
) {
    suspend fun findPosterImage(directoryAbsolutePath: Path, supportedFileExtensions: Set<String>): ByteArray? {
        return supportedFileExtensions.firstNotNullOfOrNull { findPosterFileForExtension(directoryAbsolutePath, it) }
    }

    private suspend fun findPosterFileForExtension(directoryAbsolutePath: Path, extension: String): ByteArray? {
        val posterImagePath = directoryAbsolutePath.resolve("$POSTER_IMAGE_FILE_NAME_WITHOUT_EXTENSION.$extension")

        return fileRepository.useReadFileStream(posterImagePath) { it.readBytes() }
    }
}
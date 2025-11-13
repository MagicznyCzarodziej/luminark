package pl.przemyslawpitus.luminark.domain.poster

import pl.przemyslawpitus.luminark.domain.fileSystem.FileRepository
import pl.przemyslawpitus.luminark.domain.fileSystem.FilesLister
import java.nio.file.Path
import java.nio.file.Paths

class ImageFilePosterProvider(
    private val fileRepository: FileRepository,
    private val filesLister: FilesLister,
    private val posterFileName: String,
    private val supportedFileExtensions: Set<String>,
) {
    suspend fun findPosterImage(directoryAbsolutePath: Path): Path? {
        return supportedFileExtensions.firstNotNullOfOrNull { findPosterFileForExtension(directoryAbsolutePath, it) }
    }

    suspend fun findPosterImageWithFallback(directoryAbsolutePath: Path): Path {
        return supportedFileExtensions.firstNotNullOfOrNull { findPosterFileForExtension(directoryAbsolutePath, it) }
            ?: Paths.get("defaultPoster.jpg") // TODO
    }

    suspend fun getPosterImage(posterImagePath: Path): ByteArray {
        return fileRepository.useReadFileStream(posterImagePath) { it.readBytes() }
            ?: throw PosterImageNotFound(posterImagePath)
    }

    private suspend fun findPosterFileForExtension(directoryAbsolutePath: Path, extension: String): Path? {
        val posterImagePath = directoryAbsolutePath.resolve("$posterFileName.$extension")
        val filesInDirectory = filesLister.listFilesAndDirectories(directoryAbsolutePath)
        val fileExists = filesInDirectory.any { it.absolutePath == posterImagePath }

        return if (fileExists) {
            posterImagePath
        } else {
            null
        }
    }
}

class PosterImageNotFound(posterImagePath: Path) : RuntimeException("Poster image not found at $posterImagePath")
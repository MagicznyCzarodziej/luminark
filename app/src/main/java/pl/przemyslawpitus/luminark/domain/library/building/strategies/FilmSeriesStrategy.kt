package pl.przemyslawpitus.luminark.domain.library.building.strategies

import pl.przemyslawpitus.luminark.domain.fileSystem.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.library.FilmSeries
import pl.przemyslawpitus.luminark.domain.library.FilmSeriesFilm
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.VideoFile
import pl.przemyslawpitus.luminark.domain.library.building.FileNameParser
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.randomEntryId
import java.nio.file.Path
import java.util.regex.Pattern

val FILM_SERIES_FILM_NUMBER_PATTERN: Pattern = Pattern.compile("""^\[(\d+)\]\s""")

class FilmSeriesStrategy : MediaClassifierStrategy {
    /**
     * A directory is a FilmSeries if it defined as such in config file
     */
    override suspend fun isApplicable(context: ClassificationContext): Boolean {
        return context.lumiDirectoryConfig.type === LumiDirectoryConfig.Type.FILM_SERIES
    }

    override suspend fun classify(context: ClassificationContext): LibraryEntry {
        val posterPath =
            context.posterProvider.findPosterImageWithFallback(directoryAbsolutePath = context.directory.absolutePath)

        val films = context.subdirectories.mapNotNull { filmDir ->
            processFilm(
                context = context,
                filmDir = filmDir,
                mainPosterPath = posterPath
            )
        }
            .sortedBy { it.name.name }
            .sortedBy { it.ordinalNumber }

        return FilmSeries(
            id = randomEntryId(),
            name = FileNameParser.parseName(context.directory.name),
            rootRelativePath = context.directory.absolutePath,
            rootRelativePosterPath = posterPath,
            tags = context.lumiDirectoryConfig.tags,
            franchise = context.lumiDirectoryConfig.franchise,
            films = films,
        )
    }

    private suspend fun processFilm(
        context: ClassificationContext,
        filmDir: DirectoryEntry,
        mainPosterPath: Path,
    ): FilmSeriesFilm? {
        val videoFiles = context.fileLister.listFilesAndDirectories(filmDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }
            .map { VideoFile(name = Name(it.name), it.absolutePath) }
            .sortedBy { it.name.name }

        if (videoFiles.isEmpty()) return null

        val posterPath = context.posterProvider.findPosterImage(directoryAbsolutePath = filmDir.absolutePath)
            ?: mainPosterPath

        return FilmSeriesFilm(
            id = randomEntryId(),
            name = FileNameParser.parseName(filmDir.name),
            rootRelativePath = filmDir.absolutePath,
            rootRelativePosterPath = posterPath,
            ordinalNumber = getOrdinalNumberFromName(filmDir.name) ?: 0,
            videoFiles = videoFiles
        )
    }

    private fun getOrdinalNumberFromName(name: String): Int? {
        val matcher = FILM_SERIES_FILM_NUMBER_PATTERN.matcher(name)
        if (matcher.find()) {
            return matcher.group(1)?.toIntOrNull()
        }
        return null
    }
}

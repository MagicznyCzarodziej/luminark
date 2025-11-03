package pl.przemyslawpitus.luminark.domain.library.strategies

import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.domain.library.FileNameParser
import pl.przemyslawpitus.luminark.domain.library.Film
import pl.przemyslawpitus.luminark.domain.library.FilmSeries
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.VideoFile
import pl.przemyslawpitus.luminark.randomEntryId

class FilmSeriesStrategy : MediaClassifierStrategy {
    /**
     * A directory is a FilmSeries if it defined as such in config file
     */
    override fun isApplicable(context: ClassificationContext): Boolean {
        return context.lumiDirectoryConfig?.type === LumiDirectoryConfig.Type.FILM_SERIES
    }

    override fun classify(context: ClassificationContext): LibraryEntry {
        val films = context.subdirectories.mapNotNull { filmDir ->
            processFilm(context, filmDir)
        }

        return FilmSeries(
            id = randomEntryId(),
            name = FileNameParser.parseName(context.directory.name),
            films = films,
        )
    }

    private fun processFilm(
        context: ClassificationContext,
        filmDir: DirectoryEntry,
    ): Film? {
        val videoFiles = context.fileLister.listFilesAndDirectories(filmDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }
            .map { VideoFile(name = it.name, it.absolutePath) }
            .sortedBy { it.name }

        if (videoFiles.isEmpty()) return null

        return Film(
            id = randomEntryId(),
            name = FileNameParser.parseName(filmDir.name),
            videoFiles = videoFiles
        )
    }
}

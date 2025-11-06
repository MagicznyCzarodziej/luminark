package pl.przemyslawpitus.luminark.domain.library.building.strategies

import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.library.FilmSeries
import pl.przemyslawpitus.luminark.domain.library.FilmSeriesFilm
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.VideoFile
import pl.przemyslawpitus.luminark.domain.library.building.FileNameParser
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.randomEntryId

class FilmSeriesStrategy : MediaClassifierStrategy {
    /**
     * A directory is a FilmSeries if it defined as such in config file
     */
    override fun isApplicable(context: ClassificationContext): Boolean {
        return context.lumiDirectoryConfig.type === LumiDirectoryConfig.Type.FILM_SERIES
    }

    override fun classify(context: ClassificationContext): LibraryEntry {
        val films = context.subdirectories.mapNotNull { filmDir ->
            processFilm(context, filmDir)
        }

        return FilmSeries(
            id = randomEntryId(),
            name = FileNameParser.parseName(context.directory.name),
            rootRelativePath = context.directory.absolutePath,
            tags = context.lumiDirectoryConfig.tags,
            franchise = context.lumiDirectoryConfig.franchise,
            films = films,
        )
    }

    private fun processFilm(
        context: ClassificationContext,
        filmDir: DirectoryEntry,
    ): FilmSeriesFilm? {
        val videoFiles = context.fileLister.listFilesAndDirectories(filmDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }
            .map { VideoFile(name = Name(it.name), it.absolutePath) }
            .sortedBy { it.name.name }

        if (videoFiles.isEmpty()) return null

        return FilmSeriesFilm(
            id = randomEntryId(),
            name = FileNameParser.parseName(filmDir.name),
            rootRelativePath = filmDir.absolutePath,
            ordinalNumber = 0, // TODO
            videoFiles = videoFiles
        )
    }
}

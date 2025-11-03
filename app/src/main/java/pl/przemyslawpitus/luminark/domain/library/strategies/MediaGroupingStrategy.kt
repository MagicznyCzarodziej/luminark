package pl.przemyslawpitus.luminark.domain.library.strategies

import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.library.Film
import pl.przemyslawpitus.luminark.domain.library.FileNameParser
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.MediaGrouping
import pl.przemyslawpitus.luminark.domain.library.Series
import pl.przemyslawpitus.luminark.domain.library.VideoFile
import pl.przemyslawpitus.luminark.randomEntryId

class MediaGroupingStrategy : MediaClassifierStrategy {
    override fun isApplicable(context: ClassificationContext): Boolean {
        // A directory is a potential MediaGrouping if it contains subfolders and no videos at the top level.
        if (context.videoFiles.isNotEmpty() || context.subdirectories.isEmpty()) {
            return false
        }

        // To be a MediaGrouping, it MUST contain a mix of film-like and season-like folders.
        val childTypes = context.subdirectories.map { classifySubdirectory(context, it) }
        val containsFilms = childTypes.any { it is ChildType.Film }
        val containsSeasons = childTypes.any { it is ChildType.Season }

        return containsFilms && containsSeasons
    }

    override fun classify(context: ClassificationContext): LibraryEntry {
        val childTypes = context.subdirectories.map { classifySubdirectory(context, it) }
        val entries = childTypes.mapIndexedNotNull { index, type ->
            when (type) {
                is ChildType.Film -> processFilm(context, type.dir)
                is ChildType.Season -> processSeason(context, type.dir, index + 1)
                is ChildType.Empty -> null
            }
        }

        return MediaGrouping(
            id = randomEntryId(),
            name = FileNameParser.parseName(context.directory.name),
            entries = entries
        )
    }

    private fun processFilm(context: ClassificationContext, filmDir: DirectoryEntry): Film {
        val videoFiles = context.fileLister.listFilesAndDirectories(filmDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }
            .map { VideoFile(name = it.name, it.absolutePath) }
            .sortedBy { it.name }

        return Film(
            id = randomEntryId(),
            name = FileNameParser.parseName(filmDir.name),
            videoFiles = videoFiles
        )
    }

    private fun processSeason(
        context: ClassificationContext,
        seasonDir: DirectoryEntry,
        fallbackNumber: Int
    ): Series.Season? {
        val episodeFiles = context.fileLister.listFilesAndDirectories(seasonDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }

        if (episodeFiles.isEmpty()) return null

        val seasonNumberFromName = FileNameParser.extractSeasonNumber(seasonDir.name)
        val seasonNumberFromEpisode =
            episodeFiles.firstNotNullOfOrNull { FileNameParser.extractSeasonNumberFromEpisode(it.name) }

        val episodes =
            episodeFiles.mapNotNull { parseEpisode(it, context.videoExtensions) }.sortedBy { it.ordinalNumber }

        return Series.Season(
            id = randomEntryId(),
            ordinalNumber = seasonNumberFromName ?: seasonNumberFromEpisode ?: fallbackNumber,
            name = seasonDir.name,
            episodes = episodes
        )
    }

    private fun parseEpisode(file: DirectoryEntry, videoExtensions: Set<String>): Series.Episode {
        val details = FileNameParser.parseEpisodeDetails(file.name, videoExtensions)
        return Series.Episode(
            id = randomEntryId(),
            ordinalNumber = details.number,
            name = details.title,
            absolutePath = file.absolutePath
        )
    }

    private fun classifySubdirectory(context: ClassificationContext, subdirectory: DirectoryEntry): ChildType {
        val files = context.fileLister.listFilesAndDirectories(subdirectory.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }

        // Heuristic: Does it look like a season? (multiple videos or episode patterns)
        val isSeasonLike =
            files.size > 1 || files.any { FileNameParser.extractSeasonNumberFromEpisode(it.name) != null }

        // Heuristic: Does it look like a film? (exactly one video, no episode patterns)
        val isFilmLike = files.size == 1 && !isSeasonLike

        return when {
            isSeasonLike -> ChildType.Season(subdirectory)
            isFilmLike -> ChildType.Film(subdirectory)
            else -> ChildType.Empty
        }
    }
}

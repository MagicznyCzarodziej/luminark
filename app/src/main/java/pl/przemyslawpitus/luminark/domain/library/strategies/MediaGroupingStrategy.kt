package pl.przemyslawpitus.luminark.domain.library.strategies

import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.library.Episode
import pl.przemyslawpitus.luminark.domain.library.EpisodesGroup
import pl.przemyslawpitus.luminark.domain.library.FileNameParser
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.MediaGrouping
import pl.przemyslawpitus.luminark.domain.library.MediaGroupingFilm
import pl.przemyslawpitus.luminark.domain.library.Name
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
            rootRelativePath = context.directory.absolutePath,
            tags = context.lumiDirectoryConfig.tags,
            franchise = context.lumiDirectoryConfig.franchise,
            entries = entries
        )
    }

    private fun processFilm(context: ClassificationContext, filmDir: DirectoryEntry): MediaGroupingFilm {
        val videoFiles = context.fileLister.listFilesAndDirectories(filmDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }
            .map { VideoFile(name = Name(it.name), it.absolutePath) }
            .sortedBy { it.name.name }

        return MediaGroupingFilm(
            id = randomEntryId(),
            name = FileNameParser.parseName(filmDir.name),
            rootRelativePath = filmDir.absolutePath,
            videoFiles = videoFiles
        )
    }

    private fun processSeason(
        context: ClassificationContext,
        seasonDir: DirectoryEntry,
        fallbackNumber: Int
    ): EpisodesGroup? {
        val episodeFiles = context.fileLister.listFilesAndDirectories(seasonDir.absolutePath)
            .filter { it.isFile && FileNameParser.isVideoFile(it.name, context.videoExtensions) }

        if (episodeFiles.isEmpty()) return null

        val seasonNumberFromName = FileNameParser.extractSeasonNumber(seasonDir.name)
        val seasonNumberFromEpisode =
            episodeFiles.firstNotNullOfOrNull { FileNameParser.extractSeasonNumberFromEpisode(it.name) }

        val episodes = episodeFiles
            .map { parseEpisode(it, context.videoExtensions) }
            .sortedBy { it.ordinalNumber }

        return EpisodesGroup(
            id = randomEntryId(),
            name = Name(seasonDir.name),
            rootRelativePath = seasonDir.absolutePath,
            ordinalNumber = seasonNumberFromName ?: seasonNumberFromEpisode ?: fallbackNumber,
            episodes = episodes
        )
    }

    private fun parseEpisode(file: DirectoryEntry, videoExtensions: Set<String>): Episode {
        val details = FileNameParser.parseEpisodeDetails(file.name, videoExtensions)
        return Episode(
            id = randomEntryId(),
            name = Name(details.title),
            rootRelativePath = file.absolutePath,
            ordinalNumber = details.number,
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

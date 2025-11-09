package pl.przemyslawpitus.luminark.domain.library.building.strategies

import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.library.Episode
import pl.przemyslawpitus.luminark.domain.library.EpisodesGroup
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.Series
import pl.przemyslawpitus.luminark.domain.library.building.FileNameParser
import pl.przemyslawpitus.luminark.randomEntryId

class SeriesStrategy : MediaClassifierStrategy {
    override fun isApplicable(context: ClassificationContext): Boolean {
        // A directory is a potential Series if it contains subfolders and no videos at the top level.
        if (context.videoFiles.isNotEmpty() || context.subdirectories.isEmpty()) {
            return false
        }

        // To be a Series, it must NOT qualify as a MediaGrouping (i.e., it doesn't contain a mix of content).
        val childTypes = context.subdirectories.map { classifySubdirectory(context, it) }
        val containsFilms = childTypes.any { it is ChildType.Film }
        val containsSeasons = childTypes.any { it is ChildType.Season }

        // It's a series if it contains seasons and does NOT contain films.
        return containsSeasons && !containsFilms
    }

    override fun classify(context: ClassificationContext): LibraryEntry {
        val seasons = context.subdirectories.mapIndexedNotNull { index, seasonDir ->
            processSeason(context, seasonDir, index + 1)
        }

        return Series(
            id = randomEntryId(),
            name = FileNameParser.parseName(context.directory.name),
            rootRelativePath = context.directory.absolutePath,
            tags = context.lumiDirectoryConfig.tags,
            franchise = context.lumiDirectoryConfig.franchise,
            seasons = seasons.sortedBy { it.ordinalNumber }
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
            .map { parseEpisode(it, context) }
            .sortedBy { it.ordinalNumber }

        return EpisodesGroup(
            id = randomEntryId(),
            name = Name(seasonDir.name),
            rootRelativePath = seasonDir.absolutePath,
            ordinalNumber = seasonNumberFromName ?: seasonNumberFromEpisode ?: fallbackNumber,
            episodes = episodes
        )
    }

    private fun parseEpisode(file: DirectoryEntry, context: ClassificationContext): Episode {
        val details = FileNameParser.parseEpisodeDetails(file.name, context.videoExtensions, context.directory.name)

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
        if (files.isEmpty()) return ChildType.Empty
        val hasEpisodePattern = files.any { FileNameParser.extractSeasonNumberFromEpisode(it.name) != null }
        return when {
            files.size > 1 || hasEpisodePattern -> ChildType.Season(subdirectory)
            files.size == 1 && !hasEpisodePattern -> ChildType.Film(subdirectory)
            else -> ChildType.Empty
        }
    }
}
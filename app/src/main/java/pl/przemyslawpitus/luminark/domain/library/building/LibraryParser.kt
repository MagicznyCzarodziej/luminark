package pl.przemyslawpitus.luminark.domain.library.building

import pl.przemyslawpitus.luminark.domain.fileSystem.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.fileSystem.FilesLister
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.building.strategies.ClassificationContext
import pl.przemyslawpitus.luminark.domain.library.building.strategies.FilmSeriesStrategy
import pl.przemyslawpitus.luminark.domain.library.building.strategies.MediaClassifierStrategy
import pl.przemyslawpitus.luminark.domain.library.building.strategies.MediaGroupingStrategy
import pl.przemyslawpitus.luminark.domain.library.building.strategies.SeriesStrategy
import pl.przemyslawpitus.luminark.domain.library.building.strategies.StandaloneFilmStrategy
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider

class LibraryParser(
    private val fileLister: FilesLister,
    private val videoExtensions: Set<String>,
    private val lumiDirectoryConfigProvider: LumiDirectoryConfigProvider,
) {
    // The order of strategies is important. More specific strategies (like MediaGrouping)
    // should be evaluated before more general ones (like Series).
    private val strategies: List<MediaClassifierStrategy> = listOf(
        FilmSeriesStrategy(),
        StandaloneFilmStrategy(),
        MediaGroupingStrategy(),
        SeriesStrategy(),
    )

    suspend fun parseDirectory(directory: DirectoryEntry): LibraryEntry? {
        val children = fileLister.listFilesAndDirectories(directory.absolutePath)

        val videoFiles = children.filter { it.isFile && FileNameParser.isVideoFile(it.name, videoExtensions) }
        val subdirectories = children.filter { it.isDirectory }

        val lumiDirectoryConfig = lumiDirectoryConfigProvider
            .getLumiDirectoryConfigForDirectory(directory.absolutePath)
            ?: LumiDirectoryConfig(
                type = null,
                franchise = null,
                tags = emptySet(),
            )

        val context = ClassificationContext(
            directory = directory,
            subdirectories = subdirectories,
            videoFiles = videoFiles,
            fileLister = fileLister,
            videoExtensions = videoExtensions,
            lumiDirectoryConfig = lumiDirectoryConfig,
        )

        return strategies
            .firstOrNull { it.isApplicable(context) }
            ?.classify(context)
    }
}
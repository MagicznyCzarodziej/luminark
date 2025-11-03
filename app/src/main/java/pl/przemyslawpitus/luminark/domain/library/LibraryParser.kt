package pl.przemyslawpitus.luminark.domain.library

import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.FilesLister
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider
import pl.przemyslawpitus.luminark.domain.library.strategies.ClassificationContext
import pl.przemyslawpitus.luminark.domain.library.strategies.FilmSeriesStrategy
import pl.przemyslawpitus.luminark.domain.library.strategies.FilmStrategy
import pl.przemyslawpitus.luminark.domain.library.strategies.MediaClassifierStrategy
import pl.przemyslawpitus.luminark.domain.library.strategies.MediaGroupingStrategy
import pl.przemyslawpitus.luminark.domain.library.strategies.SeriesStrategy

class LibraryParser(
    private val fileLister: FilesLister,
    private val videoExtensions: Set<String>,
    private val lumiDirectoryConfigProvider: LumiDirectoryConfigProvider,
) {
    // The order of strategies is important. More specific strategies (like MediaGrouping)
    // should be evaluated before more general ones (like Series).
    private val strategies: List<MediaClassifierStrategy> = listOf(
        FilmSeriesStrategy(),
        FilmStrategy(),
        MediaGroupingStrategy(),
        SeriesStrategy(),
    )

    suspend fun parseDirectory(directory: DirectoryEntry): LibraryEntry? {
        val children = fileLister.listFilesAndDirectories(directory.absolutePath)

        val videoFiles = children.filter { it.isFile && FileNameParser.isVideoFile(it.name, videoExtensions) }
        val subdirectories = children.filter { it.isDirectory }

        val lumiDirectoryConfig = lumiDirectoryConfigProvider.getLumiDirectoryConfigForDirectory(directory.absolutePath)

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
package pl.przemyslawpitus.luminark.domain.library.strategies

import pl.przemyslawpitus.luminark.domain.library.FileNameParser
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.StandaloneFilm
import pl.przemyslawpitus.luminark.domain.library.VideoFile
import pl.przemyslawpitus.luminark.randomEntryId

class StandaloneFilmStrategy : MediaClassifierStrategy {
    /**
     * A directory is a Film if it contains one or more video files
     * and has no subdirectories (which would likely make it a Series).
     */
    override fun isApplicable(context: ClassificationContext): Boolean {
        return context.videoFiles.isNotEmpty() && context.subdirectories.isEmpty()
    }

    override fun classify(context: ClassificationContext): LibraryEntry {
        val videoFiles = context.videoFiles
            .map {
                VideoFile(
                    name = Name(it.name),
                    absolutePath = it.absolutePath
                )
            }
            .sortedBy { it.name.name }

        return StandaloneFilm(
            id = randomEntryId(),
            name = FileNameParser.parseName(context.directory.name),
            rootRelativePath = context.directory.absolutePath, // TODO
            tags = context.lumiDirectoryConfig.tags,
            franchise = context.lumiDirectoryConfig.franchise,
            videoFiles = videoFiles
        )
    }
}

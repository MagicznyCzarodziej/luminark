package pl.przemyslawpitus.luminark.domain.library.building.strategies

import pl.przemyslawpitus.luminark.domain.fileSystem.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.fileSystem.FilesLister
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig

interface MediaClassifierStrategy {
    suspend fun isApplicable(context: ClassificationContext): Boolean
    suspend fun classify(context: ClassificationContext): LibraryEntry?
}

data class ClassificationContext(
    val directory: DirectoryEntry,
    val subdirectories: List<DirectoryEntry>,
    val videoFiles: List<DirectoryEntry>,
    val fileLister: FilesLister,
    val videoExtensions: Set<String>,
    val lumiDirectoryConfig: LumiDirectoryConfig
)

internal sealed class ChildType {
    data class Season(val dir: DirectoryEntry) : ChildType()
    data class Film(val dir: DirectoryEntry) : ChildType()
    data object Empty : ChildType()
}
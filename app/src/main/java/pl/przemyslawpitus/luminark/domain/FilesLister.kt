package pl.przemyslawpitus.luminark.domain

import java.nio.file.Path

interface FilesLister {
    suspend fun listFilesAndDirectories(directoryAbsolutePath: Path): List<DirectoryEntry>
}

interface DirectoryEntry {
    val name: String
    val absolutePath: Path
    val isDirectory: Boolean
    val isFile: Boolean
}
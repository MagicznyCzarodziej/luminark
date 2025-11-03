package pl.przemyslawpitus.luminark.domain

interface FilesLister {
    fun listFilesAndDirectories(directoryAbsolutePath: String): List<DirectoryEntry>
}

interface DirectoryEntry {
    val name: String
    val absolutePath: String
    val isDirectory: Boolean
    val isFile: Boolean
}
package pl.przemyslawpitus.luminark.infrastructure.mock

import pl.przemyslawpitus.luminark.domain.fileSystem.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.fileSystem.FileRepository
import pl.przemyslawpitus.luminark.domain.fileSystem.FilesLister
import java.io.InputStream
import java.nio.file.Path

class MockFileRepository : FilesLister, FileRepository {

    override suspend fun listFilesAndDirectories(directoryAbsolutePath: Path): List<DirectoryEntry> {
        return emptyList()
    }

    override suspend fun <T> useReadFileStream(absolutePath: Path, block: (InputStream) -> T): T? {
        return null
    }
}

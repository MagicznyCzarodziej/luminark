package pl.przemyslawpitus.luminark.infrastructure.smb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import pl.przemyslawpitus.luminark.domain.LibraryBuilder
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.library.LibraryParser
import pl.przemyslawpitus.luminark.domain.library.NaturalOrderComparator
import java.nio.file.Path

class SmbLibraryBuilder(
    private val smbFileRepository: SmbFileRepository,
    private val libraryParser: LibraryParser,
) : LibraryBuilder {
    override suspend fun buildLibraryFrom(rootLibraryPath: Path): Library = withContext(Dispatchers.IO) {
        smbFileRepository.connectToShare() // TODO

        val rootDirs = smbFileRepository.listFilesAndDirectories(rootLibraryPath)
            .filter { it.isDirectory }
            .sortedWith(compareBy(NaturalOrderComparator) { it.name })

        val libraryEntries = rootDirs.map { dir ->
            async {
                libraryParser.parseDirectory(dir)
            }
        }.awaitAll().filterNotNull()

        Library(entries = libraryEntries)
    }
}

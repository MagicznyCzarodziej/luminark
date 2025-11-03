package pl.przemyslawpitus.luminark.infrastructure.smb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import pl.przemyslawpitus.luminark.domain.LibraryProvider
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.library.LibraryParser
import pl.przemyslawpitus.luminark.domain.library.NaturalOrderComparator

class SmbLibraryProvider(
    private val smbFileRepository: SmbFileRepository,
    private val libraryParser: LibraryParser,
) : LibraryProvider {
    override suspend fun getLibrary(): Library = withContext(Dispatchers.IO) {
        smbFileRepository.connectToShare() // TODO

        val rootDirs = smbFileRepository.listFilesAndDirectories("Filmy")
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

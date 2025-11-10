package pl.przemyslawpitus.luminark.infrastructure.smb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.library.building.LibraryBuilder
import pl.przemyslawpitus.luminark.domain.library.building.LibraryParser
import pl.przemyslawpitus.luminark.domain.utils.NaturalOrderComparator
import timber.log.Timber
import java.nio.file.Path

class SmbLibraryBuilder(
    private val smbFileRepository: SmbFileRepository,
    private val libraryParser: LibraryParser,
) : LibraryBuilder {
    override suspend fun buildLibraryFrom(rootLibraryPath: Path): Library = withContext(Dispatchers.IO) {
        Timber.d("Listing the root library directory")
        val rootDirs = smbFileRepository.listFilesAndDirectories(rootLibraryPath)
            .filter { it.isDirectory }
            .sortedWith(compareBy(NaturalOrderComparator) { it.name })

        Timber.d("Building the library")
        val libraryEntries = rootDirs.map { dir ->
            async {
                libraryParser.parseDirectory(dir)
            }
        }.awaitAll().filterNotNull()

        Timber.d("Library built")
        Library(entries = libraryEntries)
    }
}

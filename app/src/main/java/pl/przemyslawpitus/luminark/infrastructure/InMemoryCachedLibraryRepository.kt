package pl.przemyslawpitus.luminark.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.przemyslawpitus.luminark.domain.library.LibraryCache
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.building.LibraryBuilder
import java.nio.file.Path

class InMemoryCachedLibraryRepository(
    private val libraryBuilder: LibraryBuilder,
    private val libraryCache: LibraryCache,
) : LibraryRepository {
    private val _entries = MutableStateFlow(emptyList<LibraryEntry>())
    override val entries = _entries.asStateFlow()

    override fun getTopLevelEntries(): List<LibraryEntry> {
        return _entries.value
    }

    override suspend fun initialize(libraryRootPath: Path) {
        if (_entries.value.isNotEmpty()) return

        val libraryFromCache = libraryCache.load()

        if (libraryFromCache != null) {
            _entries.value = libraryFromCache.entries
            return
        }

        val library = libraryBuilder.buildLibraryFrom(libraryRootPath)
        libraryCache.save(library)

        _entries.value = library.entries
    }
}
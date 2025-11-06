package pl.przemyslawpitus.luminark.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.przemyslawpitus.luminark.domain.LibraryBuilder
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import java.nio.file.Path

class InMemoryLibraryRepository(
    private val libraryBuilder: LibraryBuilder,
) : LibraryRepository {
    private val _entries = MutableStateFlow(emptyList<LibraryEntry>())
    override val entries = _entries.asStateFlow()

    override fun getTopLevelEntries(): List<LibraryEntry> {
        return _entries.value
    }

    override suspend fun initialize(libraryRootPath: Path) {
        if (_entries.value.isNotEmpty()) return

        val entries = libraryBuilder.buildLibraryFrom(libraryRootPath).entries
        _entries.value = entries
    }
}
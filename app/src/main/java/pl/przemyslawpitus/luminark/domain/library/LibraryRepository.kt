package pl.przemyslawpitus.luminark.domain.library

import kotlinx.coroutines.flow.Flow
import java.nio.file.Path

interface LibraryRepository {
    val entries: Flow<List<LibraryEntry>>

    suspend fun initialize(libraryRootPath: Path, ignoreCache: Boolean = false)
    fun getTopLevelEntries(): List<LibraryEntry>
}
package pl.przemyslawpitus.luminark.domain.library

import kotlinx.coroutines.flow.Flow
import pl.przemyslawpitus.luminark.ui.TopLevelLibraryEntry

interface LibraryRepository {
    val entries: Flow<List<TopLevelLibraryEntry>>

    suspend fun initialize()
    fun getTopLevelEntries(): List<TopLevelLibraryEntry>
}
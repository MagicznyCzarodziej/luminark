package pl.przemyslawpitus.luminark.infrastructure.mock

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.utils.LibraryStyleComparator
import timber.log.Timber
import java.nio.file.Path

class MockLibraryRepository(
    private val context: Context,
) : LibraryRepository {
    private val _entries = MutableStateFlow(emptyList<LibraryEntry>())
    override val entries = _entries.asStateFlow()

    override fun getTopLevelEntries(): List<LibraryEntry> {
        return _entries.value
    }

    override suspend fun initialize(
        libraryRootPath: Path,
        ignoreCache: Boolean,
    ) {
        Timber.d("MockLibraryRepository: loading sample data from JSON")
        val library = withContext(Dispatchers.IO) {
            val jsonString = context.assets
                .open(MOCK_LIBRARY_ASSET)
                .bufferedReader()
                .use { it.readText() }
            Json.decodeFromString<Library>(jsonString)
        }
        _entries.value = library.entries
            .sortedWith(
                compareBy(LibraryStyleComparator) { it.name.name }
            )
    }

    companion object {
        private const val MOCK_LIBRARY_ASSET = "mock_library.json"
    }
}

package pl.przemyslawpitus.luminark.infrastructure.libraryCache

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pl.przemyslawpitus.luminark.R
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.library.LibraryCache
import timber.log.Timber
import java.io.File

class LibraryJsonCache(
    context: Context
): LibraryCache {
    private val cacheFile = File(context.filesDir, context.resources.getString(R.string.library_json_cache_filename))

    private val jsonSerializer = Json {
        prettyPrint = false
    }

    override suspend fun save(library: Library) = withContext(Dispatchers.IO) {
        try {
            val jsonString = jsonSerializer.encodeToString(library)
            cacheFile.writeText(jsonString)
            Timber.Forest.d("Library saved to cache")
        } catch (e: Exception) {
            Timber.Forest.e(e, "Error while saving library to cache")
        }
    }

    override suspend fun load(): Library? = withContext(Dispatchers.IO) {
        if (!cacheFile.exists()) {
            Timber.Forest.d("Library cache file doesn't exist")
            return@withContext null
        }

        try {
            val jsonString = cacheFile.readText()
            val library = jsonSerializer.decodeFromString<Library>(jsonString)
            Timber.Forest.d("Library loaded from cache")
            return@withContext library
        } catch (e: Exception) {
            Timber.Forest.e(e, "Error while reading library from cache. Deleting cache file")
            cacheFile.delete()
            return@withContext null
        }
    }
}
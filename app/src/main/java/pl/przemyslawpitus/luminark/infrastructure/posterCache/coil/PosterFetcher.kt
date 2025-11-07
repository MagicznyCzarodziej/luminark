package pl.przemyslawpitus.luminark.infrastructure.posterCache.coil

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.FileSystem
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import java.nio.file.Path

class PosterFetcher(
    private val posterPath: PosterPath,
    private val posterProvider: ImageFilePosterProvider,
    private val options: Options,
    private val diskCache: Lazy<DiskCache?>,
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        // Try to get the image from the disk cache
        var snapshot = readFromDiskCache()

        try {
            if (snapshot != null) {
                return SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = "image/jpeg", // TODO?
                    dataSource = DataSource.DISK,
                )
            }

            // No image in the disk cache, try to fetch from external source
            val imageBytes: ByteArray? = posterProvider.findPosterImage(posterPath.path)

            // If image was not found in the external source, stop processing
            if (imageBytes == null) {
                return null
            }

            // Image found in the external source, write it to the disk cache
            snapshot = writeToDiskCache(imageBytes)

            // Try to read from saved cache and return it
            if (snapshot != null) {
                return SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = "image/jpeg",
                    dataSource = DataSource.NETWORK,
                )
            }

            // Something went wrong when trying to read from saved cache
            // TODO return directly from the external source in this case?
            return null
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private val fileSystem: FileSystem
        get() = diskCache.value?.fileSystem ?: options.fileSystem

    private fun AutoCloseable.closeQuietly() {
        try {
            close()
        } catch (e: RuntimeException) {
            throw e
        } catch (_: Exception) {
        }
    }

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            options.diskCacheKey?.let { diskCache.value?.openSnapshot(it) }
        } else {
            null
        }
    }

    private fun DiskCache.Snapshot.toImageSource(): ImageSource {
        return ImageSource(
            file = data,
            fileSystem = fileSystem,
            diskCacheKey = options.diskCacheKey,
            closeable = this,
        )
    }

    private fun writeToDiskCache(
        imageBytes: ByteArray,
    ): DiskCache.Snapshot? {
        val editor = options.diskCacheKey?.let {
            diskCache.value?.openEditor(it)
        } ?: return null

        try {
            fileSystem.write(editor.data) {
                write(imageBytes)
            }

            return editor.commitAndOpenSnapshot()
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        }
    }

    private fun DiskCache.Editor.abortQuietly() {
        try {
            abort()
        } catch (_: Exception) {
        }
    }

    class Factory(
        private val posterProvider: ImageFilePosterProvider
    ) : Fetcher.Factory<PosterPath> {
        override fun create(data: PosterPath, options: Options, imageLoader: ImageLoader): Fetcher {
            return PosterFetcher(
                posterPath = data,
                posterProvider = posterProvider,
                options = options,
                diskCache = lazy { imageLoader.diskCache }
            )
        }
    }

    data class PosterPath(val path: Path)
}

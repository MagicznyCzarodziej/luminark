package pl.przemyslawpitus.luminark.infrastructure

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.buffer
import okio.source
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import java.nio.file.Paths

data class XD(val path: String)

class PosterFetcher(
    private val posterProvider: ImageFilePosterProvider,
    private val posterDirectory: XD,
    private val options: Options,
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val imageBytes: ByteArray? = posterProvider.findPosterImage(
            Paths.get(posterDirectory.path),
            setOf("jpg", "png")
        )

        if (imageBytes == null) return null

        return SourceFetchResult(
            source = ImageSource(
                source = imageBytes.inputStream().source().buffer(),
                fileSystem = options.fileSystem,
            ),
            mimeType = "image/jpeg",
            dataSource = DataSource.NETWORK
        )
    }

    class Factory(
        private val posterProvider: ImageFilePosterProvider
    ) : Fetcher.Factory<XD> {
        override fun create(data: XD, options: Options, imageLoader: ImageLoader): Fetcher {
            return PosterFetcher(posterProvider, data, options)
        }
    }
}

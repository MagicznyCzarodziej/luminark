package pl.przemyslawpitus.luminark

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.key.Keyer
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.Options
import coil3.request.crossfade
import coil3.util.DebugLogger

import dagger.hilt.android.HiltAndroidApp
import okio.Path.Companion.toOkioPath
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import pl.przemyslawpitus.luminark.infrastructure.PosterDirectory
import pl.przemyslawpitus.luminark.infrastructure.PosterFetcher
import pl.przemyslawpitus.luminark.infrastructure.XD
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbFileRepository
import java.nio.file.Path
import javax.inject.Inject
import kotlin.math.log

@HiltAndroidApp
class MainApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var smbFileRepository: SmbFileRepository

    @Inject
    lateinit var imageFilePosterProvider: ImageFilePosterProvider


    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(PosterFetcher.Factory(imageFilePosterProvider))
                add(PosterDirectoryKeyer())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(this, 0.25)
                    .build()
            }
            .diskCachePolicy(CachePolicy.DISABLED) // Doesn't work anyway
            .build()
    }
}

class PosterDirectoryKeyer : Keyer<XD> {
    override fun key(data: XD, options: Options): String {
        return data.path
    }
}
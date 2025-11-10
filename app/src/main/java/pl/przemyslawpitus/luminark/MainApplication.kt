package pl.przemyslawpitus.luminark

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterDirectoryKeyer
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterDiskKeyInterceptor
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterFetcher
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbFileRepository
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var imageFilePosterProvider: ImageFilePosterProvider

    @Inject
    lateinit var smbFileRepository: SmbFileRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationScope.launch {
            smbFileRepository.connectToShare()
        }
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(PosterDiskKeyInterceptor())
                add(PosterDirectoryKeyer())
                add(PosterFetcher.Factory(imageFilePosterProvider))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(this, 0.25)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}

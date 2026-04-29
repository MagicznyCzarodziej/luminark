package pl.przemyslawpitus.luminark.config

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.przemyslawpitus.luminark.BuildConfig
import pl.przemyslawpitus.luminark.R
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.fileSystem.FileRepository
import pl.przemyslawpitus.luminark.domain.fileSystem.FilesLister
import pl.przemyslawpitus.luminark.domain.library.LibraryCache
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.building.LibraryBuilder
import pl.przemyslawpitus.luminark.domain.library.building.LibraryParser
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import pl.przemyslawpitus.luminark.infrastructure.InMemoryCachedLibraryRepository
import pl.przemyslawpitus.luminark.infrastructure.libraryCache.LibraryJsonCache
import pl.przemyslawpitus.luminark.infrastructure.mock.MockFileRepository
import pl.przemyslawpitus.luminark.infrastructure.mock.MockLibraryRepository
import pl.przemyslawpitus.luminark.infrastructure.mock.MockLumiDirectoryConfigProvider
import pl.przemyslawpitus.luminark.infrastructure.mock.MockVideoPlayer
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbFileRepository
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbLibraryBuilder
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbLumiDirectoryConfigFileReader
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbVideoPlayer
import javax.inject.Singleton
import java.nio.file.Path

@Module
@InstallIn(SingletonComponent::class)
object HiltConfig {

    @Provides
    @Singleton
    fun fileRepository(): FileRepository {
        if (BuildConfig.USE_MOCK) return MockFileRepository()
        val smb = SmbFileRepository()
        return smb
    }

    @Provides
    @Singleton
    fun filesLister(fileRepository: FileRepository): FilesLister {
        if (BuildConfig.USE_MOCK) return fileRepository as MockFileRepository
        return fileRepository as SmbFileRepository
    }

    @Provides
    @Singleton
    fun lumiDirectoryConfigProvider(
        fileRepository: FileRepository,
    ): LumiDirectoryConfigProvider {
        if (BuildConfig.USE_MOCK) return MockLumiDirectoryConfigProvider()
        return SmbLumiDirectoryConfigFileReader(
            smbFileRepository = fileRepository as SmbFileRepository,
        )
    }

    @Provides
    @Singleton
    fun libraryParser(
        fileLister: FilesLister,
        posterProvider: ImageFilePosterProvider,
        lumiDirectoryConfigProvider: LumiDirectoryConfigProvider,
        @ApplicationContext context: Context,
    ): LibraryParser {
        val videoExtensions = context.resources.getStringArray(R.array.video_file_extensions).toSet()

        return LibraryParser(
            fileLister = fileLister,
            posterProvider = posterProvider,
            videoExtensions = videoExtensions,
            lumiDirectoryConfigProvider = lumiDirectoryConfigProvider,
        )
    }

    @Provides
    @Singleton
    fun libraryBuilder(
        libraryParser: LibraryParser,
        fileRepository: FileRepository,
    ): LibraryBuilder {
        if (BuildConfig.USE_MOCK) {
            // Not used when BuildConfig.USE_MOCK is true (MockLibraryRepository bypasses builder),
            // but Hilt still needs to resolve it. Return a no-op builder.
            return object : LibraryBuilder {
                override suspend fun buildLibraryFrom(rootLibraryPath: Path): Library {
                    return Library(emptyList())
                }
            }
        }
        return SmbLibraryBuilder(
            smbFileRepository = fileRepository as SmbFileRepository,
            libraryParser = libraryParser,
        )
    }

    @Provides
    @Singleton
    fun videoPlayer(
        @ApplicationContext context: Context,
        fileRepository: FileRepository,
    ): VideoPlayer {
        if (BuildConfig.USE_MOCK) return MockVideoPlayer(context)
        return SmbVideoPlayer(
            smbFileRepository = fileRepository as SmbFileRepository,
            context = context,
        )
    }

    @Provides
    @Singleton
    fun libraryRepository(
        libraryBuilder: LibraryBuilder,
        libraryCache: LibraryCache,
        @ApplicationContext context: Context,
    ): LibraryRepository {
        if (BuildConfig.USE_MOCK) return MockLibraryRepository(context)
        return InMemoryCachedLibraryRepository(
            libraryBuilder = libraryBuilder,
            libraryCache = libraryCache,
        )
    }

    @Provides
    @Singleton
    fun imageFilePosterProvider(
        fileRepository: FileRepository,
        filesLister: FilesLister,
        @ApplicationContext context: Context,
    ): ImageFilePosterProvider {
        return ImageFilePosterProvider(
            fileRepository = fileRepository,
            filesLister = filesLister,
            posterFileName = context.getString(R.string.poster_file_name_without_extension),
            supportedFileExtensions = context.resources.getStringArray(R.array.poster_image_extensions).toSet()
        )
    }

    @Provides
    @Singleton
    fun libraryCache(
        @ApplicationContext context: Context,
    ): LibraryCache {
        return LibraryJsonCache(
            context = context,
        )
    }
}

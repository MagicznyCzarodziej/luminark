package pl.przemyslawpitus.luminark.config

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.przemyslawpitus.luminark.R
import pl.przemyslawpitus.luminark.domain.FileRepository
import pl.przemyslawpitus.luminark.domain.FilesLister
import pl.przemyslawpitus.luminark.domain.LibraryBuilder
import pl.przemyslawpitus.luminark.domain.VideoPlayer
import pl.przemyslawpitus.luminark.domain.library.LibraryParser
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider
import pl.przemyslawpitus.luminark.domain.poster.ImageFilePosterProvider
import pl.przemyslawpitus.luminark.infrastructure.InMemoryLibraryRepository
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbFileRepository
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbLibraryBuilder
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbLumiDirectoryConfigFileReader
import pl.przemyslawpitus.luminark.infrastructure.smb.SmbVideoPlayer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltConfig {

    @Provides
    @Singleton
    suspend fun smbFileRepository(): SmbFileRepository {
        val smbFileRepository = SmbFileRepository()
        smbFileRepository.connectToShare()
        return smbFileRepository
    }

    @Provides
    @Singleton
    fun lumiDirectoryConfigProvider(
        smbFileRepository: SmbFileRepository,
    ): LumiDirectoryConfigProvider {
        return SmbLumiDirectoryConfigFileReader(
            smbFileRepository = smbFileRepository
        )
    }

    @Provides
    @Singleton
    fun filesLister(
        smbFileRepository: SmbFileRepository,
    ): FilesLister {
        return smbFileRepository
    }

    @Provides
    @Singleton
    fun libraryParser(
        fileLister: FilesLister,
        lumiDirectoryConfigProvider: LumiDirectoryConfigProvider,
        @ApplicationContext context: Context,
    ): LibraryParser {
        val videoExtensions = context.resources.getStringArray(R.array.video_file_extensions).toSet()

        return LibraryParser(
            fileLister = fileLister,
            videoExtensions = videoExtensions,
            lumiDirectoryConfigProvider = lumiDirectoryConfigProvider,
        )
    }

    @Provides
    @Singleton
    fun libraryBuilder(
        libraryParser: LibraryParser,
        smbFileRepository: SmbFileRepository,
    ): LibraryBuilder {
        return SmbLibraryBuilder(
            smbFileRepository = smbFileRepository,
            libraryParser = libraryParser
        )
    }

    @Provides
    @Singleton
    fun videoPlayer(
        smbFileRepository: SmbFileRepository,
        @ApplicationContext context: Context,
    ): SmbVideoPlayer {
        return SmbVideoPlayer(
            smbFileRepository = smbFileRepository,
            context = context,
        )
    }

    @Provides
    @Singleton
    fun libraryRepository(
        libraryBuilder: LibraryBuilder
    ): LibraryRepository {
        return InMemoryLibraryRepository(
            libraryBuilder = libraryBuilder,
        )
    }

    @Provides
    @Singleton
    fun fileRepository(
        smbFileRepository: SmbFileRepository,
        ): FileRepository {
        return smbFileRepository
    }

    @Provides
    @Singleton
    fun imageFilePosterProvider(
        fileRepository: FileRepository,
    ): ImageFilePosterProvider {
        return ImageFilePosterProvider(
            fileRepository = fileRepository,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {
    @Binds
    @Singleton
    abstract fun bindVideoPlayer(
        smbVideoPlayer: SmbVideoPlayer
    ): VideoPlayer
}
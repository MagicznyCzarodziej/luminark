package pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig

interface LumiDirectoryConfigProvider {
    suspend fun getLumiDirectoryConfigForDirectory(directoryAbsolutePath: String): LumiDirectoryConfig?
}
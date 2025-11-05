package pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig

import java.nio.file.Path

interface LumiDirectoryConfigProvider {
    suspend fun getLumiDirectoryConfigForDirectory(directoryAbsolutePath: Path): LumiDirectoryConfig?
}
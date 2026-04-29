package pl.przemyslawpitus.luminark.infrastructure.mock

import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider
import java.nio.file.Path

class MockLumiDirectoryConfigProvider : LumiDirectoryConfigProvider {

    override suspend fun getLumiDirectoryConfigForDirectory(directoryAbsolutePath: Path): LumiDirectoryConfig? {
        return null
    }
}

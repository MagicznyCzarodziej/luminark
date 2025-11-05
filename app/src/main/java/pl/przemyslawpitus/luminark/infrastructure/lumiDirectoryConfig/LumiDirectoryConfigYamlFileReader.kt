package pl.przemyslawpitus.luminark.infrastructure.lumiDirectoryConfig

import pl.przemyslawpitus.luminark.domain.library.Franchise
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.infrastructure.YamlFileReader
import java.io.InputStream

object LumiDirectoryConfigYamlFileReader {
    const val LUMI_CONFIG_FILE_NAME = ".lumi"

    fun read(inputStream: InputStream): LumiDirectoryConfig {
        val rawConfig = YamlFileReader.readYaml(inputStream, LumiDirectoryConfigRaw::class.java)
        return LumiDirectoryConfig(
            type = rawConfig.type?.let { LumiDirectoryConfig.Type.valueOf(it) },
            franchise = rawConfig.franchise?.let { Franchise(it) },
            tags = rawConfig.tags?.toSet().orEmpty(),
        )
    }
}
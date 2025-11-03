package pl.przemyslawpitus.luminark.infrastructure

import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import java.io.InputStream

object LumiDirectoryConfigYamlFileReader {
    const val LUMI_CONFIG_FILE_NAME = ".lumi"

    fun read(inputStream: InputStream): LumiDirectoryConfig {
        return YamlFileReader.readYaml(inputStream, LumiDirectoryConfig::class.java)
    }
}
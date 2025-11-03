package pl.przemyslawpitus.luminark.infrastructure.smb

import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider
import pl.przemyslawpitus.luminark.infrastructure.LumiDirectoryConfigYamlFileReader
import pl.przemyslawpitus.luminark.infrastructure.LumiDirectoryConfigYamlFileReader.LUMI_CONFIG_FILE_NAME

class SmbLumiDirectoryConfigFileReader(
    private val smbFileRepository: SmbFileRepository,
) : LumiDirectoryConfigProvider {
    override suspend fun getLumiDirectoryConfigForDirectory(directoryAbsolutePath: String): LumiDirectoryConfig? {
        val filePath = "${directoryAbsolutePath}/${LUMI_CONFIG_FILE_NAME}"

        return smbFileRepository.useReadFileStream(filePath) { inputStream ->
            LumiDirectoryConfigYamlFileReader.read(inputStream)
        }
    }
}
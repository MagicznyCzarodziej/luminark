package pl.przemyslawpitus.luminark.infrastructure.smb

import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfig
import pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig.LumiDirectoryConfigProvider
import pl.przemyslawpitus.luminark.infrastructure.lumiDirectoryConfig.LumiDirectoryConfigYamlFileReader
import pl.przemyslawpitus.luminark.infrastructure.lumiDirectoryConfig.LumiDirectoryConfigYamlFileReader.LUMI_CONFIG_FILE_NAME
import java.nio.file.Path

class SmbLumiDirectoryConfigFileReader(
    private val smbFileRepository: SmbFileRepository,
) : LumiDirectoryConfigProvider {
    override suspend fun getLumiDirectoryConfigForDirectory(directoryAbsolutePath: Path): LumiDirectoryConfig? {
//        val filePath = "${directoryAbsolutePath}/${LUMI_CONFIG_FILE_NAME}"
        val filePath = directoryAbsolutePath.resolve(LUMI_CONFIG_FILE_NAME)

        return smbFileRepository.useReadFileStream(filePath) { inputStream ->
            LumiDirectoryConfigYamlFileReader.read(inputStream)
        }
    }
}
package pl.przemyslawpitus.luminark.domain

import java.io.InputStream
import java.nio.file.Path

interface FileRepository {
    suspend fun <T> useReadFileStream(absolutePath: Path, block: (InputStream) -> T): T?
}
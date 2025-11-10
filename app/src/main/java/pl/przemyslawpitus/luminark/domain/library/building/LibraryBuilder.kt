package pl.przemyslawpitus.luminark.domain.library.building

import pl.przemyslawpitus.luminark.domain.library.Library
import java.nio.file.Path

interface LibraryBuilder {
    suspend fun buildLibraryFrom(rootLibraryPath: Path): Library
}
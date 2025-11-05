package pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig

import pl.przemyslawpitus.luminark.domain.library.Franchise

data class LumiDirectoryConfig(
    val type: Type?,
    val franchise: Franchise?,
    val tags: Set<String>,
) {
    enum class Type {
        FILM_SERIES
    }
}

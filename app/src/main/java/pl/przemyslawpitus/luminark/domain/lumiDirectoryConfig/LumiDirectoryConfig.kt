package pl.przemyslawpitus.luminark.domain.lumiDirectoryConfig

data class LumiDirectoryConfig(
    val type: Type? = null,
    val franchise: String? = null,
    val tags: List<String>? = null,
) {
    enum class Type {
        FILM_SERIES
    }
}

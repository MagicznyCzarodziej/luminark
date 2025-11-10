package pl.przemyslawpitus.luminark.domain.library

interface LibraryCache {
    suspend fun save(library: Library)
    suspend fun load(): Library?
}
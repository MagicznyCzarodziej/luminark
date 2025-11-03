package pl.przemyslawpitus.luminark.domain

import pl.przemyslawpitus.luminark.domain.library.Library

interface LibraryProvider {
    suspend fun getLibrary(): Library
}
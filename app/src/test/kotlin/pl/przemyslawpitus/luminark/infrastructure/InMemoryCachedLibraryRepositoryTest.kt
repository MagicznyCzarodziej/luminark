package pl.przemyslawpitus.luminark.infrastructure

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import pl.przemyslawpitus.luminark.domain.library.EntryId
import pl.przemyslawpitus.luminark.domain.library.Library
import pl.przemyslawpitus.luminark.domain.library.LibraryCache
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.StandaloneFilm
import pl.przemyslawpitus.luminark.domain.library.building.LibraryBuilder
import java.nio.file.Paths

class InMemoryCachedLibraryRepositoryTest {
    private val libraryBuilder = mockk<LibraryBuilder>()
    private val libraryCache = mockk<LibraryCache>(relaxUnitFun = true)

    private val repository = InMemoryCachedLibraryRepository(
        libraryBuilder = libraryBuilder,
        libraryCache = libraryCache,
    )

    private val rootPath = Paths.get("/library")

    @Nested
    inner class Sorting {
        @Test
        fun `should sort entries by name ignoring articles`() {
            runBlocking {
                // given
                val unsortedEntries = listOf(
                    film("Inception"),
                    film("Avatar"),
                    film("A Beautiful Mind"),
                    film("Zodiac"),
                    film("The Abyss"),
                    film("An Officer and a Gentleman"),
                )
                coEvery { libraryCache.load() } returns null
                coEvery { libraryBuilder.buildLibraryFrom(rootPath) } returns Library(unsortedEntries)

                // when
                repository.initialize(rootPath)

                // then
                repository.getTopLevelEntries().map { it.name.name } shouldBe listOf(
                    "The Abyss",
                    "Avatar",
                    "A Beautiful Mind",
                    "Inception",
                    "An Officer and a Gentleman",
                    "Zodiac",
                )
            }
        }

        @Test
        fun `should save sorted entries to cache`() {
            runBlocking {
                // given
                val unsortedEntries = listOf(
                    film("Zodiac"),
                    film("Avatar"),
                    film("The Matrix"),
                )
                coEvery { libraryCache.load() } returns null
                coEvery { libraryBuilder.buildLibraryFrom(rootPath) } returns Library(unsortedEntries)

                // when
                repository.initialize(rootPath)

                // then
                val savedLibrary = slot<Library>()
                coVerify { libraryCache.save(capture(savedLibrary)) }
                savedLibrary.captured.entries.map { it.name.name } shouldBe listOf(
                    "Avatar",
                    "The Matrix",
                    "Zodiac",
                )
            }
        }

        @Test
        fun `should not re-sort when loading from cache`() {
            runBlocking {
                // given
                // intentionally mis-sorted entries in cache to make sur sorting is not run after loading from cache
                val cachedEntries = listOf(
                    film("Zodiac"),
                    film("Avatar"),
                    film("The Matrix"),
                )
                coEvery { libraryCache.load() } returns Library(cachedEntries)

                // when
                repository.initialize(rootPath)

                // then
                repository.getTopLevelEntries().map { it.name.name } shouldBe listOf(
                    "Zodiac",
                    "Avatar",
                    "The Matrix",
                )
            }
        }
    }

    @Nested
    inner class Caching {
        @Test
        fun `should use cache when available`() {
            runBlocking {
                // given
                val cachedEntries = listOf(film("Cached Film"))
                coEvery { libraryCache.load() } returns Library(cachedEntries)

                // when
                repository.initialize(rootPath)

                // then
                repository.getTopLevelEntries().map { it.name.name } shouldBe listOf("Cached Film")
                coVerify(exactly = 0) { libraryBuilder.buildLibraryFrom(any()) }
            }
        }

        @Test
        fun `should build from builder when cache is empty`() {
            runBlocking {
                // given
                coEvery { libraryCache.load() } returns null
                coEvery { libraryBuilder.buildLibraryFrom(rootPath) } returns Library(listOf(film("Built Film")))

                // when
                repository.initialize(rootPath)

                // then
                repository.getTopLevelEntries().map { it.name.name } shouldBe listOf("Built Film")
                coVerify { libraryBuilder.buildLibraryFrom(rootPath) }
                coVerify { libraryCache.save(any()) }
            }
        }

        @Test
        fun `should skip cache when ignoreCache is true`() {
            runBlocking {
                // given
                coEvery { libraryCache.load() } returns Library(listOf(film("Cached Film")))
                coEvery { libraryBuilder.buildLibraryFrom(rootPath) } returns Library(listOf(film("Fresh Film")))

                // when
                repository.initialize(rootPath, ignoreCache = true)

                // then
                repository.getTopLevelEntries().map { it.name.name } shouldBe listOf("Fresh Film")
                coVerify(exactly = 0) { libraryCache.load() }
                coVerify { libraryBuilder.buildLibraryFrom(rootPath) }
            }
        }
    }

    companion object {
        private fun film(name: String) = StandaloneFilm(
            id = EntryId(name.lowercase().replace(" ", "-")),
            name = Name(name),
            rootRelativePath = Paths.get(name),
            rootRelativePosterPath = Paths.get(name),
            tags = emptySet(),
            franchise = null,
            videoFiles = emptyList(),
        )
    }
}

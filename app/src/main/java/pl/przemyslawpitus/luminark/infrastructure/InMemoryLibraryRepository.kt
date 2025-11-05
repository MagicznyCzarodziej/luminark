package pl.przemyslawpitus.luminark.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.przemyslawpitus.luminark.domain.LibraryBuilder
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import java.nio.file.Path

class InMemoryLibraryRepository(
    private val libraryBuilder: LibraryBuilder,
) : LibraryRepository {
    private val _entries = MutableStateFlow(emptyList<LibraryEntry>())
    override val entries = _entries.asStateFlow()

    override fun getTopLevelEntries(): List<LibraryEntry> {
        return _entries.value
    }

    override suspend fun initialize(libraryRootPath: Path) {
        if (_entries.value.isNotEmpty()) return

        val entries = libraryBuilder.buildLibraryFrom(libraryRootPath).entries
        _entries.value = entries
    }

//    private fun map(entries: List<LibraryEntry>): List<TopLevelLibraryEntry> {
//        return entries.map { entry ->
//            when (entry) {
//                is Film -> FilmView(
//                    id = entry.id,
//                    name = entry.name,
//                    videoFiles = entry.videoFiles.map {
//                        object : PlayableMedia {
//                            override val absolutePath: String
//                                get() = it.absolutePath
//                        }
//                    }
//                )
//
//                is Series -> SeriesView(
//                    id = entry.id,
//                    name = entry.name,
//                    seasons = entry.seasons.map {
//                        SeasonView(
//                            id = it.id,
//                            name = it.name ?: "Season ${it.ordinalNumber}",
//                            ordinalNumber = it.ordinalNumber,
//                            episodes = it.episodes.map {
//                                EpisodeView(
//                                    name = it.name,
//                                    absolutePath = it.absolutePath
//                                )
//                            }
//                        )
//                    }
//                )
//
//                is MediaGrouping -> MediaGroupingView(
//                    id = entry.id,
//                    name = entry.name,
//                    entries = entry.entries.map {
//                        when (it) {
//                            is Series.Season -> SeasonView(
//                                id = it.id,
//                                name = it.name ?: "Season ${it.ordinalNumber}",
//                                ordinalNumber = it.ordinalNumber,
//                                episodes = it.episodes.map {
//                                    EpisodeView(
//                                        name = it.name,
//                                        absolutePath = it.absolutePath
//                                    )
//                                }
//                            )
//
//                            is Film -> FilmView(
//                                id = it.id,
//                                name = it.name,
//                                videoFiles = it.videoFiles.map {
//                                    object : PlayableMedia {
//                                        override val absolutePath: String
//                                            get() = it.absolutePath
//                                    }
//                                }
//                            )
//
//                            else -> {
//                                throw RuntimeException()
//                            }
//                        }
//                    }
//                )
//
//                is FilmSeries -> FilmSeriesView(
//                    id = entry.id,
//                    name = entry.name,
//                    films = entry.films.map {
//                        FilmView(
//                            id = it.id,
//                            name = it.name,
//                            videoFiles = it.videoFiles.map {
//                                object : PlayableMedia {
//                                    override val absolutePath: String
//                                        get() = it.absolutePath
//                                }
//                            }
//                        )
//                    }
//                )
//
//                else -> {
//                    return@map null
//                }
//            }
//
//        }.filterNotNull()
//    }
}
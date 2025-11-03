package pl.przemyslawpitus.luminark.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.przemyslawpitus.luminark.domain.LibraryProvider
import pl.przemyslawpitus.luminark.domain.library.Film
import pl.przemyslawpitus.luminark.domain.library.FilmSeries
import pl.przemyslawpitus.luminark.domain.library.LibraryEntry
import pl.przemyslawpitus.luminark.domain.library.LibraryRepository
import pl.przemyslawpitus.luminark.domain.library.MediaGrouping
import pl.przemyslawpitus.luminark.domain.library.Series
import pl.przemyslawpitus.luminark.ui.EpisodeView
import pl.przemyslawpitus.luminark.ui.FilmSeriesView
import pl.przemyslawpitus.luminark.ui.FilmView
import pl.przemyslawpitus.luminark.ui.MediaGroupingView
import pl.przemyslawpitus.luminark.ui.PlayableMedia
import pl.przemyslawpitus.luminark.ui.SeasonView
import pl.przemyslawpitus.luminark.ui.SeriesView
import pl.przemyslawpitus.luminark.ui.TopLevelLibraryEntry

class InMemoryLibraryRepository(
    private val libraryProvider: LibraryProvider,
) : LibraryRepository {
    private val _entries = MutableStateFlow(emptyList<TopLevelLibraryEntry>())
    override val entries = _entries.asStateFlow()

    override fun getTopLevelEntries(): List<TopLevelLibraryEntry> {
        return _entries.value
    }

    override suspend fun initialize() {
        if (_entries.value.isNotEmpty()) return

        val entries = libraryProvider.getLibrary().entries
        _entries.value = map(entries)
    }

    private fun map(entries: List<LibraryEntry>): List<TopLevelLibraryEntry> {
        return entries.map { entry ->
            when (entry) {
                is Film -> FilmView(
                    id = entry.id,
                    name = entry.name,
                    videoFiles = entry.videoFiles.map {
                        object : PlayableMedia {
                            override val absolutePath: String
                                get() = it.absolutePath
                        }
                    }
                )

                is Series -> SeriesView(
                    id = entry.id,
                    name = entry.name,
                    seasons = entry.seasons.map {
                        SeasonView(
                            id = it.id,
                            name = it.name ?: "Season ${it.ordinalNumber}",
                            ordinalNumber = it.ordinalNumber,
                            episodes = it.episodes.map {
                                EpisodeView(
                                    name = it.name,
                                    absolutePath = it.absolutePath
                                )
                            }
                        )
                    }
                )

                is MediaGrouping -> MediaGroupingView(
                    id = entry.id,
                    name = entry.name,
                    entries = entry.entries.map {
                        when (it) {
                            is Series.Season -> SeasonView(
                                id = it.id,
                                name = it.name ?: "Season ${it.ordinalNumber}",
                                ordinalNumber = it.ordinalNumber,
                                episodes = it.episodes.map {
                                    EpisodeView(
                                        name = it.name,
                                        absolutePath = it.absolutePath
                                    )
                                }
                            )

                            is Film -> FilmView(
                                id = it.id,
                                name = it.name,
                                videoFiles = it.videoFiles.map {
                                    object : PlayableMedia {
                                        override val absolutePath: String
                                            get() = it.absolutePath
                                    }
                                }
                            )

                            else -> {
                                throw RuntimeException()
                            }
                        }
                    }
                )

                is FilmSeries -> FilmSeriesView(
                    id = entry.id,
                    name = entry.name,
                    films = entry.films.map {
                        FilmView(
                            id = it.id,
                            name = it.name,
                            videoFiles = it.videoFiles.map {
                                object : PlayableMedia {
                                    override val absolutePath: String
                                        get() = it.absolutePath
                                }
                            }
                        )
                    }
                )

                else -> {
                    return@map null
                }
            }

        }.filterNotNull()
    }
}
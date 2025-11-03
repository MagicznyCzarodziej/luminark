package pl.przemyslawpitus.luminark.ui

import pl.przemyslawpitus.luminark.domain.library.EntryId
import pl.przemyslawpitus.luminark.domain.library.Name

data class LibraryView(
    val entries: TopLevelLibraryEntry
)

interface TopLevelLibraryEntry {
    val name: Name
}

interface PlayableMedia {
    val absolutePath: String
}

data class FilmView(
    val id: EntryId,
    override val name: Name,
    val videoFiles: List<PlayableMedia>,
) : TopLevelLibraryEntry, MediaGroupingEntry

data class FilmSeriesView(
    val id: EntryId,
    override val name: Name,
    val films: List<FilmView>,
) : TopLevelLibraryEntry

data class SeriesView(
    val id: EntryId,
    override val name: Name,
    val seasons: List<SeasonView>,
) : TopLevelLibraryEntry

data class MediaGroupingView(
    val id: EntryId,
    override val name: Name,
    val entries: List<MediaGroupingEntry>,
) : TopLevelLibraryEntry

data class SeasonView(
    val id: EntryId,
    val name: String,
    val ordinalNumber: Int,
    val episodes: List<EpisodeView>
) : MediaGroupingEntry

data class EpisodeView(
    val name: String,
    override val absolutePath: String
) : PlayableMedia

interface MediaGroupingEntry


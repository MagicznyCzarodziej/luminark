package pl.przemyslawpitus.luminark.domain.library

data class EntryId(
    val id: String,
)

data class Library(
    val entries: List<LibraryEntry>
)

interface LibraryEntry {
    val name: Name
}

data class Franchise(
    val name: Name,
)

interface FranchiseEntry {
    val franchise: Franchise?
}

data class Name(
    val name: String,
    val alternativeName: String? = null,
)

data class VideoFile(
    val name: String,
    override val absolutePath: String
) : PlayableMedia

open class Film(
    val id: EntryId,
    override val name: Name,
    val videoFiles: List<VideoFile>,
    override val franchise: Franchise? = null,
) : LibraryEntry, FranchiseEntry, MediaGroupingEntry

data class FilmSeries(
    val id: EntryId,
    override val name: Name,
    override val franchise: Franchise? = null,
    val films: List<Film>,
) : FranchiseEntry, LibraryEntry

data class Series(
    val id: EntryId,
    override val name: Name,
    override val franchise: Franchise? = null,
    val seasons: List<Season>
) : LibraryEntry, FranchiseEntry {
    data class Season(
        val id: EntryId,
        val ordinalNumber: Int,
        val name: String? = null,
        val episodes: List<Episode>,
    ) : MediaGroupingEntry

    data class Episode(
        val id: EntryId,
        val ordinalNumber: Int,
        val name: String,
        override val absolutePath: String,
    ) : PlayableMedia
}

// For series containing films, related closer than just a franchise
data class MediaGrouping(
    val id: EntryId,
    override val name: Name,
    val entries: List<MediaGroupingEntry>
) : LibraryEntry

interface MediaGroupingEntry

interface PlayableMedia {
    val absolutePath: String
}
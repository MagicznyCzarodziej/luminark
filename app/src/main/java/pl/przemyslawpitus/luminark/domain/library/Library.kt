package pl.przemyslawpitus.luminark.domain.library

import java.nio.file.Path

data class EntryId(
    val id: String,
)

data class Name(
    val name: String,
    val alternativeName: String? = null,
)

data class Library(
    val entries: List<LibraryEntry>
)

interface LibraryEntry {
    val id: EntryId
    val name: Name
    val rootRelativePath: Path
}

interface Directory {
    val id: EntryId
    val name: Name
    /**
     *  // e.g. if absolute path of the entry is "smb://DOMAIN/Filmy/Avengers/The Avengers"
     *  and the library root path is "/Filmy" then this path would be "Avengers/The Avengers"
     */
    val rootRelativePath: Path
}

data class StandaloneFilm(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val videoFiles: List<VideoFile>,
): LibraryEntry, Directory, Taggable, Franchisable

data class FilmSeries(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val films: List<FilmSeriesFilm>,
): LibraryEntry, Directory, Taggable, Franchisable

data class FilmSeriesFilm(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    val ordinalNumber: Int,
    val videoFiles: List<VideoFile>,
): Directory

data class Series(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val seasons: List<EpisodesGroup>,
): LibraryEntry, Directory, Taggable, Franchisable

data class EpisodesGroup(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    val ordinalNumber: Int,
    val episodes: List<Episode>,
): Directory, MediaGroupingEntry

data class Episode(
    val id: EntryId,
    val name: Name,
    val rootRelativePath: Path,
    val ordinalNumber: Int,
): Playable

data class MediaGrouping(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val entries: List<MediaGroupingEntry>,
): LibraryEntry, Directory, Taggable, Franchisable

interface MediaGroupingEntry {
    val id: EntryId
    val name: Name
    val rootRelativePath: Path
}

data class MediaGroupingFilm(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    val videoFiles: List<VideoFile>,
): Directory, MediaGroupingEntry


data class VideoFile(
    val name: Name,
    val absolutePath: Path,
): Playable

interface Taggable {
    val tags: Set<String>
}

interface Franchisable {
    val franchise: Franchise?
}

data class Franchise (
    val name: String,
)

interface Playable
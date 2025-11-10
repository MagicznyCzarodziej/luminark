@file: UseSerializers(PathSerializer::class)

package pl.przemyslawpitus.luminark.domain.library

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pl.przemyslawpitus.luminark.infrastructure.serialization.PathSerializer
import java.nio.file.Path

@Serializable
data class EntryId(
    val id: String,
)

@Serializable
data class Name(
    val name: String,
    val alternativeName: String? = null,
)
@Serializable
data class Library(
    val entries: List<LibraryEntry>
)

@Serializable
sealed interface LibraryEntry {
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

@Serializable
data class StandaloneFilm(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val videoFiles: List<VideoFile>,
): LibraryEntry, Directory, Taggable, Franchisable

@Serializable
data class FilmSeries(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val films: List<FilmSeriesFilm>,
): LibraryEntry, Directory, Taggable, Franchisable

@Serializable
data class FilmSeriesFilm(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    val ordinalNumber: Int,
    val videoFiles: List<VideoFile>,
): Directory

@Serializable
data class Series(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val seasons: List<EpisodesGroup>,
): LibraryEntry, Directory, Taggable, Franchisable

@Serializable
data class EpisodesGroup(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    val ordinalNumber: Int,
    val episodes: List<Episode>,
): Directory, MediaGroupingEntry

@Serializable
data class Episode(
    val id: EntryId,
    val name: Name,
    val rootRelativePath: Path,
    val ordinalNumber: Int,
): Playable

@Serializable
data class MediaGrouping(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    override val tags: Set<String>,
    override val franchise: Franchise?,
    val entries: List<MediaGroupingEntry>,
): LibraryEntry, Directory, Taggable, Franchisable

@Serializable
sealed interface MediaGroupingEntry {
    val id: EntryId
    val name: Name
    val rootRelativePath: Path
}

@Serializable
data class MediaGroupingFilm(
    override val id: EntryId,
    override val name: Name,
    override val rootRelativePath: Path,
    val videoFiles: List<VideoFile>,
): Directory, MediaGroupingEntry


@Serializable
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

@Serializable
data class Franchise (
    val name: String,
)

interface Playable
package pl.przemyslawpitus.luminark.ui.navigation

import kotlinx.serialization.Serializable

class Destination {
    @Serializable
    data object Library

    @Serializable
    data class Series(val seriesId: String)

    @Serializable
    data class EpisodesGroup(val episodesGroupId: String)

    @Serializable
    data class FilmSeries(val filmSeriesId: String)

    @Serializable
    data class MediaGrouping(val mediaGroupingId: String)

    @Serializable
    data class MediaGroupingEpisodesGroup(val mediaGroupingId: String, val episodesGroupId: String)
}

package pl.przemyslawpitus.luminark.ui.navigation

sealed class Destination(val route: String) {
    data object Library : Destination(route = "library")

    data object Series : Destination(route = "series/{seriesId}") {
        const val seriesIdArg = "seriesId"
        fun createRoute(seriesId: String) = "series/$seriesId"
    }

    data object Season : Destination(route = "season/{seasonId}") {
        const val seasonIdArg = "seasonId"
        fun createRoute(seasonId: String) = "season/$seasonId"
    }

    data object FilmSeries : Destination(route = "film-series/{filmSeriesId}") {
        const val filmSeriesIdArg = "filmSeriesId"
        fun createRoute(filmSeriesId: String) = "film-series/$filmSeriesId"
    }

    data object MediaGrouping : Destination(route = "media-grouping/{groupingId}") {
        const val groupingIdArg = "groupingId"
        fun createRoute(groupingId: String) = "media-grouping/$groupingId"
    }
}

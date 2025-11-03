package pl.przemyslawpitus.luminark.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.przemyslawpitus.luminark.ui.screens.EpisodesScreen.EpisodesScreen
import pl.przemyslawpitus.luminark.ui.screens.SeriesScreen.SeriesScreen
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.LibraryScreen
import pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen.MediaGroupingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.Library.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(Destination.Library.route) {
            LibraryScreen(
                navController = navController,
            )
        }
        composable(
            route = Destination.Series.route,
            arguments = listOf(navArgument(Destination.Series.seriesIdArg) { type = NavType.StringType })
        ) {
            SeriesScreen(
                navController = navController,
            )
        }
        composable(
            route = Destination.Season.route,
            arguments = listOf(navArgument(Destination.Season.seasonIdArg) { type = NavType.StringType })
        ) {
            EpisodesScreen()
        }
        composable(
            route = Destination.MediaGrouping.route,
            arguments = listOf(navArgument(Destination.MediaGrouping.groupingIdArg) { type = NavType.StringType })
        ) {
            MediaGroupingScreen(
                navController = navController,
            )
        }

//        composable("film-series/{seriesId}") { backStackEntry ->
//            val seriesId = backStackEntry.arguments?.getString("seriesId")
//
//            val filmSeriesView = uiState.entries.filterIsInstance<FilmSeriesView>().find { it.id.id == seriesId }!!
//
//            FilmSeriesScreen(
//                filmSeriesView = filmSeriesView,
//            )
//        }
//        composable("episodes/{seasonId}") { backStackEntry ->
//            val seasonId = backStackEntry.arguments?.getString("seasonId")
//
//            // TODO Refactor this
//            val seasonView1 = uiState.entries
//                .filterIsInstance<SeriesView>()
//                .flatMap { it.seasons }
//                .find { it.id.id == seasonId }
//
//            val seasonView2 = uiState.entries
//                .filterIsInstance<MediaGroupingView>()
//                .flatMap { it.entries.filterIsInstance<SeasonView>() }
//                .find { it.id.id == seasonId }
//
//            EpisodesScreen(
//                seasonView = (seasonView1 ?: seasonView2)!!
//            )
//        }
//
//        composable("media-grouping/{groupingId}") { backStackEntry ->
//            val groupingId = backStackEntry.arguments?.getString("groupingId")
//
//            val mediaGroupingView =
//                uiState.entries.filterIsInstance<MediaGroupingView>().find { it.id.id == groupingId }!!
//
//            MediaGroupingsScreen(
//                mediaGroupingView = mediaGroupingView,
//                onSeasonClick = { seasonId ->
//                    navController.navigate("episodes/${seasonId.id}")
//                },
//                onFilmClick = { absolutePath ->
//                    viewModel.videoPlayer.playVideo(absolutePath)
//                }
//            )
//        }
    }
}

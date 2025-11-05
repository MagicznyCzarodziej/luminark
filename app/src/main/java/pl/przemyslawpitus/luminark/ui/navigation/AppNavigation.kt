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
import pl.przemyslawpitus.luminark.ui.screens.FilmSeriesScreen.FilmSeriesScreen
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.LibraryScreen
import pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen.MediaGroupingScreen
import pl.przemyslawpitus.luminark.ui.screens.SeriesScreen.SeriesScreen

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
        composable(
            route = Destination.FilmSeries.route,
            arguments = listOf(navArgument(Destination.FilmSeries.filmSeriesIdArg) { type = NavType.StringType })
        ) {
            FilmSeriesScreen()
        }
    }
}

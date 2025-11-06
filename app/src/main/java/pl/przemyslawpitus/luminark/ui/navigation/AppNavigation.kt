package pl.przemyslawpitus.luminark.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.przemyslawpitus.luminark.ui.screens.EpisodesScreen.EpisodesScreen
import pl.przemyslawpitus.luminark.ui.screens.FilmSeriesScreen.FilmSeriesScreen
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.LibraryScreen
import pl.przemyslawpitus.luminark.ui.screens.MediaGroupingEpisodesGroupScreen.MediaGroupingEpisodesGroupScreen
import pl.przemyslawpitus.luminark.ui.screens.MediaGroupingScreen.MediaGroupingScreen
import pl.przemyslawpitus.luminark.ui.screens.SeriesScreen.SeriesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.Library,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable<Destination.Library> {
            LibraryScreen(
                navController = navController,
            )
        }
        composable<Destination.Series> {
            SeriesScreen(
                navController = navController,
            )
        }
        composable<Destination.EpisodesGroup> {
            EpisodesScreen()
        }
        composable<Destination.MediaGrouping> {
            MediaGroupingScreen(
                navController = navController,
            )
        }
        composable<Destination.MediaGroupingEpisodesGroup> {
            MediaGroupingEpisodesGroupScreen()
        }
        composable<Destination.FilmSeries> {
            FilmSeriesScreen()
        }
    }
}

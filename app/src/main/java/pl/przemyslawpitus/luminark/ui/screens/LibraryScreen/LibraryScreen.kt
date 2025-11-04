package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.LibraryList
import pl.przemyslawpitus.luminark.ui.screens.components.Poster.Poster

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val posterData by viewModel.posterData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPoster()
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToFilmSeries -> navController.navigate(Destination.FilmSeries.createRoute(event.filmSeriesId.id))
                is NavigationEvent.ToMediaGrouping -> navController.navigate(Destination.MediaGrouping.createRoute(event.groupingId.id))
                is NavigationEvent.ToSeries -> navController.navigate(Destination.Series.createRoute(event.seriesId.id))
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ładowanie...")
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Poster(posterData)
            Column {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color(0x33F5F5F5)
                    )
                    Text(
                        text = "Search library...",
                        fontSize = 20.sp,
                        color = Color(0x33F5F5F5),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                    Box(
                        modifier = Modifier.background(
                            Color(0xFF1A1B2C),
                            RoundedCornerShape(4.dp)
                        )
                    ) {
                        Text(
                            text = "All",
                            fontSize = 20.sp,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(16.dp, 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.background(
                            Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                    ) {
                        Text(
                            text = "Films",
                            fontSize = 20.sp,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(16.dp, 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.background(
                            Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                    ) {
                        Text(
                            text = "Series",
                            fontSize = 20.sp,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(16.dp, 2.dp)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .background(
                                Color(0x33D9D9D9),
                            )
                            .width(1.dp)
                            .height(16.dp)
                            .padding(8.dp, 0.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0x33FFFFFF),
                        modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                    )

                }
                LibraryList(
                    entries = uiState.entries,
                    onEntryClick = viewModel::onEntryClick
                )
            }
        }

    }
}
package pl.przemyslawpitus.luminark.ui.screens.SeriesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.navigation.Destination
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.ClickableLibraryListEntry

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SeriesScreen(
    navController: NavController,
) {
    val viewModel: SeriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var lastFocusedIndex by rememberSaveable { mutableStateOf(0) }

    if (uiState.isLoading) {
        Text("Loading...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF191A25)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Seasons for: ${uiState.series!!.name.name}",
                modifier = Modifier.padding(16.dp),
                color = Color.White
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(uiState.series!!.seasons) { index, season ->
                    val focusRequester = remember { FocusRequester() }

                    ClickableLibraryListEntry(
                        focusRequester = focusRequester,
                        lastFocusedIndex = lastFocusedIndex,
                        onFocusChange = { isFocused: Boolean ->
                            if (isFocused) {
                                lastFocusedIndex = index
                            }
                        },
                        index = index,
                        onEntryClick = {
                            navController.navigate(Destination.Season.createRoute(season.id.id))
                        },
                    ) {
                        Text(
                            text = season.name,
                            color = Color(0xFFF5F5F5),
                            fontSize = 22.sp,
                        )
                    }

                    if (index == lastFocusedIndex) {
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                }
            }
        }
    }
}
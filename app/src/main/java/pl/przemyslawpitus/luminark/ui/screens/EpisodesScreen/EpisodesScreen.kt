package pl.przemyslawpitus.luminark.ui.screens.EpisodesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EpisodesScreen() {
    val viewModel: EpisodesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var lastFocusedIndex by rememberSaveable { mutableStateOf(0) }

    if (uiState.isLoading) {
        Text("Loading...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090A1A)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Episodes for: TODO", modifier = Modifier.padding(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(uiState.episodes!!) { index, episode ->
                    val focusRequester = remember { FocusRequester() }

                    Text(
                        text = episode.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    lastFocusedIndex = index
                                }
                            }
                            .clickable {
                                viewModel.playVideo(episode.absolutePath)
                            }
                            .background(
                                if (lastFocusedIndex == index) Color(0xFF2A2635) else Color.Transparent,
                                RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 4.dp, bottomEnd = 4.dp)
                            )
                            .padding(8.dp),
                        color = Color.White
                    )

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
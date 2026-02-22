package pl.przemyslawpitus.luminark.ui.components.EntriesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.EntriesListState

data class ListEntryUiModel(
    val name: Name,
    val type: Type,
    val onClick: () -> Unit,
    val onFocus: () -> Unit,
    val isLoading: Boolean = false,
) {
    sealed class Type {
        data object Single : Type()
        data class PlayablesGroup(val size: Int) : Type()
        data class Series(val size: Int) : Type()
        data class Grouping(val size: Int) : Type()
    }
}
enum class NameDisplayStrategy {
    REGULAR, LIBRARY,
}

@Composable
fun EntriesList(
    modifier: Modifier = Modifier,
    entries: List<ListEntryUiModel>,
    nameDisplayStrategy: NameDisplayStrategy = NameDisplayStrategy.REGULAR,
    state: EntriesListState? = null
) {
    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    SideEffect {
        state?.lazyListState = listState
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        modifier = modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()
                val isScrolled = listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
                if (isScrolled) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            endY = 80f
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            }
    ) {
        itemsIndexed(entries) { index, entry ->
            val focusRequester = remember { FocusRequester() }
            state?.focusRequesters[index] = focusRequester

            ClickableListEntry(
                focusRequester = focusRequester,
                lastFocusedIndex = lastFocusedIndex,
                onFocusChange = { isFocused: Boolean ->
                    if (isFocused) {
                        lastFocusedIndex = index
                        entry.onFocus()
                    }
                },
                index = index,
                onEntryClick = entry.onClick,
            ) {
                ListEntry(
                    name = entry.name,
                    type = entry.type,
                    nameDisplayStrategy = nameDisplayStrategy,
                    isFocused = lastFocusedIndex == index,
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
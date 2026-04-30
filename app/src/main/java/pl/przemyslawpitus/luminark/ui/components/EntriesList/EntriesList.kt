package pl.przemyslawpitus.luminark.ui.components.EntriesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.ui.TestTags
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
    // Survives recomposition via rememberSaveable. Tracks which entry had focus
    // so it can be auto-focused when the list recomposes (e.g. after returning
    // from another screen).
    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    // When filtering shrinks the list, lastFocusedIndex could point beyond
    // the new list bounds (e.g. was 40, but filtered list has 18 entries).
    // Clamp to 0 to prevent scrollAndFocusEntry from targeting a non-existent item.
    if (entries.isNotEmpty() && lastFocusedIndex >= entries.size) {
        lastFocusedIndex = 0
    }

    // Expose the LazyListState to EntriesListState so that scrollAndFocusEntry
    // (in LibraryScreen) can scroll this list programmatically.
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

            // Register this entry's FocusRequester on the shared EntriesListState.
            // Keyed on BOTH index and state — when filtering changes the entries,
            // a new EntriesListState is created (via remember(uiState.entries)),
            // and this effect must re-run to register on the new instance.
            // Without the `state` key, the effect wouldn't re-run (same index),
            // leaving the new state's focusRequesters map empty.
            DisposableEffect(index, state) {
                state?.focusRequesters?.set(index, focusRequester)
                onDispose { state?.focusRequesters?.remove(index) }
            }

            ClickableListEntry(
                focusRequester = focusRequester,
                isSelected = lastFocusedIndex == index,
                onFocusChange = { isFocused: Boolean ->
                    if (isFocused) {
                        lastFocusedIndex = index
                        state?.onEntryFocused(index)
                        entry.onFocus()
                    }
                },
                onEntryClick = entry.onClick,
                modifier = Modifier.testTag(TestTags.entryItem(index)),
            ) {
                ListEntry(
                    name = entry.name,
                    type = entry.type,
                    nameDisplayStrategy = nameDisplayStrategy,
                    isFocused = lastFocusedIndex == index,
                )
            }

            // Auto-focus the entry that matches lastFocusedIndex when it gets composed.
            // This handles initial focus and restoring focus after recomposition.
            if (index == lastFocusedIndex) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}
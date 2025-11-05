package pl.przemyslawpitus.luminark.ui.components.EntriesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import pl.przemyslawpitus.luminark.domain.library.Name

data class ListEntryUiModel(
    val name: Name,
    val type: Type,
    val onClick: () -> Unit,
    val onFocus: () -> Unit,
) {
    sealed class Type {
        data object Single : Type()
        data class PlayablesGroup(val size: Int) : Type()
        data class Series(val size: Int) : Type()
        data class Grouping(val size: Int) : Type()
    }
}

@Composable
fun EntriesList(
    entries: List<ListEntryUiModel>,
    modifier: Modifier = Modifier,
) {
    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(0) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        itemsIndexed(entries) { index, entry ->
            val focusRequester = remember { FocusRequester() }

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
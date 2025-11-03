package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
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
import pl.przemyslawpitus.luminark.ui.TopLevelLibraryEntry

@Composable
fun LibraryList(
    entries: List<TopLevelLibraryEntry>,
    onEntryClick: (entry: TopLevelLibraryEntry) -> Unit,
) {
    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(0) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
    ) {
        itemsIndexed(entries) { index, entry ->
            val focusRequester = remember { FocusRequester() }

            LibraryListEntry(
                entry = entry,
                focusRequester = focusRequester,
                lastFocusedIndex = lastFocusedIndex,
                onFocusChange = { isFocused: Boolean ->
                    if (isFocused) {
                        lastFocusedIndex = index
                    }
                },
                index = index,
                onEntryClick = onEntryClick,
            )

            if (index == lastFocusedIndex) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}



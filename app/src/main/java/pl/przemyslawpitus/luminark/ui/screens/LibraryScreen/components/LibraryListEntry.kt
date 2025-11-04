package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import pl.przemyslawpitus.luminark.ui.FilmSeriesView
import pl.przemyslawpitus.luminark.ui.FilmView
import pl.przemyslawpitus.luminark.ui.MediaGroupingView
import pl.przemyslawpitus.luminark.ui.SeriesView
import pl.przemyslawpitus.luminark.ui.TopLevelLibraryEntry
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries.FilmEntry
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries.FilmSeriesEntry
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries.MediaGroupingEntry
import pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries.SeriesEntry

@Composable
fun LibraryListEntry(
    entry: TopLevelLibraryEntry,
    focusRequester: FocusRequester,
    lastFocusedIndex: Int,
    onFocusChange: (Boolean) -> Unit,
    index: Int,
    onEntryClick: (entry: TopLevelLibraryEntry) -> Unit,
) {
    val isFocused = index == lastFocusedIndex

    val component: @Composable () -> Unit = when (entry) {
        is SeriesView -> ({ SeriesEntry(seriesView = entry, isFocused = isFocused) })

        is FilmView -> ({ FilmEntry(filmView = entry, isFocused = isFocused) })

        is MediaGroupingView -> ({
            MediaGroupingEntry(
                mediaGroupingView = entry,
                isFocused = isFocused,
            )
        })

        is FilmSeriesView -> ({
            FilmSeriesEntry(
                filmSeriesEntry = entry,
                isFocused = isFocused,
            )
        })

        else -> {
            return
        }
    }

    ClickableLibraryListEntry(
        focusRequester = focusRequester,
        lastFocusedIndex = lastFocusedIndex,
        onFocusChange = onFocusChange,
        index = index,
        onEntryClick = { onEntryClick(entry) },
    ) {
        component()
    }
}
package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.TestTags

/**
 * Filter buttons row (All / Films / Series).
 *
 * D-pad focus containment:
 * - Up: blocked on all buttons (FocusRequester.Cancel) — nothing above the top bar
 * - Left: blocked on the first button — prevents escaping left
 * - Right: blocked on the last button — prevents escaping right
 * - Down: intercepted via onPreviewKeyEvent to call [onNavigateDown],
 *   which scrolls to and focuses the last focused entry in the list.
 *   We can't rely on Compose's default spatial focus because the entries
 *   list may be scrolled and the target entry may not be composed yet.
 */
@Composable
fun TopBar(
    onFilterChanged: (EntriesFilter) -> Unit,
    onNavigateDown: () -> Unit,
) {
    var focusedFilter by remember {
        mutableStateOf<EntriesFilter?>(null)
    }

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
            modifier = Modifier
                .testTag(TestTags.topBarButton(0))
                // FocusRequester.Cancel blocks focus movement in that direction
                .focusProperties {
                    up = FocusRequester.Cancel
                    left = FocusRequester.Cancel
                }
                // Intercept Down to use custom scroll-and-focus instead of spatial navigation
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
                        onNavigateDown(); true
                    } else false
                }
                .onFocusChanged {
                    focusedFilter = if (it.isFocused) EntriesFilter.ALL else null
                }
                .background(
                    color = if (focusedFilter == EntriesFilter.ALL) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    onFilterChanged(EntriesFilter.ALL)
                }
        ) {
            Text(
                text = "All",
                fontSize = 20.sp,
                color = if (focusedFilter == EntriesFilter.ALL) Color.Black else Color.White,
                modifier = Modifier.padding(16.dp, 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .testTag(TestTags.topBarButton(1))
                .focusProperties {
                    up = FocusRequester.Cancel
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
                        onNavigateDown(); true
                    } else false
                }
                .onFocusChanged {
                    focusedFilter = if (it.isFocused) EntriesFilter.FILMS else null
                }
                .background(
                    color = if (focusedFilter == EntriesFilter.FILMS) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    onFilterChanged(EntriesFilter.FILMS)
                }
        ) {
            Text(
                text = "Films",
                fontSize = 20.sp,
                color = if (focusedFilter == EntriesFilter.FILMS) Color.Black else Color.White,
                modifier = Modifier.padding(16.dp, 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .testTag(TestTags.topBarButton(2))
                .focusProperties {
                    up = FocusRequester.Cancel
                    right = FocusRequester.Cancel
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
                        onNavigateDown(); true
                    } else false
                }
                .onFocusChanged {
                    focusedFilter = if (it.isFocused) EntriesFilter.SERIES else null
                }
                .background(
                    color = if (focusedFilter == EntriesFilter.SERIES) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    onFilterChanged(EntriesFilter.SERIES)
                }
        ) {
            Text(
                text = "Series",
                fontSize = 20.sp,
                color = if (focusedFilter == EntriesFilter.SERIES) Color.Black else Color.White,
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
}
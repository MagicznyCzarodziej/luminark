package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ClickableLibraryListEntry(
    focusRequester: FocusRequester,
    lastFocusedIndex: Int,
    onFocusChange: (Boolean) -> Unit,
    index: Int,
    onEntryClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
            }
            .clickable {
                onEntryClick()
            }
            .background(
                if (lastFocusedIndex == index) Color(0xFF2A2635) else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
    ) {
        content()
    }
}
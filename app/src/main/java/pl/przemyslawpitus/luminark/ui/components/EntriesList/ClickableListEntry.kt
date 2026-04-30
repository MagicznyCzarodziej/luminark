package pl.przemyslawpitus.luminark.ui.components.EntriesList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ClickableListEntry(
    focusRequester: FocusRequester,
    isSelected: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onEntryClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChange(focusState.isFocused)
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // disable default background
                onClick = { onEntryClick() }
            )
            .background(
                brush = when {
                    isSelected && isFocused -> Brush.linearGradient(
                        0.0f to Color(0xFF1A2367),
                        0.9f to Color(0x000A0E23),
                        1f to Color.Transparent,
                    )
                    isSelected -> Brush.linearGradient(
                        0.0f to Color(0x801A2367),
                        0.9f to Color(0x000A0E23),
                        1f to Color.Transparent,
                    )
                    else -> Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                },
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
    ) {
        content()
    }
}
package pl.przemyslawpitus.luminark.ui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.focusableBackground(
    unfocusedColor: Color,
    focusedColor: Color,
    shape: Shape = RoundedCornerShape(4.dp)
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        }
        .background(
            color = if (isFocused) focusedColor else unfocusedColor,
            shape = shape
        )
}
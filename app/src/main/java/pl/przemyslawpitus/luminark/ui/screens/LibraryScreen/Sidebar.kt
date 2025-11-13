package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import pl.przemyslawpitus.luminark.letIf
import pl.przemyslawpitus.luminark.ui.modifiers.focusableBackground


@Composable
fun Sidebar(
    rebuildLibrary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    var initializationComplete: Boolean by remember { mutableStateOf(false) }
    var focusState by remember { mutableStateOf<FocusState?>(null) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open && focusState?.hasFocus == false) {
            // used to grab focus if the drawer state is set to Open on start.
            focusRequester.requestFocus()
        }

        initializationComplete = true
    }

    Box(
        modifier = modifier
            .width(300.dp)
            .graphicsLayer {
                translationX =
                    if (drawerState.currentValue == DrawerValue.Closed) {
                        300.dp.toPx()
                    } else {
                        0f
                    }
            }
            .background(Color(0xFF0B0C1D))
            .shadow(elevation = 2.dp)
            .focusRequester(focusRequester)
            .fillMaxHeight()
            .onFocusChanged {
                focusState = it

                if (initializationComplete) {
                    drawerState.setValue(if (it.hasFocus) DrawerValue.Open else DrawerValue.Closed)
                }
            }
            .focusGroup()
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MenuItem(
                "Rebuild the library",
                onClick = { rebuildLibrary() }
            )
            MenuItem(
                "Something else",
                onClick = { println("Click") },
                isLast = true,
            )
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    onClick: () -> Unit,
    isLast: Boolean = false,
) {
    Text(
        text,
        color = Color.White,
        modifier = Modifier
            .letIf(isLast) { it: Modifier ->
                it.focusProperties { down = FocusRequester.Cancel }
            }
            .focusableBackground(
                unfocusedColor = Color.Transparent,
                focusedColor = Color(0xFF1A2367),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(4.dp)
    )
}
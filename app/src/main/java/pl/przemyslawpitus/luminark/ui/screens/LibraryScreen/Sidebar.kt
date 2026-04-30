package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import kotlinx.coroutines.launch
import pl.przemyslawpitus.luminark.ui.TestTags

private val SidebarBackground = Color(0xFF0D0E1F)
private val SidebarFocusedBg = Color(0xFF1A2367)
private val SidebarDivider = Color(0xFF1A1C30)
private val SidebarLabelColor = Color(0xFF6B6F8A)
private val SidebarTextColor = Color(0xFFD0D3E3)
private val SidebarFocusedTextColor = Color.White


@Composable
fun Sidebar(
    tags: List<String>,
    rebuildLibrary: () -> Unit,
    filterByTag: (tag: String?) -> Unit,
    onExitSidebar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    var initializationComplete: Boolean by remember { mutableStateOf(false) }
    var focusState by remember { mutableStateOf<FocusState?>(null) }
    /** FocusRequester for the entire sidebar Box (the focusGroup container). */
    val focusRequester = remember { FocusRequester() }
    /** FocusRequester for the first menu item — used to override focusGroup's
     *  default behavior of restoring focus to the last focused child. */
    val firstItemFocusRequester = remember { FocusRequester() }
    /** Scroll state for the tags LazyColumn — reset to top when sidebar reopens. */
    val tagsListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // On initial open, if the sidebar doesn't have focus yet, grab it.
    // This handles the case where drawerState starts as Open.
    LaunchedEffect(key1 = drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open && focusState?.hasFocus == false) {
            focusRequester.requestFocus()
        }
        initializationComplete = true
    }

    Box(
        modifier = modifier
            .testTag(TestTags.SIDEBAR)
            .width(280.dp)
            .graphicsLayer {
                translationX =
                    if (drawerState.currentValue == DrawerValue.Closed) {
                        280.dp.toPx()
                    } else {
                        0f
                    }
            }
            .background(SidebarBackground)
            .focusRequester(focusRequester)
            .fillMaxHeight()
            .onFocusChanged {
                val wasOpen = focusState?.hasFocus == true
                focusState = it

                if (initializationComplete) {
                    // Show/hide sidebar based on whether any child has focus
                    drawerState.setValue(if (it.hasFocus) DrawerValue.Open else DrawerValue.Closed)

                    // When the sidebar gains focus from outside (wasOpen=false -> hasFocus=true):
                    // 1. Reset the tags LazyColumn scroll to the top — otherwise the list
                    //    stays scrolled to wherever the user left it last time
                    // 2. Force focus to the first item — without this, focusGroup restores
                    //    focus to the last focused child, which may be off-screen
                    if (it.hasFocus && !wasOpen) {
                        scope.launch { tagsListState.scrollToItem(0) }
                        firstItemFocusRequester.requestFocus()
                    }
                }
            }
            // focusGroup makes D-pad navigate between children (menu items)
            .focusGroup()
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(start = 20.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            // ── Actions section ──────────────────────────────────────
            MenuItem(
                text = "Rebuild library",
                onClick = { rebuildLibrary() },
                onExitSidebar = onExitSidebar,
                isFirst = true,
                isLast = tags.isEmpty(),
                index = 0,
                focusRequester = firstItemFocusRequester,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SidebarLabelColor,
                    )
                },
            )

            // ── Divider ─────────────────────────────────────────────
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SidebarDivider)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ── Tags section ────────────────────────────────────────
            Text(
                "TAGS",
                color = SidebarLabelColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
            )

            LazyColumn(
                state = tagsListState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                item {
                    MenuItem(
                        text = "All",
                        onClick = { filterByTag(null) },
                        onExitSidebar = onExitSidebar,
                        isLast = tags.isEmpty(),
                        index = 1,
                    )
                }
                itemsIndexed(tags) { tagIndex, tag ->
                    MenuItem(
                        text = tag.replaceFirstChar { it.uppercase() },
                        onClick = { filterByTag(tag) },
                        onExitSidebar = onExitSidebar,
                        isLast = tagIndex == tags.lastIndex,
                        index = tagIndex + 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    onClick: () -> Unit,
    onExitSidebar: () -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    index: Int = 0,
    focusRequester: FocusRequester? = null,
    icon: @Composable (() -> Unit)? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    var modifier = Modifier
        .testTag(TestTags.sidebarItem(index))
    if (focusRequester != null) {
        modifier = modifier.focusRequester(focusRequester)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            // D-pad containment: Left/Right exit the sidebar, Up/Down blocked at edges.
            // Returning true from onPreviewKeyEvent consumes the event (blocks it).
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionLeft, Key.DirectionRight -> {
                        onExitSidebar()
                        true
                    }
                    Key.DirectionUp -> isFirst   // block Up on first item
                    Key.DirectionDown -> isLast  // block Down on last item
                    else -> false
                }
            }
            .onFocusChanged { isFocused = it.isFocused }
            .background(
                color = if (isFocused) SidebarFocusedBg else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
            text,
            color = if (isFocused) SidebarFocusedTextColor else SidebarTextColor,
            fontSize = 15.sp,
        )
    }
}

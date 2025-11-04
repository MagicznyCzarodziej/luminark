package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.MediaGroupingView

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MediaGroupingEntry(
    mediaGroupingView: MediaGroupingView,
    isFocused: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = mediaGroupingView.name.name,
                color = Color(0xFFF5F5F5),
                fontSize = 20.sp,
            )

            mediaGroupingView.name.alternativeName?.let { altName ->
                Text(
                    text = altName,
                    fontSize = 10.sp,
                    color = Color(0xFFF5F5F5),
                )
            }
        }
        if (isFocused) {
            Icon(
                imageVector = Icons.Outlined.FolderCopy,
                contentDescription = null,
                tint = Color(0x80FFFFFF)
            )
            Text(
                text = mediaGroupingView.entries.size.toString(),
                fontSize = 14.sp,
                color = Color(0x80FFFFFF),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
package pl.przemyslawpitus.luminark.ui.components.EntriesList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.R
import pl.przemyslawpitus.luminark.domain.library.Name

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ListEntry(
    name: Name,
    type: ListEntryUiModel.Type,
    isFocused: Boolean,
) {
    val right: @Composable () -> Unit = when (type) {
        is ListEntryUiModel.Type.Single -> ({
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = Color(0x80FFFFFF),
                modifier = Modifier.padding(end = 12.dp) // Compensate for no text
            )
        })

        is ListEntryUiModel.Type.Series -> ({
            Icon(
                imageVector = Icons.Outlined.AutoAwesomeMotion,
                contentDescription = null,
                tint = Color(0x80FFFFFF)
            )
            Text(
                text = type.size.toString(),
                fontSize = 14.sp,
                color = Color(0x80FFFFFF),
                modifier = Modifier.padding(start = 4.dp)
            )
        })

        is ListEntryUiModel.Type.PlayablesGroup -> ({
            Icon(
                painter = painterResource(id = R.drawable.icon_animation_play_outline),
                contentDescription = null,
                tint = Color(0x80FFFFFF)
            )
            Text(
                text = type.size.toString(),
                fontSize = 14.sp,
                color = Color(0x80FFFFFF),
                modifier = Modifier.padding(start = 4.dp)
            )
        })

        is ListEntryUiModel.Type.Grouping -> ({
            Icon(
                imageVector = Icons.Outlined.FolderCopy,
                contentDescription = null,
                tint = Color(0x80FFFFFF)
            )
            Text(
                text = type.size.toString(),
                fontSize = 14.sp,
                color = Color(0x80FFFFFF),
                modifier = Modifier.padding(start = 4.dp)
            )
        })
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name.name,
                color = Color(0xFFF5F5F5),
                fontSize = 22.sp,
            )

            name.alternativeName?.let { alternativeName ->
                Text(
                    text = alternativeName,
                    fontSize = 12.sp,
                    color = Color(0xFFF5F5F5),
                )
            }
        }
        if (isFocused) {
            right()
        }
    }
}
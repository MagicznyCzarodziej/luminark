package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.domain.library.StandaloneFilm

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FilmEntry(
    filmView: StandaloneFilm,
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
                text = filmView.name.name,
                color = Color(0xFFF5F5F5),
                fontSize = 22.sp,
            )

            filmView.name.alternativeName?.let { altName ->
                Text(
                    text = altName,
                    fontSize = 12.sp,
                    color = Color(0xFFF5F5F5),
                )
            }
        }
        if (isFocused) {
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = Color(0x80FFFFFF),
                modifier = Modifier.padding(end = 12.dp) // Compensate for no text
            )
        }
    }
}
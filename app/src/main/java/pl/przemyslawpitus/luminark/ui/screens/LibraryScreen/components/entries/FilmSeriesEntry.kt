package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.R
import pl.przemyslawpitus.luminark.ui.FilmSeriesView

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FilmSeriesEntry(
    filmSeriesEntry: FilmSeriesView,
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
                text = filmSeriesEntry.name.name,
                color = Color(0xFFF5F5F5),
                fontSize = 22.sp,
            )

            filmSeriesEntry.name.alternativeName?.let { altName ->
                Text(
                    text = altName,
                    fontSize = 12.sp,
                    color = Color(0xFFF5F5F5),
                )
            }
        }
        if (isFocused) {
            Icon(
                painter = painterResource(id = R.drawable.icon_animation_play_outline),
                contentDescription = null,
                tint = Color(0x80FFFFFF)
            )
            Text(
                text = filmSeriesEntry.films.size.toString(),
                fontSize = 12.sp,
                color = Color(0xFFF5F5F5),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
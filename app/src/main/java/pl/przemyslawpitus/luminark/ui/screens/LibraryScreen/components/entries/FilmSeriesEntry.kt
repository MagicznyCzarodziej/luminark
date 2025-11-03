package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.FilmSeriesView

@Composable
fun FilmSeriesEntry(
    filmSeriesEntry: FilmSeriesView,
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
        Text(
            text = filmSeriesEntry.films.size.toString() + " film(s)",
            fontSize = 12.sp,
            color = Color(0xFFF5F5F5),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
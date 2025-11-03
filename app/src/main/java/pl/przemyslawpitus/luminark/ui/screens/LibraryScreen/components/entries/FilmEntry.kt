package pl.przemyslawpitus.luminark.ui.screens.LibraryScreen.components.entries

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import pl.przemyslawpitus.luminark.ui.FilmView

@Composable
fun FilmEntry(
    filmView: FilmView,
) {
    Column {
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
}
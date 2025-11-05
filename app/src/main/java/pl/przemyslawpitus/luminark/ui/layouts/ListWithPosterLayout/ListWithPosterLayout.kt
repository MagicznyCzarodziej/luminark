package pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.przemyslawpitus.luminark.ui.components.EntriesList.EntriesList
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.components.Poster.Poster

@Composable
fun ListWithPosterLayout(
    posterData: ByteArray?,
    breadcrumbs: String,
    title: String,
    subtitle: String? = null,
    tags: Set<String>,
    entries: List<ListEntryUiModel>,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A1A)),
    ) {
        Poster(posterData)
        Column {
            Header(
                breadcrumbs = breadcrumbs,
                title = title,
                subtitle = subtitle,
                tags = tags,
            )
            EntriesList(
                entries = entries,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
            )
        }
    }
}
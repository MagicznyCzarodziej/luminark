package pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.przemyslawpitus.luminark.ui.components.EntriesList.EntriesList
import pl.przemyslawpitus.luminark.ui.components.EntriesList.ListEntryUiModel
import pl.przemyslawpitus.luminark.ui.components.Poster.Poster

data class ListWithPosterLayoutProps(
    val posterData: ByteArray?,
    val breadcrumbs: String,
    val title: String,
    val subtitle: String? = null,
    val tags: Set<String>,
    val entries: List<ListEntryUiModel>,
)

@Composable
fun ListWithPosterLayout(
    props: ListWithPosterLayoutProps?,
) {
    if (props == null) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090A1A)),
        ) {
            Box(
                Modifier.fillMaxWidth(0.37f)
            )
            Column {
                Box(
                    Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        .width(250.dp)
                        .height(18.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0x10FFFFFF),
                                    Color.Transparent,
                                )
                            ),
                            RoundedCornerShape(8.dp)
                        )
                )
                Box(
                    Modifier
                        .padding(start = 16.dp)
                        .width(400.dp)
                        .height(28.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0x10FFFFFF),
                                    Color.Transparent,
                                )
                            ),
                            RoundedCornerShape(8.dp)
                        )
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090A1A)),
        ) {
            Poster(
                props.posterData,
                Modifier.fillMaxWidth(0.37f)
            )
            Column {
                Header(
                    breadcrumbs = props.breadcrumbs,
                    title = props.title,
                    subtitle = props.subtitle,
                    tags = props.tags,
                )
                EntriesList(
                    entries = props.entries,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
                )
            }
        }
    }

}

@Preview(device = Devices.TV_1080p)
@Composable
fun Preview() {
    ListWithPosterLayout(null)
}
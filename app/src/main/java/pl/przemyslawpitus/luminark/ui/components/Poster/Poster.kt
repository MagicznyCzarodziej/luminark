package pl.przemyslawpitus.luminark.ui.components.Poster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import pl.przemyslawpitus.luminark.infrastructure.posterCache.coil.PosterFetcher
import java.nio.file.Path

@Composable
fun Poster(
    modifier: Modifier = Modifier,
    rootRelativeDirectoryPath: Path,
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(PosterFetcher.PosterPath(rootRelativeDirectoryPath))
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxHeight()
        )
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF090A1A)
                        )
                    )
                )
        )
    }
}

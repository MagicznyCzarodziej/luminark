package pl.przemyslawpitus.luminark.ui.components.Poster

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun Poster(
    imageData: ByteArray?,
    modifier: Modifier = Modifier,
) {
    val imageBitmap = remember(imageData) {
        imageData?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
        }
    }

    if (imageBitmap != null) {
        Box(modifier = modifier) {
            Image(
                bitmap = imageBitmap,
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
    } else {
        Box(modifier = modifier) { }
    }
}

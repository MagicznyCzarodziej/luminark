package pl.przemyslawpitus.luminark.ui.layouts.ListWithPosterLayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Header(
    breadcrumbs: String,
    title: String,
    subtitle: String?,
    tags: Set<String>
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
    ) {
        Text(
            text = breadcrumbs,
            fontSize = 18.sp,
            color = Color(0x80FFFFFF),
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        Text(
            text = title,
            fontSize = 28.sp,
            color = Color(0xFFF5F5F5),
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFFF5F5F5),
            )
        }
        tags.map {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        color = Color(0xFF772D82),
                        shape = RoundedCornerShape(100)
                    )
            ) {
                Icon(
                    modifier = Modifier
                        .height(12.dp)
                        .padding(start = 8.dp),
                    imageVector = Icons.Outlined.Sell,
                    contentDescription = null,
                    tint = Color(0xFFFFFFFF),
                )
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
                    color = Color(0xFFFFFFFF),
                    fontSize = 12.sp
                )
            }
        }
    }
}
package com.daedan.festabook.presentation.common.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.daedan.festabook.R

@Composable
fun CoilImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    builder: ImageRequest.Builder.() -> Unit = {},
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .apply(builder)
                .data(url)
                .crossfade(true)
                .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        placeholder = ColorPainter(Color.LightGray),
        fallback = painterResource(R.drawable.img_fallback),
        error = painterResource(R.drawable.img_fallback),
        modifier = modifier,
    )
}

@Composable
@Preview
fun CoilImagePreview() {
    CoilImage(
        url = "",
        contentDescription = "",
    )
}

package com.daedan.festabook.presentation.news.lost.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.CoilImage
import com.daedan.festabook.presentation.common.component.cardBackground

private const val ROUNDED_CORNER_SHAPE = 16

@Composable
fun LostItem(
    url: String,
    modifier: Modifier = Modifier,
    onLostItemClick: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE.dp),
        modifier =
            modifier
                .cardBackground(roundedCornerShape = ROUNDED_CORNER_SHAPE.dp)
                .aspectRatio(1f)
                .clickable(indication = null, interactionSource = null) { onLostItemClick() },
    ) {
        CoilImage(
            url = url,
            contentDescription = stringResource(R.string.lost_item),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
@Preview
private fun LostItemPreview() {
    LostItem(
        url = "https://i.imgur.com/Zblctu7.png",
        onLostItemClick = { },
    )
}

package com.daedan.festabook.presentation.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R

@Composable
fun Modifier.cardBackground(
    backgroundColor: Int = R.color.gray100,
    borderStroke: Dp = 1.dp,
    borderColor: Int = R.color.gray200,
    roundedCornerShape: Dp = 16.dp,
): Modifier =
    background(
        color = colorResource(backgroundColor),
        shape = RoundedCornerShape(roundedCornerShape),
    ).border(
        width = borderStroke,
        color = colorResource(borderColor),
        shape = RoundedCornerShape(roundedCornerShape),
    )

@Composable
@Preview(showBackground = true)
private fun CardBackgroundPreview() {
    Box(
        modifier =
            Modifier
                .cardBackground()
                .size(120.dp),
    )
}

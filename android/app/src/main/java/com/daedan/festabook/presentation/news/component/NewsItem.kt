package com.daedan.festabook.presentation.news.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daedan.festabook.R

private const val ICON_ROTATION_EXPANDED: Float = 180F
private const val ICON_ROTATION_COLLAPSED: Float = 0F

@Composable
fun NewsItem(
    title: String,
    description: String,
    isExpanded: Boolean,
    onclick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    createdAt: String? = null,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) ICON_ROTATION_EXPANDED else ICON_ROTATION_COLLAPSED,
    )
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = colorResource(R.color.gray100),
                    shape = RoundedCornerShape(16.dp),
                ).border(
                    width = 1.dp,
                    color = colorResource(R.color.gray200),
                    shape = RoundedCornerShape(16.dp),
                ).animateContentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onclick() }
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 14.sp,
            )
            Spacer(modifier = modifier.weight(1f))
            if (createdAt != null) {
                Text(
                    text = createdAt,
                    fontFamily = FontFamily(Font(R.font.pretendard_regular)),
                    fontSize = 10.sp,
                    color = colorResource(R.color.gray500),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_down),
                    contentDescription = stringResource(R.string.chevron_down),
                    modifier = Modifier.rotate(rotation),
                )
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description)
        }
    }
}

@Preview
@Composable
private fun FAQItemPreview() {
    NewsItem(
        title = stringResource(R.string.tab_faq_question, "주차는 어디에 가능한가요"),
        description = "미소집이요미소집이요미소집이요미소집이요미소집이요미소집이요미소집이요미소집이요",
        isExpanded = false,
        onclick = {},
    )
}

@Preview
@Composable
private fun NoticeItemPreview() {
    NewsItem(
        title = "공지사항 제목입니다.",
        description = "설명입니다.설명입니다.설명입니다.설명입니다.설명입니다.설명입니다.설명입니다.",
        isExpanded = true,
        onclick = {},
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_pin),
                contentDescription = "",
            )
        },
        createdAt = "11/12 12:00",
    )
}

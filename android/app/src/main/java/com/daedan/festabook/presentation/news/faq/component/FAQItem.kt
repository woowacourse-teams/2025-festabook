package com.daedan.festabook.presentation.news.faq.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel

private const val ICON_ROTATION_EXPANDED: Float = 180F
private const val ICON_ROTATION_COLLAPSED: Float = 0F

@Composable
fun FAQItem(
    faqItemUiModel: FAQItemUiModel,
    onclick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (faqItemUiModel.isExpanded) ICON_ROTATION_EXPANDED else ICON_ROTATION_COLLAPSED,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = faqItemUiModel.question,
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 14.sp,
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = stringResource(R.string.faq_expand),
                modifier = Modifier.rotate(rotation),
            )
        }

        if (faqItemUiModel.isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = faqItemUiModel.answer)
        }
    }
}

@Preview
@Composable
private fun FAQItemPreview() {
    FAQItem(
        faqItemUiModel =
            FAQItemUiModel(
                questionId = 1,
                question = "Q. 주차는 어디에 가능한가요?",
                answer = "널린게 미소집 앞마당입니다.널린게 미소집 앞마당입니다널린게 미소집 앞마당입니다널린게 미소집 앞마당입니다널린게 미소집 앞마당입니다",
                sequence = 1,
                isExpanded = true,
            ),
        onclick = {},
    )
}

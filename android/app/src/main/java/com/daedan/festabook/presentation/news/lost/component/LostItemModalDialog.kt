package com.daedan.festabook.presentation.news.lost.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.cardBackground
import com.daedan.festabook.presentation.news.lost.model.LostItemUiStatus
import com.daedan.festabook.presentation.news.lost.model.LostUiModel

private const val PADDING: Int = 16

@Composable
fun LostItemModalDialog(
    lostItem: LostUiModel.Item,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PADDING.dp),
        modifier =
            modifier
                .cardBackground(
                    backgroundColor = R.color.gray050,
                    borderStroke = 0.dp,
                    borderColor = R.color.gray050,
                ).padding(PADDING.dp),
    ) {
        LostItem(url = lostItem.imageUrl)
        Text(
            text = stringResource(R.string.modal_lost_item_location, lostItem.storageLocation),
            color = colorResource(R.color.gray500),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.pretendard_medium)),
        )
        Text(
            text = stringResource(R.string.modal_lost_item_created_at, lostItem.createdAt),
            color = colorResource(R.color.gray500),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.pretendard_medium)),
        )
    }
}

@Composable
@Preview
private fun LostItemModalDialogPreview() {
    LostItemModalDialog(
        lostItem =
            LostUiModel.Item(
                lostItemId = 1L,
                imageUrl = "",
                storageLocation = "미소 집",
                status = LostItemUiStatus.PENDING,
                createdAt = "2025-11-12",
            ),
    )
}

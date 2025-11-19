package com.daedan.festabook.presentation.news.lost.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.cardBackground
import com.daedan.festabook.presentation.news.lost.model.LostItemUiStatus
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.theme.FestabookColor
import com.daedan.festabook.presentation.theme.FestabookTypography

private const val PADDING: Int = 16

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemModalDialog(
    lostItem: LostUiModel.Item,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING.dp),
            modifier =
                modifier
                    .cardBackground(
                        backgroundColor = FestabookColor.white,
                        borderStroke = 0.dp,
                        borderColor = FestabookColor.white,
                    ).padding(PADDING.dp),
        ) {
            LostItem(url = lostItem.imageUrl)
            Text(
                text = stringResource(R.string.modal_lost_item_location, lostItem.storageLocation),
                color = FestabookColor.gray500,
                style = FestabookTypography.titleMedium,
            )
            Text(
                text = stringResource(R.string.modal_lost_item_created_at, lostItem.createdAt),
                color = FestabookColor.gray500,
                style = FestabookTypography.titleMedium,
            )
        }
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
        onDismiss = {},
    )
}

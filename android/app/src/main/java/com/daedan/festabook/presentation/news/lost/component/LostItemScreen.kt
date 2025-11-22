package com.daedan.festabook.presentation.news.lost.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.EmptyStateScreen
import com.daedan.festabook.presentation.common.component.LoadingStateScreen
import com.daedan.festabook.presentation.common.component.PULL_OFFSET_LIMIT
import com.daedan.festabook.presentation.common.component.PullToRefreshContainer
import com.daedan.festabook.presentation.news.component.NewsItem
import com.daedan.festabook.presentation.news.lost.LostUiState
import com.daedan.festabook.presentation.news.lost.model.LostItemUiStatus
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import timber.log.Timber

private const val SPAN_COUNT: Int = 2
private const val PADDING: Int = 8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemScreen(
    lostUiState: LostUiState,
    onLostGuideClick: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var clickedLostItem by remember { mutableStateOf<LostUiModel.Item?>(null) }

    clickedLostItem?.let {
        LostItemModalDialog(
            lostItem = it,
            onDismiss = { clickedLostItem = null },
        )
    }

    PullToRefreshContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) { pullToRefreshState ->
        when (lostUiState) {
            LostUiState.InitialLoading -> LoadingStateScreen()

            is LostUiState.Error -> {
                LaunchedEffect(lostUiState) {
                    Timber.w(lostUiState.throwable.stackTraceToString())
                }
            }

            is LostUiState.Refreshing -> {
                LostItemContent(
                    lostItems = lostUiState.oldLostItems,
                    onLostGuideClick = onLostGuideClick,
                    onLostItemClick = { },
                    modifier =
                        modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationY =
                                    pullToRefreshState.distanceFraction * PULL_OFFSET_LIMIT
                            },
                )
            }

            is LostUiState.Success -> {
                LostItemContent(
                    lostItems = lostUiState.lostItems,
                    onLostGuideClick = onLostGuideClick,
                    onLostItemClick = { clickedLostItem = it },
                    modifier =
                        modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationY =
                                    pullToRefreshState.distanceFraction * PULL_OFFSET_LIMIT
                            },
                )
            }
        }
    }
}

@Composable
private fun LostItemContent(
    lostItems: List<LostUiModel>,
    onLostGuideClick: () -> Unit,
    onLostItemClick: (LostUiModel.Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLostItemEmpty = lostItems.none { it is LostUiModel.Item }
    if (isLostItemEmpty) {
        EmptyStateScreen(modifier = modifier)
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(SPAN_COUNT),
        contentPadding = PaddingValues(top = PADDING.dp, bottom = PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(PADDING.dp),
        horizontalArrangement = Arrangement.spacedBy(PADDING.dp),
    ) {
        item(span = { GridItemSpan(SPAN_COUNT) }) {
            val guide = lostItems.firstOrNull() as? LostUiModel.Guide
            guide?.let {
                NewsItem(
                    title = stringResource(R.string.lost_item_guide),
                    description = it.description,
                    isExpanded = it.isExpanded,
                    onclick = onLostGuideClick,
                    icon =
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_info),
                                contentDescription = stringResource(R.string.info),
                            )
                        },
                )
            }
        }
        items(
            items = lostItems.drop(1).filterIsInstance<LostUiModel.Item>(),
            key = { lostItem -> lostItem.lostItemId },
        ) { lostItem ->
            LostItem(
                url = lostItem.imageUrl,
                onLostItemClick = { onLostItemClick(lostItem) },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LostItemContentPreview() {
    val dummyLostList: List<LostUiModel> =
        listOf(
            LostUiModel.Guide(
                description = "운영 시간: 09:00 ~ 18:00",
                isExpanded = true,
            ),
            LostUiModel.Item(
                lostItemId = 1L,
                imageUrl = "https://i.imgur.com/Zblctu7.png",
                storageLocation = "1층 안내데스크",
                status = LostItemUiStatus.PENDING,
                createdAt = "2025-11-10",
            ),
            LostUiModel.Item(
                lostItemId = 2L,
                imageUrl = "https://i.imgur.com/Zblctu7.png",
                storageLocation = "2층 분실물 보관함",
                status = LostItemUiStatus.PENDING,
                createdAt = "2025-11-12",
            ),
        )
    LostItemContent(
        lostItems = dummyLostList,
        onLostGuideClick = { },
        modifier = Modifier.fillMaxSize(),
        onLostItemClick = { },
    )
}

package com.daedan.festabook.presentation.news.notice.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.NoticeUiState.Companion.DEFAULT_POSITION
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import timber.log.Timber

private const val PADDING: Int = 8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    uiState: NoticeUiState,
    onNoticeClick: (NoticeUiModel) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) { pullToRefreshState ->
        when (uiState) {
            NoticeUiState.InitialLoading -> LoadingStateScreen()

            is NoticeUiState.Error -> {
                LaunchedEffect(uiState) {
                    Timber.w(uiState.throwable.stackTraceToString())
                }
            }

            is NoticeUiState.Refreshing -> {
                NoticeContent(
                    notices = uiState.oldNotices,
                    onNoticeClick = onNoticeClick,
                    modifier =
                        modifier.graphicsLayer {
                            translationY = pullToRefreshState.distanceFraction * PULL_OFFSET_LIMIT
                        },
                )
            }

            is NoticeUiState.Success -> {
                NoticeContent(
                    notices = uiState.notices,
                    expandPosition = uiState.expandPosition,
                    onNoticeClick = onNoticeClick,
                    modifier =
                        modifier.graphicsLayer {
                            translationY = pullToRefreshState.distanceFraction * PULL_OFFSET_LIMIT
                        },
                )
            }
        }
    }
}

@Composable
private fun NoticeContent(
    notices: List<NoticeUiModel>,
    onNoticeClick: (NoticeUiModel) -> Unit,
    modifier: Modifier = Modifier,
    expandPosition: Int = DEFAULT_POSITION,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(expandPosition) {
        listState.animateScrollToItem(expandPosition)
    }
    if (notices.isEmpty()) {
        EmptyStateScreen(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(top = PADDING.dp, bottom = PADDING.dp),
            verticalArrangement = Arrangement.spacedBy(PADDING.dp),
        ) {
            items(
                items = notices,
                key = { notice -> notice.id },
            ) { notice ->
                NewsItem(
                    title = notice.title,
                    description = notice.content,
                    isExpanded = notice.isExpanded,
                    onclick = { onNoticeClick(notice) },
                    icon = {
                        if (notice.isPinned) {
                            Icon(
                                painter = painterResource(R.drawable.ic_pin),
                                contentDescription = stringResource(R.string.iv_pin),
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_speaker),
                                contentDescription = stringResource(R.string.iv_speaker),
                            )
                        }
                    },
                    createdAt = notice.formattedCreatedAt,
                )
            }
        }
    }
}

@Preview
@Composable
private fun NoticeScreenPreview() {
    NoticeScreen(
        uiState =
            NoticeUiState.Success(
                notices = emptyList(),
                expandPosition = 0,
            ),
        onNoticeClick = { },
        isRefreshing = false,
        onRefresh = {},
    )
}

package com.daedan.festabook.presentation.news.notice.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.EmptyStateScreen
import com.daedan.festabook.presentation.common.component.LottieLoadingScreen
import com.daedan.festabook.presentation.news.component.NewsItem
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    uiState: NoticeUiState,
    onNoticeClick: (NoticeUiModel) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    pullToRefreshState.distanceFraction
    when (uiState) {
        NoticeUiState.InitialLoading, NoticeUiState.Loading -> LottieLoadingScreen()

        is NoticeUiState.Error -> {
            LaunchedEffect(uiState) {
                Timber.w(uiState.throwable.stackTraceToString())
            }
        }

        is NoticeUiState.Success -> {
            if (uiState.notices.isEmpty()) {
                EmptyStateScreen(modifier = modifier)
            } else {
                NoticeContent(
                    uiState = uiState,
                    onNoticeClick = onNoticeClick,
                    modifier = modifier,
                    onRefresh = onRefresh,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoticeContent(
    uiState: NoticeUiState.Success,
    onNoticeClick: (NoticeUiModel) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(isRefreshing = false, onRefresh = onRefresh, modifier = modifier) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            itemsIndexed(
                items = uiState.notices,
                key = { _, notice -> notice.id },
            ) { index, notice ->
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
                    modifier =
                        Modifier.padding(
                            top = if (index == 0) 12.dp else 6.dp,
                            bottom = if (index == uiState.notices.lastIndex) 12.dp else 6.dp,
                        ),
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
                noticeIdToExpandPosition = -1,
            ),
        onNoticeClick = { },
        onRefresh = {},
    )
}

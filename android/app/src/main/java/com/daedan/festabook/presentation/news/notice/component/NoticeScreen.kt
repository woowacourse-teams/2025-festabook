package com.daedan.festabook.presentation.news.notice.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.EmptyStateScreen
import com.daedan.festabook.presentation.news.component.NewsItem
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import timber.log.Timber

@Composable
fun NoticeScreen(
    uiState: NoticeUiState,
    onNoticeClick: (NoticeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is NoticeUiState.Error -> {
            LaunchedEffect(uiState) {
                Timber.w(uiState.throwable.stackTraceToString())
            }
        }

        is NoticeUiState.InitialLoading, NoticeUiState.Loading -> {
            LottieLoadingScreen()
        }

        is NoticeUiState.Success -> {
            if (uiState.notices.isEmpty()) {
                EmptyStateScreen(modifier = modifier)
            } else {
                NoticeContent(
                    uiState = uiState,
                    onNoticeClick = onNoticeClick,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
private fun LottieLoadingScreen(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(composition = composition, progress = { progress }, modifier = modifier)
}

@Composable
private fun NoticeContent(
    modifier: Modifier,
    uiState: NoticeUiState.Success,
    onNoticeClick: (NoticeUiModel) -> Unit,
) {
    LazyColumn(modifier = modifier) {
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
    )
}

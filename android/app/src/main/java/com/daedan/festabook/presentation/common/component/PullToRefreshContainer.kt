package com.daedan.festabook.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.theme.FestabookColor

const val PULL_OFFSET_LIMIT = 180F

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    pullOffsetLimit: Float = PULL_OFFSET_LIMIT,
    content: @Composable (PullToRefreshState) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val threshold = (pullOffsetLimit / 2).dp

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        indicator = {
            PullToRefreshIndicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                pullOffsetLimit = pullOffsetLimit,
                modifier = Modifier.align(Alignment.TopCenter),
                threshold = threshold,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) {
        content(pullToRefreshState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    pullOffsetLimit: Float,
    modifier: Modifier = Modifier,
    threshold: Dp,
) {
    val indicatorSize = (pullOffsetLimit / 5).dp
    val centerOffset = -(threshold / 2 - indicatorSize / 2)

    Box(
        modifier =
            modifier.pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                threshold = threshold,
                onRefresh = onRefresh,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val distanceFraction = state.distanceFraction.coerceIn(0f, 1f)

        if (isRefreshing) {
            CircularProgressIndicator(
                color = FestabookColor.gray200,
                modifier =
                    Modifier
                        .size(indicatorSize)
                        .offset(y = -(centerOffset * 2 / 3)),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.logo_title),
                contentDescription = stringResource(R.string.logo_splash),
                modifier =
                    Modifier
                        .scale(distanceFraction)
                        .size((pullOffsetLimit / 2).dp)
                        .offset(y = centerOffset / 3),
            )
        }
    }
}

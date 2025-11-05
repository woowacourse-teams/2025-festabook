package com.daedan.festabook.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    pullOffsetLimit: Float,
    modifier: Modifier = Modifier,
    content: @Composable (PullToRefreshState) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()

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
                threshold = (pullOffsetLimit / 2).dp,
                modifier = Modifier.align(Alignment.TopCenter),
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
    threshold: Dp = PullToRefreshDefaults.PositionalThreshold,
) {
    val indicatorSize = (pullOffsetLimit / 9).dp
    val centerOffset = -(threshold / 2 - indicatorSize / 2)

    Box(
        modifier =
            modifier.pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                threshold = threshold,
                onRefresh = onRefresh,
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        val distanceFraction = { state.distanceFraction.coerceIn(0f, 0.5f) }

        if (isRefreshing) {
            CircularProgressIndicator(
                color = colorResource(R.color.gray200),
                modifier =
                    Modifier
                        .size(-centerOffset)
                        .padding(top = -centerOffset / 2),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.logo_splash),
                contentDescription = stringResource(R.string.logo_splash),
                modifier =
                    Modifier
                        .scale(distanceFraction())
                        .offset(y = centerOffset / 2),
            )
        }
    }
}

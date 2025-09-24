package com.daedan.festabook.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.daedan.festabook.R

@Composable
fun EmptyStateScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.empty_state_message),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateScreenPreview() {
    EmptyStateScreen()
}

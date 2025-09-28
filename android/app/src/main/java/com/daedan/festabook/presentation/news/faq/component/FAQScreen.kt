package com.daedan.festabook.presentation.news.faq.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.component.EmptyStateScreen
import com.daedan.festabook.presentation.news.component.NewsItem
import com.daedan.festabook.presentation.news.faq.FAQUiState
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import timber.log.Timber

@Composable
fun FAQScreen(
    uiState: FAQUiState,
    onFaqClick: (FAQItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is FAQUiState.Error -> {
            LaunchedEffect(uiState) {
                Timber.w(uiState.throwable.stackTraceToString())
            }
        }

        is FAQUiState.InitialLoading -> Unit

        is FAQUiState.Success -> {
            if (uiState.faqs.isEmpty()) {
                EmptyStateScreen()
            } else {
                LazyColumn(modifier = modifier) {
                    itemsIndexed(
                        items = uiState.faqs,
                        key = { _, faq -> faq.questionId },
                    ) { index, faq ->
                        NewsItem(
                            title = stringResource(R.string.tab_faq_question, faq.question),
                            description = faq.answer,
                            isExpanded = faq.isExpanded,
                            onclick = { onFaqClick(faq) },
                            modifier =
                                Modifier.padding(
                                    top = if (index == 0) 12.dp else 6.dp,
                                    bottom = 6.dp,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FAQScreenPreview() {
    FAQScreen(uiState = FAQUiState.Success(emptyList()), onFaqClick = {})
}

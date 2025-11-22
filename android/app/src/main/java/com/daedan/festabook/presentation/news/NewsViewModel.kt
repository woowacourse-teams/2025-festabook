package com.daedan.festabook.presentation.news

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daedan.festabook.di.viewmodel.ViewModelKey
import com.daedan.festabook.domain.model.Lost
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.LostItemRepository
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.presentation.news.faq.FAQUiState
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.faq.model.toUiModel
import com.daedan.festabook.presentation.news.lost.LostUiState
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.news.lost.model.toLostGuideItemUiModel
import com.daedan.festabook.presentation.news.lost.model.toLostItemUiModel
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.NoticeUiState.Companion.DEFAULT_POSITION
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(NewsViewModel::class)
@Inject
class NewsViewModel(
    private val noticeRepository: NoticeRepository,
    private val faqRepository: FAQRepository,
    private val lostItemRepository: LostItemRepository,
) : ViewModel() {
    var noticeUiState by mutableStateOf<NoticeUiState>(NoticeUiState.InitialLoading)
        private set

    val isNoticeScreenRefreshing by derivedStateOf {
        noticeUiState is NoticeUiState.Refreshing
    }

    var faqUiState by mutableStateOf<FAQUiState>(FAQUiState.InitialLoading)
        private set

    var lostUiState by mutableStateOf<LostUiState>(LostUiState.InitialLoading)
        private set

    val isLostItemScreenRefreshing by derivedStateOf {
        lostUiState is LostUiState.Refreshing
    }

    private var noticeIdToExpand: Long? = null

    init {
        loadAllNotices(NoticeUiState.InitialLoading)
        loadAllFAQs()
        loadAllLostItems(LostUiState.InitialLoading)
    }

    fun loadAllNotices(state: NoticeUiState) {
        viewModelScope.launch {
            noticeUiState = state
            val result = noticeRepository.fetchNotices()
            result
                .onSuccess { notices ->
                    val updatedNotices =
                        notices.map {
                            it.toUiModel().let { notice ->
                                if (notice.id == noticeIdToExpand) notice.copy(isExpanded = true) else notice
                            }
                        }
                    val expandPosition =
                        notices.indexOfFirst { it.id == noticeIdToExpand }.let {
                            if (it == -1) DEFAULT_POSITION else it
                        }
                    noticeUiState =
                        NoticeUiState.Success(updatedNotices, expandPosition)
                    noticeIdToExpand = null
                }.onFailure {
                    noticeUiState = NoticeUiState.Error(it)
                }
        }
    }

    fun toggleNoticeExpanded(notice: NoticeUiModel) {
        updateNoticeUiState { notices ->
            notices.map { updatedNotice ->
                if (notice.id == updatedNotice.id) {
                    updatedNotice.copy(isExpanded = !updatedNotice.isExpanded)
                } else {
                    updatedNotice
                }
            }
        }
    }

    fun expandNotice(noticeId: Long) {
        this.noticeIdToExpand = noticeId
        val notices =
            when (val currentState = noticeUiState) {
                is NoticeUiState.Refreshing -> currentState.oldNotices
                is NoticeUiState.Success -> currentState.notices
                else -> return
            }

        loadAllNotices(NoticeUiState.Refreshing(notices))
    }

    fun toggleFAQExpanded(faqItem: FAQItemUiModel) {
        updateFAQUiState { faqItems ->
            faqItems.map { updatedFAQItem ->
                if (faqItem.questionId == updatedFAQItem.questionId) {
                    updatedFAQItem.copy(isExpanded = !updatedFAQItem.isExpanded)
                } else {
                    updatedFAQItem
                }
            }
        }
    }

    fun toggleLostGuideExpanded() {
        updateLostUiState { lostUiModels ->
            lostUiModels.map { lostUiModel ->
                if (lostUiModel is LostUiModel.Guide) {
                    lostUiModel.copy(isExpanded = !lostUiModel.isExpanded)
                } else {
                    lostUiModel
                }
            }
        }
    }

    fun loadAllLostItems(state: LostUiState) {
        viewModelScope.launch {
            lostUiState = state
            val result = lostItemRepository.getLost()

            val lostUiModels =
                result.map { lost ->
                    when (lost) {
                        is Lost.Guide -> lost.toLostGuideItemUiModel()
                        is Lost.Item -> lost.toLostItemUiModel()
                        null -> LostUiModel.Guide()
                    }
                }
            lostUiState = LostUiState.Success(lostUiModels)
        }
    }

    private fun loadAllFAQs(state: FAQUiState = FAQUiState.InitialLoading) {
        viewModelScope.launch {
            faqUiState = state

            val result = faqRepository.getAllFAQ()

            result
                .onSuccess { faqItems ->
                    faqUiState = FAQUiState.Success(faqItems.map { it.toUiModel() })
                }.onFailure {
                    faqUiState = FAQUiState.Error(it)
                }
        }
    }

    private fun updateNoticeUiState(onUpdate: (List<NoticeUiModel>) -> List<NoticeUiModel>) {
        noticeUiState =
            when (val currentState = noticeUiState) {
                is NoticeUiState.Success ->
                    currentState.copy(
                        notices = onUpdate(currentState.notices),
                    )

                else -> currentState
            }
    }

    private fun updateFAQUiState(onUpdate: (List<FAQItemUiModel>) -> List<FAQItemUiModel>) {
        val currentState = faqUiState
        faqUiState =
            when (currentState) {
                is FAQUiState.Success -> currentState.copy(faqs = onUpdate(currentState.faqs))
                else -> currentState
            }
    }

    private fun updateLostUiState(onUpdate: (List<LostUiModel>) -> List<LostUiModel>) {
        val currentState = lostUiState
        lostUiState =
            when (currentState) {
                is LostUiState.Success -> currentState.copy(lostItems = onUpdate(currentState.lostItems))
                else -> currentState
            }
    }
}

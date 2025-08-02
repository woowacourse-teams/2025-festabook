package com.daedan.festabook.presentation.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.presentation.news.faq.FAQUiState
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.faq.model.toUiModel
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
import kotlinx.coroutines.launch

class NewsViewModel(
    private val noticeRepository: NoticeRepository,
    private val faqRepository: FAQRepository,
) : ViewModel() {
    private val _noticeUiState: MutableLiveData<NoticeUiState> = MutableLiveData<NoticeUiState>()
    val noticeUiState: LiveData<NoticeUiState> = _noticeUiState

    private val _faqUiState: MutableLiveData<FAQUiState> = MutableLiveData()
    val faqUiState: LiveData<FAQUiState> get() = _faqUiState

    init {
        loadAllNotices(NoticeUiState.InitialLoading)
        loadAllFAQs()
    }

    fun loadAllNotices(state: NoticeUiState = NoticeUiState.Loading) {
        viewModelScope.launch {
            _noticeUiState.value = state

            val result = noticeRepository.fetchNotices()
            result
                .onSuccess { notices ->
                    _noticeUiState.value = NoticeUiState.Success(notices.map { it.toUiModel() })
                }.onFailure {
                    _noticeUiState.value = NoticeUiState.Error(it)
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

    private fun loadAllFAQs(state: FAQUiState = FAQUiState.InitialLoading) {
        viewModelScope.launch {
            _faqUiState.value = state

            val result = faqRepository.getAllFAQ()
            result
                .onSuccess { fAQItems ->
                    _faqUiState.value = FAQUiState.Success(fAQItems.map { it.toUiModel() })
                }.onFailure {
                    _faqUiState.value = FAQUiState.Error(it)
                }
        }
    }

    private fun updateNoticeUiState(onUpdate: (List<NoticeUiModel>) -> List<NoticeUiModel>) {
        val currentState = _noticeUiState.value ?: return
        _noticeUiState.value =
            when (currentState) {
                is NoticeUiState.Success -> currentState.copy(notices = onUpdate(currentState.notices))
                else -> currentState
            }
    }

    private fun updateFAQUiState(onUpdate: (List<FAQItemUiModel>) -> List<FAQItemUiModel>) {
        val currentState = _faqUiState.value ?: return
        _faqUiState.value =
            when (currentState) {
                is FAQUiState.Success -> currentState.copy(faqs = onUpdate(currentState.faqs))
                else -> currentState
            }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val festaBookApp = this[APPLICATION_KEY] as FestaBookApp
                    val noticeRepository =
                        festaBookApp.appContainer.noticeRepository
                    val faqRepository = festaBookApp.appContainer.faqRepository
                    NewsViewModel(noticeRepository, faqRepository)
                }
            }
    }
}

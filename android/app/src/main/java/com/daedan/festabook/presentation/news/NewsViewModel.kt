package com.daedan.festabook.presentation.news

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.model.Lost
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.LostItemRepository
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.presentation.common.Event
import com.daedan.festabook.presentation.news.faq.FAQUiState
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.faq.model.toUiModel
import com.daedan.festabook.presentation.news.lost.LostUiState
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.news.lost.model.toLostGuideItemUiModel
import com.daedan.festabook.presentation.news.lost.model.toLostItemUiModel
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
import kotlinx.coroutines.launch

class NewsViewModel(
    private val noticeRepository: NoticeRepository,
    private val faqRepository: FAQRepository,
    private val lostItemRepository: LostItemRepository,
) : ViewModel() {
    private val _noticeUiState: MutableLiveData<NoticeUiState> = MutableLiveData<NoticeUiState>()
    val noticeUiState: LiveData<NoticeUiState> = _noticeUiState

    var faqUiState by mutableStateOf<FAQUiState>(FAQUiState.InitialLoading)
        private set

    private val _lostUiState: MutableLiveData<LostUiState> = MutableLiveData()
    val lostUiState: LiveData<LostUiState> get() = _lostUiState

    private val _lostItemClickEvent: MutableLiveData<Event<LostUiModel.Item>> = MutableLiveData()
    val lostItemClickEvent: LiveData<Event<LostUiModel.Item>> get() = _lostItemClickEvent

    private var noticeIdToExpand: Long? = null

    init {
        loadAllNotices(NoticeUiState.InitialLoading)
        loadAllFAQs()
        loadAllLostItems()
    }

    fun loadAllNotices(state: NoticeUiState = NoticeUiState.Loading) {
        viewModelScope.launch {
            _noticeUiState.value = state
            val result = noticeRepository.fetchNotices()
            result
                .onSuccess { notices ->
                    val updatedNotices =
                        notices.map {
                            it.toUiModel().let { notice ->
                                if (notice.id == noticeIdToExpand) notice.copy(isExpanded = true) else notice
                            }
                        }
                    val noticeIdToExpandPosition =
                        notices.indexOfFirst { it.id == noticeIdToExpand }
                    _noticeUiState.value =
                        NoticeUiState.Success(updatedNotices, noticeIdToExpandPosition)
                    noticeIdToExpand = null
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

    fun expandNotice(noticeId: Long) {
        this.noticeIdToExpand = noticeId
        if (noticeUiState.value == NoticeUiState.InitialLoading) return
        loadAllNotices()
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

    fun lostItemClick(lostItem: LostUiModel.Item) {
        _lostItemClickEvent.value = Event(lostItem)
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

    fun loadAllLostItems(state: LostUiState = LostUiState.InitialLoading) {
        viewModelScope.launch {
            _lostUiState.value = state
            val result = lostItemRepository.getLost()

            val lostUiModels =
                result.map { lost ->
                    when (lost) {
                        is Lost.Guide -> lost.toLostGuideItemUiModel()
                        is Lost.Item -> lost.toLostItemUiModel()
                        null -> LostUiModel.Guide()
                    }
                }
            _lostUiState.value = LostUiState.Success(lostUiModels)
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
        val currentState = _noticeUiState.value ?: return
        _noticeUiState.value =
            when (currentState) {
                is NoticeUiState.Success ->
                    currentState.copy(
                        notices = onUpdate(currentState.notices),
                        noticeIdToExpandPosition = -1,
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
        val currentState = _lostUiState.value ?: return
        _lostUiState.value =
            when (currentState) {
                is LostUiState.Success -> currentState.copy(lostItems = onUpdate(currentState.lostItems))
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
                    val lostItemRepository = festaBookApp.appContainer.lostItemRepository
                    NewsViewModel(noticeRepository, faqRepository, lostItemRepository)
                }
            }
    }
}

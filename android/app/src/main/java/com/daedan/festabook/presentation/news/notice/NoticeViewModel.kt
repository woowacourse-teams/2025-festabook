package com.daedan.festabook.presentation.news.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
import kotlinx.coroutines.launch

class NoticeViewModel(
    private val noticeRepository: NoticeRepository,
) : ViewModel() {
    private val _noticeUiState: MutableLiveData<NoticeUiState> = MutableLiveData<NoticeUiState>()
    val noticeUiState: LiveData<NoticeUiState> = _noticeUiState

    init {
        fetchNotices()
    }

    fun fetchNotices() {
        viewModelScope.launch {
            _noticeUiState.value = NoticeUiState.Loading

            val result = noticeRepository.fetchNotices()
            result
                .onSuccess { notices ->
                    _noticeUiState.value = NoticeUiState.Success(notices.map { it.toUiModel() })
                }.onFailure {
                    _noticeUiState.value = NoticeUiState.Error(it.message.toString())
                }
        }
    }

    fun toggleNoticeExpanded(noticeId: Long) {
        updateNoticeUiState { notices ->
            notices.map { notice ->
                if (notice.id == noticeId) {
                    notice.copy(isExpanded = !notice.isExpanded)
                } else {
                    notice
                }
            }
        }
    }

    private fun updateNoticeUiState(onUpdate: (List<NoticeUiModel>) -> List<NoticeUiModel>) {
        val currentState = _noticeUiState.value ?: return
        _noticeUiState.value =
            when (currentState) {
                is NoticeUiState.Success -> currentState.copy(notices = onUpdate(currentState.notices))
                is NoticeUiState.Error, NoticeUiState.Loading -> currentState
            }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val noticeRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.noticeRepository
                    NoticeViewModel(noticeRepository)
                }
            }
    }
}

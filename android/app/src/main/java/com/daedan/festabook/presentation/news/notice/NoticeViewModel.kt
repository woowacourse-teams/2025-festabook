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
    private val _notices = MutableLiveData<List<NoticeUiModel>>()
    val notices: LiveData<List<NoticeUiModel>> = _notices

    fun fetchNotices() {
        viewModelScope.launch {
            val result = noticeRepository.fetchNotices()

            result
                .onSuccess { notices ->
                    _notices.value = notices.map { it.toUiModel() }
                }.onFailure {
                }
        }
    }

    fun toggleNoticeExpanded(noticeId: Long) {
        val currentList = _notices.value ?: return
        val updatedList =
            currentList.map { notice ->
                if (notice.id == noticeId) {
                    notice.copy(isExpanded = !notice.isExpanded)
                } else {
                    notice
                }
            }
        _notices.value = updatedList
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

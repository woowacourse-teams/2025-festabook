package com.daedan.festabook.presentation.news.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
            noticeRepository
                .fetchNotices()
                .onSuccess { notices ->
                    _notices.value = notices.map { it.toUiModel() }
                }.onFailure {
                }
        }
    }

    fun toggleNoticeExpanded(noticeId: Long) {
        _notices.value?.map { notice ->
            if (notice.id == noticeId) {
                notice.copy(isExpanded = !notice.isExpanded)
            } else {
                notice
            }
        }
    }

    companion object {
        fun factory(noticeRepository: NoticeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = NoticeViewModel(noticeRepository) as T
            }
    }
}

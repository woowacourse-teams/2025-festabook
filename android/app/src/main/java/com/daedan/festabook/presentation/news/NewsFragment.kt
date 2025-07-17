package com.daedan.festabook.presentation.news.notice

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class NewsFragment : BaseFragment<FragmentNewsBinding>(R.layout.fragment_news) {
    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter { noticeId ->
            val newList =
                noticeAdapter.currentList.map { updateNotice ->
                    if (updateNotice.id == noticeId) updateNotice.copy(isExpanded = !updateNotice.isExpanded) else updateNotice
                }
            noticeAdapter.submitList(newList)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNoticeList.adapter = noticeAdapter
        val notices =
            listOf(
                NoticeUiModel(1L, "제목1", "설명1", "2025-07-14T05:22:39.963Z"),
                NoticeUiModel(2L, "제목2", "설명2", "2025-07-13T11:11:39.963Z"),
                NoticeUiModel(
                    3L,
                    "제목3",
                    "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다.",
                    "2025-07-13T11:11:39.963Z",
                ),
                NoticeUiModel(
                    4L,
                    "제목4",
                    "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다." +
                        "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다." +
                        "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다." +
                        "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다." +
                        "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다.",
                    "2025-07-13T11:11:39.963Z",
                ),
                NoticeUiModel(
                    5L,
                    "제목5",
                    "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다.",
                    "2025-07-13T11:11:39.963Z",
                ),
                NoticeUiModel(
                    6L,
                    "제목6",
                    "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다.",
                    "2025-07-13T11:11:39.963Z",
                ),
                NoticeUiModel(
                    7L,
                    "제목7",
                    "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다.",
                    "2025-07-13T11:11:39.963Z",
                ),
                NoticeUiModel(
                    8L,
                    "제목8",
                    "엄청 긴 설명입니다. 엄청 긴 ~~~~~~~ 설명입니다. 엄청 긴 설명입니ㅏㄷ. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다. 엄청 긴 설명입니다.",
                    "2025-07-13T11:11:39.963Z",
                ),
            )
        noticeAdapter.submitList(notices)
    }
}

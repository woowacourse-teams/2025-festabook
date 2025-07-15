package com.daedan.festabook.presentation.news.notice

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter

class NewsFragment : BaseFragment<FragmentNewsBinding>(R.layout.fragment_news) {
    private val noticeAdapter: NoticeAdapter by lazy { NoticeAdapter() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNoticeList.adapter = noticeAdapter
        val notices =
            listOf(
                NoticeUiModel("제목1", "설명1", "2025-07-14T05:22:39.963Z"),
                NoticeUiModel("제목2", "설명2", "2025-07-13T11:11:39.963Z"),
            )
        noticeAdapter.submitList(notices)
    }
}

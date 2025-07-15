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
    }
}

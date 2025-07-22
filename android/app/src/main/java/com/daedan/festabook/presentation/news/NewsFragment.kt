package com.daedan.festabook.presentation.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.notice.NoticeViewModel
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class NewsFragment : BaseFragment<FragmentNewsBinding>(R.layout.fragment_news) {
    private val viewModel: NoticeViewModel by viewModels { NoticeViewModel.Factory }

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter { noticeId ->
            viewModel.toggleNoticeExpanded(noticeId)
        }
    }

    override fun onStart() {
        super.onStart()
        fetchNotices()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvNoticeList.adapter = noticeAdapter

        setupObserver()
    }

    private fun fetchNotices() {
        viewModel.fetchNotices()
    }

    private fun setupObserver() {
        viewModel.notices.observe(viewLifecycleOwner) { notices ->
            noticeAdapter.submitList(notices)
        }
    }
}

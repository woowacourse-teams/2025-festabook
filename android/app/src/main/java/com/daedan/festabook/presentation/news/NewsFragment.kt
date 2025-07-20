package com.daedan.festabook.presentation.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.notice.NoticeViewModel
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter

class NewsFragment : BaseFragment<FragmentNewsBinding>(R.layout.fragment_news) {
    private val viewModel: NoticeViewModel by viewModels {
        NoticeViewModel.factory(
            noticeRepository = (requireActivity().application as FestaBookApp).appContainer.noticeRepository,
        )
    }

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter { noticeId ->
            viewModel.toggleNoticeExpanded(noticeId)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchNotices()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvNoticeList.adapter = noticeAdapter

        initObserver()
    }

    private fun fetchNotices() {
        viewModel.fetchNotices()
    }

    private fun initObserver() {
        viewModel.notices.observe(viewLifecycleOwner) { notices ->
            noticeAdapter.submitList(notices)
        }
    }
}

package com.daedan.festabook.presentation.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.NoticeViewModel
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter

class NewsFragment : BaseFragment<FragmentNewsBinding>(R.layout.fragment_news) {
    private val viewModel: NoticeViewModel by viewModels { NoticeViewModel.Factory }

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter { notice ->
            viewModel.toggleNoticeExpanded(notice)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvNoticeList.adapter = noticeAdapter

        setupObserver()
        onSwipeRefreshNoticesListener()
    }

    private fun onSwipeRefreshNoticesListener() {
        binding.srlNoticeList.setOnRefreshListener {
            viewModel.fetchNotices()
        }
    }

    private fun setupObserver() {
        viewModel.noticeUiState.observe(viewLifecycleOwner) { noticeState ->
            when (noticeState) {
                is NoticeUiState.Error -> {
                    binding.srlNoticeList.isRefreshing = false
                }

                is NoticeUiState.Loading -> {
                    binding.srlNoticeList.isRefreshing = true
                }

                is NoticeUiState.Success -> {
                    noticeAdapter.submitList(noticeState.notices)
                    binding.srlNoticeList.isRefreshing = false
                }
            }
        }
    }
}

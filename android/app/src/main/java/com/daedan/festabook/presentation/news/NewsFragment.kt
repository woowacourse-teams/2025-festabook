package com.daedan.festabook.presentation.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
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
                is NoticeUiState.InitialLoading -> {
                    binding.srlNoticeList.isRefreshing = true
                    showSkeleton()
                }

                is NoticeUiState.Error -> {
                    hideSkeleton()
                    showErrorSnackBar(noticeState.throwable)
                    binding.srlNoticeList.isRefreshing = false
                }

                is NoticeUiState.Loading -> {
                    binding.srlNoticeList.isRefreshing = true
                }

                is NoticeUiState.Success -> {
                    hideSkeleton()
                    noticeAdapter.submitList(noticeState.notices)
                    binding.srlNoticeList.isRefreshing = false
                }
            }
        }
    }

    private fun showSkeleton() {
        binding.sflScheduleSkeleton.visibility = View.VISIBLE
        binding.rvNoticeList.visibility = View.INVISIBLE
        binding.sflScheduleSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.sflScheduleSkeleton.visibility = View.GONE
        binding.rvNoticeList.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.stopShimmer()
    }
}

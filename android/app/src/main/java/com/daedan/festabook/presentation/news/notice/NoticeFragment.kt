package com.daedan.festabook.presentation.news.notice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNoticeBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter

class NoticeFragment : BaseFragment<FragmentNoticeBinding>(R.layout.fragment_notice) {
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
                    binding.srlNoticeList.isRefreshing = false
                    showSkeleton()
                }

                is NoticeUiState.Error -> {
                    showErrorSnackBar(noticeState.throwable)
                    binding.srlNoticeList.isRefreshing = false
                    hideSkeleton()
                }

                is NoticeUiState.Loading -> {
                    binding.srlNoticeList.isRefreshing = true
                    showSkeleton()
                }

                is NoticeUiState.Success -> {
                    noticeAdapter.submitList(noticeState.notices)
                    binding.srlNoticeList.isRefreshing = false
                    hideSkeleton()
                }
            }
        }
    }

    private fun showSkeleton() {
        binding.sflNoticeSkeleton.visibility = View.VISIBLE
        binding.sflNoticeSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.sflNoticeSkeleton.visibility = View.GONE
        binding.sflNoticeSkeleton.stopShimmer()
    }

    companion object {
        fun newInstance() = NoticeFragment()
    }
}

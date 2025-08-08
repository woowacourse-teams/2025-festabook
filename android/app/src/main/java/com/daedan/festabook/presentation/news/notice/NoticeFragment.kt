package com.daedan.festabook.presentation.news.notice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNoticeBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import timber.log.Timber

class NoticeFragment : BaseFragment<FragmentNoticeBinding>(R.layout.fragment_notice) {
    private val viewModel: NewsViewModel by viewModels({ requireParentFragment() }) { NewsViewModel.Factory }

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter(requireParentFragment() as OnNewsClickListener)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvNoticeList.adapter = noticeAdapter
        (binding.rvNoticeList.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        setupObserver()
        onSwipeRefreshNoticesListener()
    }

    private fun onSwipeRefreshNoticesListener() {
        binding.srlNoticeList.setOnRefreshListener {
            viewModel.loadAllNotices()
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
                    Timber.w(noticeState.throwable, "NoticeFragment: ${noticeState.throwable.message}")
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
        binding.srlNoticeList.visibility = View.INVISIBLE
        binding.sflNoticeSkeleton.visibility = View.VISIBLE
        binding.sflNoticeSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.srlNoticeList.visibility = View.VISIBLE
        binding.sflNoticeSkeleton.visibility = View.GONE
        binding.sflNoticeSkeleton.stopShimmer()
    }

    companion object {
        fun newInstance() = NoticeFragment()
    }
}

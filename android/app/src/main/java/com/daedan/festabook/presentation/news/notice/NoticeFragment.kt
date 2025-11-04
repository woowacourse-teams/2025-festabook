package com.daedan.festabook.presentation.news.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNoticeBinding
import com.daedan.festabook.di.appGraph
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.main.MainViewModel
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import com.daedan.festabook.presentation.news.notice.component.NoticeScreen

class NoticeFragment : BaseFragment<FragmentNoticeBinding>() {
    override val layoutId: Int = R.layout.fragment_notice

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = appGraph.metroViewModelFactory
    private val newsViewModel: NewsViewModel by viewModels({ requireParentFragment() })
    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter(requireParentFragment() as OnNewsClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                NoticeScreen(
                    uiState = newsViewModel.noticeUiState,
                    onNoticeClick = { notice ->
                        (requireParentFragment() as OnNewsClickListener)
                            .onNoticeClick(notice)
                    },
                )
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

//        binding.lifecycleOwner = viewLifecycleOwner
//        binding.rvNoticeList.adapter = noticeAdapter
//        (binding.rvNoticeList.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
//
        setupObserver()
//        onSwipeRefreshNoticesListener()
    }

//    private fun onSwipeRefreshNoticesListener() {
//        binding.srlNoticeList.setOnRefreshListener {
//            newsViewModel.loadAllNotices()
//        }
//    }

    private fun setupObserver() {
//        newsViewModel.noticeUiState.observe(viewLifecycleOwner) { noticeState ->
//            when (noticeState) {
//                is NoticeUiState.InitialLoading -> {
//                    binding.srlNoticeList.isRefreshing = false
//                    showSkeleton()
//                }
//
//                is NoticeUiState.Error -> {
//                    showErrorSnackBar(noticeState.throwable)
//                    Timber.w(
//                        noticeState.throwable,
//                        "${this::class.simpleName}: ${noticeState.throwable.message}",
//                    )
//                    binding.srlNoticeList.isRefreshing = false
//                    hideSkeleton()
//                }
//
//                is NoticeUiState.Loading -> {
//                    binding.srlNoticeList.isRefreshing = true
//                    showSkeleton()
//                }
//
//                is NoticeUiState.Success -> {
//                    noticeAdapter.submitList(noticeState.notices) {
//                        showEmptyStateMessage()
//                        scrollExpandedNoticeToTop(noticeState)
//                    }
//                    binding.srlNoticeList.isRefreshing = false
//                    hideSkeleton()
//                }
//            }
//        }
        mainViewModel.noticeIdToExpand.observe(viewLifecycleOwner) { noticeId ->
            newsViewModel.expandNotice(noticeId)
        }
    }
//
//    private fun scrollExpandedNoticeToTop(noticeState: NoticeUiState.Success) {
//        if (noticeState.noticeIdToExpandPosition == -1) return
//        val layoutManager =
//            binding.rvNoticeList.layoutManager as LinearLayoutManager
//        layoutManager.scrollToPositionWithOffset(
//            noticeState.noticeIdToExpandPosition,
//            0,
//        )
//    }
//
//    private fun showSkeleton() {
//        binding.srlNoticeList.visibility = View.INVISIBLE
//        binding.sflNoticeSkeleton.visibility = View.VISIBLE
//        binding.sflNoticeSkeleton.startShimmer()
//    }
//
//    private fun hideSkeleton() {
//        binding.srlNoticeList.visibility = View.VISIBLE
//        binding.sflNoticeSkeleton.visibility = View.GONE
//        binding.sflNoticeSkeleton.stopShimmer()
//    }
//
//    private fun showEmptyStateMessage() {
//        val itemCount = binding.rvNoticeList.adapter?.itemCount ?: 0
//
//        binding.tvEmptyState.root.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
//    }

    companion object {
        fun newInstance() = NoticeFragment()
    }
}

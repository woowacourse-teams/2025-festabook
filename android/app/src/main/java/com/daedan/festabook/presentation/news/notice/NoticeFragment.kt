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
                    onRefresh = {
                        newsViewModel.loadAllNotices()
                    },
                )
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        mainViewModel.noticeIdToExpand.observe(viewLifecycleOwner) { noticeId ->
            newsViewModel.expandNotice(noticeId)
        }
    }

    companion object {
        fun newInstance() = NoticeFragment()
    }
}

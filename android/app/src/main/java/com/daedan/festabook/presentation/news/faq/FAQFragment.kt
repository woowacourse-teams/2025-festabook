package com.daedan.festabook.presentation.news.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentFaqBinding
import com.daedan.festabook.di.metroViewModels
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.faq.component.FAQScreen
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class FAQFragment : BaseFragment<FragmentFaqBinding>(R.layout.fragment_faq) {
    private val viewModel: NewsViewModel by metroViewModels { requireParentFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FAQScreen(uiState = viewModel.faqUiState, onFaqClick = { faqItemUiModel ->
                    (requireParentFragment() as OnNewsClickListener).onFAQClick(faqItemUiModel)
                })
            }
        }

    companion object {
        fun newInstance() = FAQFragment()
    }
}

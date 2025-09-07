package com.daedan.festabook.presentation.news.faq

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentFaqBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.faq.adapter.FAQAdapter
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import timber.log.Timber

class FAQFragment : BaseFragment<FragmentFaqBinding>(R.layout.fragment_faq) {
    private val viewModel: NewsViewModel by viewModels({ requireParentFragment() }) { NewsViewModel.Factory }
    private val adapter by lazy {
        FAQAdapter(requireParentFragment() as OnNewsClickListener)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFaq.adapter = adapter
        (binding.rvFaq.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.faqUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                FAQUiState.InitialLoading -> {}
                FAQUiState.Refreshing -> {}
                is FAQUiState.Success -> {
                    adapter.submitList(state.faqs)
                }

                is FAQUiState.Error -> {
                    Timber.w(state.throwable.stackTraceToString())
                    showErrorSnackBar(state.throwable)
                }
            }
        }
    }

    companion object {
        fun newInstance() = FAQFragment()
    }
}

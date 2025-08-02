package com.daedan.festabook.presentation.news.faq

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentFAQBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.faq.adapter.FAQAdapter

class FAQFragment : BaseFragment<FragmentFAQBinding>(R.layout.fragment_f_a_q) {
    private val viewModel: NewsViewModel by viewModels({ requireParentFragment() }) { NewsViewModel.Factory }
    private val adapter by lazy { FAQAdapter() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFaq.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
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
                    showErrorSnackBar(state.throwable)
                }
            }
        }
    }

    companion object {
        fun newInstance() = FAQFragment()
    }
}

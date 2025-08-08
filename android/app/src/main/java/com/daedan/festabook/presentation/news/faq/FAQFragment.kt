package com.daedan.festabook.presentation.news.faq

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentFAQBinding
import com.daedan.festabook.databinding.ItemFaqBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import timber.log.Timber

class FAQFragment : BaseFragment<FragmentFAQBinding>(R.layout.fragment_f_a_q) {
    private val viewModel: NewsViewModel by viewModels({ requireParentFragment() }) { NewsViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.faqUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                FAQUiState.InitialLoading -> {}
                FAQUiState.Refreshing -> {}
                is FAQUiState.Success -> {
                    setupScrollview(state.faqs)
                }

                is FAQUiState.Error -> {
                    Timber.w(state.throwable.stackTraceToString())
                    showErrorSnackBar(state.throwable)
                }
            }
        }
    }

    private fun setupScrollview(items: List<FAQItemUiModel>) {
        binding.llFaqContainer.removeAllViews()

        items.forEachIndexed { index, item ->
            val faqItemBinding =
                ItemFaqBinding.inflate(layoutInflater, binding.llFaqContainer, false)

            setupFAQItemTopMargin(faqItemBinding, index)
            setupFAQItemView(faqItemBinding, item)

            binding.llFaqContainer.addView(faqItemBinding.root)
        }
    }

    private fun setupFAQItemView(
        faqItemBinding: ItemFaqBinding,
        item: FAQItemUiModel,
    ) {
        faqItemBinding.tvFaqQuestion.text =
            getString(R.string.tab_faq_question, item.question)
        faqItemBinding.tvFaqAnswer.text = item.answer
        faqItemBinding.ivFaqExpand.setImageResource(
            if (item.isExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down,
        )
        faqItemBinding.tvFaqAnswer.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

        faqItemBinding.clFaqItem.setOnClickListener {
            (requireParentFragment() as OnNewsClickListener).onFAQClick(item)
        }
    }

    private fun setupFAQItemTopMargin(
        faqItemBinding: ItemFaqBinding,
        index: Int,
    ) {
        val layoutParams = faqItemBinding.root.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = if (index == 0) TOP_MARGIN.toPx(requireContext()) else 0
        faqItemBinding.root.layoutParams = layoutParams
    }

    companion object {
        private const val TOP_MARGIN = 12

        fun newInstance() = FAQFragment()
    }
}

package com.daedan.festabook.presentation.news.faq

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentFAQBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.faq.adapter.FAQAdapter

class FAQFragment : BaseFragment<FragmentFAQBinding>(R.layout.fragment_f_a_q) {
    private val adapter by lazy { FAQAdapter() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFaq.adapter = adapter
    }

    companion object {
        fun newInstance() = FAQFragment()
    }
}

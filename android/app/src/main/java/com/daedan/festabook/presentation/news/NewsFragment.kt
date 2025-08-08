package com.daedan.festabook.presentation.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.adapter.NewsPagerAdapter
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment :
    BaseFragment<FragmentNewsBinding>(R.layout.fragment_news),
    OnNewsClickListener {
    private val newsPagerAdapter by lazy {
        NewsPagerAdapter(this)
    }

    private val viewModel: NewsViewModel by viewModels { NewsViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setupNewsTabLayout()
    }

    override fun onNoticeClick(notice: NoticeUiModel) {
        viewModel.toggleNoticeExpanded(notice)
    }

    override fun onFAQClick(faqItem: FAQItemUiModel) {
        viewModel.toggleFAQExpanded(faqItem)
    }

    override fun onLostItemClick(lostItem: LostItemUiModel) {
        viewModel.lostItemClick(lostItem)
    }

    private fun setupNewsTabLayout() {
        binding.vpNews.adapter = newsPagerAdapter
        TabLayoutMediator(binding.tlNews, binding.vpNews) { tab, position ->
            val tabNameRes = NewsTab.entries[position].tabNameRes
            tab.text = getString(tabNameRes)
        }.attach()
    }
}

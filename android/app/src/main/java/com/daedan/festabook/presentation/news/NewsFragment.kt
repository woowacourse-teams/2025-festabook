package com.daedan.festabook.presentation.news

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentNewsBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.adapter.NewsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment : BaseFragment<FragmentNewsBinding>(R.layout.fragment_news) {
    private val newsPagerAdapter by lazy {
        NewsPagerAdapter(this)
    }

    private val tabTitle = listOf("공지", "Q&A", "분실물")

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vpNews.adapter = newsPagerAdapter

        TabLayoutMediator(binding.tlNews, binding.vpNews) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
        hideSkeleton()
    }

    private fun showSkeleton() {
        binding.sflScheduleSkeleton.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.sflScheduleSkeleton.visibility = View.GONE
        binding.sflScheduleSkeleton.stopShimmer()
    }
}

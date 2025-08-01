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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setupNewsTabLayout()
    }

    private fun setupNewsTabLayout() {
        binding.vpNews.adapter = newsPagerAdapter
        TabLayoutMediator(binding.tlNews, binding.vpNews) { tab, position ->
            val tabNameRes = NewsTab.entries[position].tabNameRes
            tab.text = getString(tabNameRes)
        }.attach()
    }
}

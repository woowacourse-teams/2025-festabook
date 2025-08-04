package com.daedan.festabook.presentation.news.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daedan.festabook.presentation.news.NewsTab
import com.daedan.festabook.presentation.news.faq.FAQFragment
import com.daedan.festabook.presentation.news.lost.LostItemFragment
import com.daedan.festabook.presentation.news.notice.NoticeFragment

class NewsPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = NewsTab.entries.size

    override fun createFragment(position: Int): Fragment =
        when (NewsTab.entries[position]) {
            NewsTab.NOTICE -> NoticeFragment.newInstance()
            NewsTab.FAQ -> FAQFragment.newInstance()
            NewsTab.LOST_ITEM -> LostItemFragment.newInstance()
        }
}

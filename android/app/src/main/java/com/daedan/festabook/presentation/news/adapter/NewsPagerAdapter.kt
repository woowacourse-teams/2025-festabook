package com.daedan.festabook.presentation.news.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daedan.festabook.presentation.news.faq.FAGFragment
import com.daedan.festabook.presentation.news.lost.LostItemFragment
import com.daedan.festabook.presentation.news.notice.NoticeFragment

class NewsPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> NoticeFragment.newInstance()
            1 -> FAGFragment.newInstance()
            2 -> LostItemFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
}

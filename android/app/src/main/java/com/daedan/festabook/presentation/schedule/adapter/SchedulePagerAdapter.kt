package com.daedan.festabook.presentation.schedule.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daedan.festabook.presentation.schedule.ScheduleTabPageFragment

class SchedulePagerAdapter(
    fragment: Fragment,
    private val items: List<String>,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        val title: String = items[position]
        return ScheduleTabPageFragment.newInstance(title)
    }
}

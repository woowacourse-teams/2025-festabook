package com.daedan.festabook.presentation.schedule.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daedan.festabook.presentation.schedule.ScheduleTabPageFragment

class SchedulePagerAdapter(
    fragment: Fragment,
    private val items: MutableList<Long> = mutableListOf(),
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        val dateId: Long = items[position]
        return ScheduleTabPageFragment.newInstance(dateId)
    }

    fun submitList(newIds: List<Long>) {
        items.clear()
        items.addAll(newIds)
        val lastIndex: Int = newIds.size - 1
        notifyItemRangeChanged(FIRST_INDEX, lastIndex)
    }

    companion object {
        private const val FIRST_INDEX: Int = 0
    }
}

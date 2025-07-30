package com.daedan.festabook.presentation.schedule.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daedan.festabook.presentation.schedule.ScheduleTabPageFragment
import com.daedan.festabook.presentation.schedule.ScheduleViewModel.Companion.FIRST_INDEX
import com.daedan.festabook.presentation.schedule.model.ScheduleDateUiModel

class SchedulePagerAdapter(
    fragment: Fragment,
    private val items: MutableList<ScheduleDateUiModel> = mutableListOf(),
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        val dateId: Long = items[position].id
        return ScheduleTabPageFragment.newInstance(dateId)
    }

    fun submitList(newItems: List<ScheduleDateUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyItemRangeChanged(FIRST_INDEX, itemCount)
    }
}

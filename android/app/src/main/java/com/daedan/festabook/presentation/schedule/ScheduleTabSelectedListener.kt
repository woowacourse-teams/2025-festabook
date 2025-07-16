package com.daedan.festabook.presentation.schedule

import android.graphics.Typeface
import com.daedan.festabook.databinding.ItemScheduleTabBinding
import com.google.android.material.tabs.TabLayout

class ScheduleTabSelectedListener(
    private val itemTabBinding: ItemScheduleTabBinding,
) : TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.customView?.isSelected = true
        itemTabBinding.tvScheduleTabItem.setTypeface(null, Typeface.BOLD)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.customView?.isSelected = false
        itemTabBinding.tvScheduleTabItem.setTypeface(null, Typeface.NORMAL)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        return
    }
}

package com.daedan.festabook.presentation.schedule

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.schedule.adapter.SchedulePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(R.layout.fragment_schedule) {
    private lateinit var adapter: SchedulePagerAdapter
    private val tabTitles =
        listOf(
            "5/20(화)",
            "5/20(화)",
            "5/20(화)",
            "5/20(화)",
        )

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        adapter = SchedulePagerAdapter(this, tabTitles)
        binding.vpSchedule.adapter = adapter

        TabLayoutMediator(binding.tlSchedule, binding.vpSchedule) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}

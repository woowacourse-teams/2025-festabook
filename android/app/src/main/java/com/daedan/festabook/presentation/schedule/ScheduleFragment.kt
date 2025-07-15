package com.daedan.festabook.presentation.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleBinding
import com.daedan.festabook.databinding.ItemScheduleTabBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.schedule.adapter.SchedulePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(R.layout.fragment_schedule) {
    private val tabTitles =
        listOf(
            "5/20(화)",
            "5/20(화)",
            "5/20(화)",
            "5/20(화)",
        )
    private val adapter: SchedulePagerAdapter by lazy {
        SchedulePagerAdapter(this, tabTitles)
    }
    private val viewModel: ScheduleViewModel by viewModels { ScheduleViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.vpSchedule.adapter = adapter

        TabLayoutMediator(binding.tlSchedule, binding.vpSchedule) { tab, position ->
            val itemTabBinding =
                ItemScheduleTabBinding.inflate(
                    LayoutInflater.from(view.context),
                    binding.tlSchedule,
                    false,
                )
            itemTabBinding.tvScheduleTabItem.text = tabTitles[position]
            tab.customView = itemTabBinding.root
        }.attach()
    }
}

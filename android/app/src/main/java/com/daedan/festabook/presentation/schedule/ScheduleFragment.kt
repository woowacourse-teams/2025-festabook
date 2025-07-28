package com.daedan.festabook.presentation.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
    private val adapter: SchedulePagerAdapter by lazy {
        SchedulePagerAdapter(this)
    }

    private val viewModel: ScheduleViewModel by viewModels { ScheduleViewModel.Factory() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.vpSchedule.adapter = adapter
        setupObservers()
        setupScheduleTabLayout()
    }

    @SuppressLint("WrongConstant")
    private fun setupScheduleTabLayout() {
        binding.vpSchedule.offscreenPageLimit = PRELOAD_PAGE_COUNT

        TabLayoutMediator(binding.tlSchedule, binding.vpSchedule) { tab, position ->
            val itemScheduleTabBinding =
                ItemScheduleTabBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    binding.tlSchedule,
                    false,
                )
            tab.customView = itemScheduleTabBinding.root
            itemScheduleTabBinding.tvScheduleTabItem.text =
                viewModel.scheduleDatesUiState.value
                    .let { (it as? ScheduleDatesUiState.Success)?.dates?.get(position)?.date ?: "" }
        }.attach()
    }

    private fun setupObservers() {
        viewModel.scheduleDatesUiState.observe(viewLifecycleOwner) { scheduleDateUiModels ->

            when (scheduleDateUiModels) {
                is ScheduleDatesUiState.Loading -> {
                    showSkeleton(isLoading = true)
                }

                is ScheduleDatesUiState.Success -> {
                    showSkeleton(isLoading = false)
                    setupScheduleTabLayout()
                    adapter.submitList(scheduleDateUiModels.dates.map { it.id })
                }

                is ScheduleDatesUiState.Error -> {
                    showSkeleton(isLoading = false)
                    Log.d("TAG", "setupDate: ${scheduleDateUiModels.message}")
                }
            }
        }
    }

    private fun showSkeleton(isLoading: Boolean) {
        if (isLoading) {
            binding.sflScheduleTabSkeleton.visibility = View.VISIBLE
            binding.sflScheduleSkeleton.visibility = View.VISIBLE
        } else {
            binding.sflScheduleTabSkeleton.visibility = View.GONE
            binding.sflScheduleSkeleton.visibility = View.GONE

            binding.sflScheduleTabSkeleton.stopShimmer()
            binding.sflScheduleSkeleton.stopShimmer()
        }
    }

    companion object {
        private const val PRELOAD_PAGE_COUNT: Int = 2
    }
}

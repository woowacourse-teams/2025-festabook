package com.daedan.festabook.presentation.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleBinding
import com.daedan.festabook.databinding.ItemScheduleTabBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.schedule.adapter.SchedulePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class ScheduleFragment :
    BaseFragment<FragmentScheduleBinding>(R.layout.fragment_schedule),
    OnMenuItemReClickListener {
    private val adapter: SchedulePagerAdapter by lazy {
        SchedulePagerAdapter(this)
    }

    private val viewModel: ScheduleViewModel by viewModels { ScheduleViewModel.factory() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.vpSchedule.adapter = adapter
        setupObservers()
    }

    override fun onMenuItemReClick() {
        val currentScheduleTabPageFragmentPosition = binding.vpSchedule.currentItem
        val tag = getTagByFragmentPosition(currentScheduleTabPageFragmentPosition)
        val scheduleTabPageFragment = childFragmentManager.findFragmentByTag(tag)

        if (scheduleTabPageFragment is ScheduleTabPageUpdater) {
            scheduleTabPageFragment.updateScheduleTabPage()
        }
    }

    @SuppressLint("WrongConstant")
    private fun setupScheduleTabLayout(initialCurrentDateIndex: Int) {
        binding.vpSchedule.offscreenPageLimit = PRELOAD_PAGE_COUNT

        TabLayoutMediator(binding.tlSchedule, binding.vpSchedule) { tab, position ->
            setupScheduleTabView(tab, position)
            binding.vpSchedule.setCurrentItem(initialCurrentDateIndex, false)
        }.attach()
    }

    private fun setupScheduleTabView(
        tab: TabLayout.Tab,
        position: Int,
    ) {
        val itemScheduleTabBinding =
            ItemScheduleTabBinding.inflate(
                LayoutInflater.from(requireContext()),
                binding.tlSchedule,
                false,
            )
        tab.customView = itemScheduleTabBinding.root

        itemScheduleTabBinding.tvScheduleTabItem.text =
            viewModel.scheduleDatesUiState.value
                .let {
                    (it as? ScheduleDatesUiState.Success)?.dates?.get(position)?.date
                        ?: EMPTY_DATE_TEXT
                }
    }

    private fun setupObservers() {
        viewModel.scheduleDatesUiState.observe(viewLifecycleOwner) { scheduleDatesUiState ->

            when (scheduleDatesUiState) {
                is ScheduleDatesUiState.Loading -> {
                    showSkeleton(isLoading = true)
                }

                is ScheduleDatesUiState.Success -> {
                    showSkeleton(isLoading = false)
                    setupScheduleTabLayout(scheduleDatesUiState.initialDatePosition)
                    adapter.submitList(scheduleDatesUiState.dates)
                }

                is ScheduleDatesUiState.Error -> {
                    showSkeleton(isLoading = false)
                    Timber.tag("TAG").d("setupDate: ${scheduleDatesUiState.throwable.message}")
                    showErrorSnackBar(scheduleDatesUiState.throwable)
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
        private const val EMPTY_DATE_TEXT: String = ""

        private fun getTagByFragmentPosition(position: Int): String = "f$position"
    }
}

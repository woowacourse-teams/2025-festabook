package com.daedan.festabook.presentation.schedule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleTabPageBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.schedule.adapter.ScheduleAdapter

class ScheduleTabPageFragment : BaseFragment<FragmentScheduleTabPageBinding>(R.layout.fragment_schedule_tab_page) {
    private lateinit var adapter: ScheduleAdapter
    private val viewModel: ScheduleViewModel by viewModels { ScheduleViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupScheduleEventRecyclerView()

        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()

        val dateId: Long = arguments?.getLong(KEY_DATE_ID) ?: return
        viewModel.loadScheduleByDate(dateId)

        onSwipeRefreshScheduleByDateListener(dateId)
    }

    private fun setupScheduleEventRecyclerView() {
        adapter =
            ScheduleAdapter(onBookmarkCheckedListener = { scheduleEventId ->
                viewModel.updateBookmark(scheduleEventId)
            })
        binding.rvScheduleEvent.adapter = adapter
        (binding.rvScheduleEvent.itemAnimator as DefaultItemAnimator).supportsChangeAnimations =
            false
    }

    private fun onSwipeRefreshScheduleByDateListener(dateId: Long) {
        binding.srlScheduleEvent.setOnRefreshListener {
            viewModel.loadScheduleByDate(dateId)
        }
    }

    private fun setupObservers() {
        viewModel.scheduleEventsUiState.observe(viewLifecycleOwner) { schedule ->
            when (schedule) {
                is ScheduleEventsUiState.Loading -> {
                    showSkeleton(isLoading = true)
                }

                is ScheduleEventsUiState.Success -> {
                    adapter.submitList(schedule.events)
                    showSkeleton(isLoading = false)
                }

                is ScheduleEventsUiState.Error -> {
                    Log.d("TAG", "setupObservers: ${schedule.message}")
                    showSkeleton(isLoading = false)
                }
            }
        }
    }

    private fun showSkeleton(isLoading: Boolean) {
        if (isLoading) {
            binding.rvScheduleEvent.visibility = View.INVISIBLE
            binding.sflScheduleSkeleton.visibility = View.VISIBLE
        } else {
            binding.rvScheduleEvent.visibility = View.VISIBLE
            binding.sflScheduleSkeleton.visibility = View.GONE
        }
        binding.srlScheduleEvent.isRefreshing = isLoading
    }

    companion object {
        private const val KEY_DATE_ID = "dateId"

        fun newInstance(dateId: Long): ScheduleTabPageFragment {
            val fragment = ScheduleTabPageFragment()
            val args =
                Bundle().apply {
                    putLong(KEY_DATE_ID, dateId)
                }
            fragment.arguments = args
            return fragment
        }
    }
}

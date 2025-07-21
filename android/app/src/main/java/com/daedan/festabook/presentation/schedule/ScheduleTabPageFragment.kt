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
        adapter =
            ScheduleAdapter(onBookmarkCheckedListener = { scheduleEventId ->
                viewModel.updateBookmark(scheduleEventId)
            })
        binding.rvScheduleEvent.adapter = adapter
        (binding.rvScheduleEvent.itemAnimator as DefaultItemAnimator).supportsChangeAnimations =
            false
        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()
        val dateId: Long = arguments?.getLong(KEY_DATE_ID) ?: return
        viewModel.loadScheduleByDate(dateId)
    }

    private fun setupObservers() {
        viewModel.scheduleEventsUiState.observe(viewLifecycleOwner) { schedule ->
            when (schedule) {
                is ScheduleEventsUiState.Loading -> {
                    Log.d("TAG", "setupObservers: 로딩중")
                }

                is ScheduleEventsUiState.Success -> adapter.submitList(schedule.events)
                is ScheduleEventsUiState.Error -> {
                    Log.d("TAG", "setupObservers: ${schedule.message}")
                }
            }
        }
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

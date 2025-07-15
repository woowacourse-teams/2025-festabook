package com.daedan.festabook.presentation.schedule

import android.os.Bundle
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
    }

    private fun setupObservers() {
        viewModel.scheduleUiState.observe(viewLifecycleOwner) { schedule ->
            when (schedule) {
                is ScheduleUiState.Loading -> {}
                is ScheduleUiState.Success -> adapter.submitList(schedule.events)
                is ScheduleUiState.Error -> {}
            }
        }
    }

    companion object {
        private const val ARG_DATE = "arg_date"

        fun newInstance(date: String): ScheduleTabPageFragment {
            val fragment = ScheduleTabPageFragment()
            val args =
                Bundle().apply {
                    putString(ARG_DATE, date)
                }
            fragment.arguments = args
            return fragment
        }
    }
}

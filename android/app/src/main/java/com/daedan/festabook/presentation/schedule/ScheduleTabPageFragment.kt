package com.daedan.festabook.presentation.schedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleTabPageBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.schedule.ScheduleViewModel.Companion.INVALID_ID
import com.daedan.festabook.presentation.schedule.adapter.ScheduleAdapter
import timber.log.Timber

class ScheduleTabPageFragment :
    BaseFragment<FragmentScheduleTabPageBinding>(R.layout.fragment_schedule_tab_page),
    ScheduleTabPageUpdater {
    private val viewModel: ScheduleViewModel by viewModels {
        val dateId: Long = arguments?.getLong(KEY_DATE_ID, INVALID_ID) ?: INVALID_ID
        ScheduleViewModel.factory(dateId)
    }
    private val adapter: ScheduleAdapter by lazy {
        ScheduleAdapter(onBookmarkCheckedListener = { scheduleEventId ->
            viewModel.updateBookmark(scheduleEventId)
        })
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupScheduleEventRecyclerView()

        binding.lifecycleOwner = viewLifecycleOwner
        onSwipeRefreshScheduleByDateListener()
    }

    override fun updateScheduleTabPage() {
        viewModel.loadScheduleByDate()
    }

    private fun setupScheduleEventRecyclerView() {
        binding.rvScheduleEvent.adapter = adapter
        (binding.rvScheduleEvent.itemAnimator as DefaultItemAnimator).supportsChangeAnimations =
            false
        viewModel.loadScheduleByDate()
    }

    private fun onSwipeRefreshScheduleByDateListener() {
        binding.srlScheduleEvent.setOnRefreshListener {
            viewModel.loadScheduleByDate()
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
                    scrollToCenterOfCurrentEvent(schedule.currentEventPosition)
                    showSkeleton(isLoading = false)
                }

                is ScheduleEventsUiState.Error -> {
                    Timber.tag("TAG").d("setupObservers: ${schedule.throwable.message}")
                    showErrorSnackBar(schedule.throwable)
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

    private fun scrollToCenterOfCurrentEvent(position: Int) {
        val recyclerView = binding.rvScheduleEvent
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(position, NO_OFFSET)

        recyclerView.post {
            val view = layoutManager.findViewByPosition(position)
            if (view != null) {
                val viewTop = layoutManager.getDecoratedTop(view)
                val viewHeight = view.height
                val parentHeight = recyclerView.height
                val dy = viewTop - ((parentHeight - viewHeight) / HALF)

                recyclerView.smoothScrollBy(NO_OFFSET, dy)
            }
        }
    }

    companion object {
        const val KEY_DATE_ID = "dateId"
        private const val NO_OFFSET: Int = 0
        private const val HALF: Int = 2

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

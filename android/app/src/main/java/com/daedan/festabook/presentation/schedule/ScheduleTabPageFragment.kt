package com.daedan.festabook.presentation.schedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleTabPageBinding
import com.daedan.festabook.di.appGraph
import com.daedan.festabook.logging.logger
import com.daedan.festabook.logging.model.schedule.ScheduleSwipeRefreshLogData
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.schedule.ScheduleViewModel.Companion.INVALID_ID
import com.daedan.festabook.presentation.schedule.adapter.ScheduleAdapter
import timber.log.Timber

class ScheduleTabPageFragment : BaseFragment<FragmentScheduleTabPageBinding>() {
    override val layoutId: Int = R.layout.fragment_schedule_tab_page
    private val viewModel: ScheduleViewModel by viewModels {
        val dateId: Long = arguments?.getLong(KEY_DATE_ID, INVALID_ID) ?: INVALID_ID
        ScheduleViewModel.factory(appGraph.scheduleViewModelFactory, dateId)
    }
    private val adapter: ScheduleAdapter by lazy {
        ScheduleAdapter()
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

    private fun setupScheduleEventRecyclerView() {
        binding.rvScheduleEvent.adapter = adapter
        (binding.rvScheduleEvent.itemAnimator as DefaultItemAnimator).supportsChangeAnimations =
            false
        viewModel.loadScheduleByDate()
    }

    private fun onSwipeRefreshScheduleByDateListener() {
        binding.srlScheduleEvent.setOnRefreshListener {
            binding.logger.log(ScheduleSwipeRefreshLogData(binding.logger.getBaseLogData()))
            viewModel.loadScheduleByDate()
        }
    }

    private fun setupObservers() {
        viewModel.scheduleEventsUiState.observe(viewLifecycleOwner) { schedule ->
            when (schedule) {
                is ScheduleEventsUiState.Loading,
                -> {
                    showLoadingView(isLoading = true)
                    showEmptyStateMessage()
                }

                is ScheduleEventsUiState.Success -> {
                    showLoadingView(isLoading = false)
                    adapter.submitList(schedule.events) {
                        showEmptyStateMessage()
                        scrollToCenterOfCurrentEvent(schedule.currentEventPosition)
                    }
                }

                is ScheduleEventsUiState.Error -> {
                    Timber.w(
                        schedule.throwable,
                        "ScheduleTabPageFragment: ${schedule.throwable.message}",
                    )
                    showErrorSnackBar(schedule.throwable)
                    showLoadingView(isLoading = false)
                    showEmptyStateMessage()
                }
            }
        }
    }

    private fun showLoadingView(isLoading: Boolean) {
        if (isLoading) {
            binding.rvScheduleEvent.visibility = View.INVISIBLE
            binding.viewScheduleEventTimeLine.visibility = View.INVISIBLE
            binding.lavScheduleLoading.visibility = View.VISIBLE
        } else {
            binding.lavScheduleLoading.visibility = View.GONE
            binding.viewScheduleEventTimeLine.visibility = View.VISIBLE
            binding.rvScheduleEvent.visibility = View.VISIBLE
        }
        binding.srlScheduleEvent.isRefreshing = false
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

    private fun showEmptyStateMessage() {
        val itemCount = binding.rvScheduleEvent.adapter?.itemCount ?: 0

        if (itemCount == 0) {
            binding.rvScheduleEvent.visibility = View.GONE
            binding.viewScheduleEventTimeLine.visibility = View.GONE
            binding.tvEmptyState.root.visibility = View.VISIBLE
        } else {
            binding.rvScheduleEvent.visibility = View.VISIBLE
            binding.viewScheduleEventTimeLine.visibility = View.VISIBLE
            binding.tvEmptyState.root.visibility = View.GONE
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

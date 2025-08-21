package com.daedan.festabook.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentHomeBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.formatFestivalPeriod
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.home.adapter.CenterItemMotionEnlarger
import com.daedan.festabook.presentation.home.adapter.FestivalUiState
import com.daedan.festabook.presentation.home.adapter.LineupAdapter
import com.daedan.festabook.presentation.home.adapter.PosterAdapter
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }
    private val centerItemMotionEnlarger = CenterItemMotionEnlarger()

    private val posterAdapter: PosterAdapter by lazy {
        PosterAdapter()
    }
    private val lineupAdapter: LineupAdapter by lazy {
        LineupAdapter()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()
        setupAdapters()
    }

    private fun setupObservers() {
        viewModel.festivalUiState.observe(viewLifecycleOwner) { festivalUiState ->
            when (festivalUiState) {
                is FestivalUiState.Loading -> {}
                is FestivalUiState.Success -> handleSuccessState(festivalUiState)
                is FestivalUiState.Error -> {
                    showErrorSnackBar(festivalUiState.throwable)
                    Timber.w(
                        festivalUiState.throwable,
                        "HomeFragment: ${festivalUiState.throwable.message}",
                    )
                }
            }
        }
        viewModel.lineupUiState.observe(viewLifecycleOwner) { lineupUiState ->
            when (lineupUiState) {
                is LineupUiState.Loading -> {}
                is LineupUiState.Success -> {
                    lineupAdapter.submitList(lineupUiState.lineups)
                }

                is LineupUiState.Error -> {
                    showErrorSnackBar(lineupUiState.throwable)
                    Timber.w(
                        lineupUiState.throwable,
                        "HomeFragment: ${lineupUiState.throwable.message}",
                    )
                }
            }
        }
    }

    private fun setupAdapters() {
        binding.rvHomePoster.adapter = posterAdapter
        binding.rvHomeLineup.adapter = lineupAdapter
        attachSnapHelper()
        addScrollEffectListener()
    }

    private fun handleSuccessState(festivalUiState: FestivalUiState.Success) {
        binding.tvHomeOrganizationTitle.text =
            festivalUiState.organization.universityName
        binding.tvHomeFestivalTitle.text =
            festivalUiState.organization.festival.festivalName
        binding.tvHomeFestivalDate.text =
            formatFestivalPeriod(
                festivalUiState.organization.festival.startDate,
                festivalUiState.organization.festival.endDate,
            )

        val posterUrls =
            festivalUiState.organization.festival.festivalImages
                .sortedBy { it.sequence }
                .map { it.imageUrl }

        posterAdapter.submitList(posterUrls) {
            scrollToInitialPosition(posterUrls.size)
        }
    }

    private fun attachSnapHelper() {
        PagerSnapHelper().attachToRecyclerView(binding.rvHomePoster)
    }

    private fun scrollToInitialPosition(size: Int) {
        val safeMaxValue = Int.MAX_VALUE / INFINITE_SCROLL_SAFETY_FACTOR
        val initialPosition = safeMaxValue - (safeMaxValue % size)

        val layoutManager = binding.rvHomePoster.layoutManager as? LinearLayoutManager ?: return

        val itemWidth = resources.getDimensionPixelSize(R.dimen.poster_item_width)
        val offset = (binding.rvHomePoster.width / 2) - (itemWidth / 2)

        layoutManager.scrollToPositionWithOffset(initialPosition, offset)

        binding.rvHomePoster.post {
            centerItemMotionEnlarger.expandCenterItem(binding.rvHomePoster)
        }
    }

    private fun addScrollEffectListener() {
        binding.rvHomePoster.addOnScrollListener(centerItemMotionEnlarger)
    }

    override fun onDestroyView() {
        binding.rvHomePoster.clearOnScrollListeners()
        super.onDestroyView()
    }

    companion object {
        private const val INFINITE_SCROLL_SAFETY_FACTOR = 4
    }
}

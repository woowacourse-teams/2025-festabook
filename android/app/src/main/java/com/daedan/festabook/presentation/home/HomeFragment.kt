package com.daedan.festabook.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentHomeBinding
import com.daedan.festabook.di.fragment.FragmentKey
import com.daedan.festabook.logging.logger
import com.daedan.festabook.logging.model.home.ExploreClickLogData
import com.daedan.festabook.logging.model.home.HomeViewLogData
import com.daedan.festabook.logging.model.home.ScheduleClickLogData
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.formatFestivalPeriod
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.explore.ExploreActivity
import com.daedan.festabook.presentation.home.adapter.CenterItemMotionEnlarger
import com.daedan.festabook.presentation.home.adapter.FestivalUiState
import com.daedan.festabook.presentation.home.adapter.LineUpItemOfDayAdapter
import com.daedan.festabook.presentation.home.adapter.PosterAdapter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import timber.log.Timber

@ContributesIntoMap(scope = AppScope::class, binding = binding<Fragment>())
@FragmentKey(HomeFragment::class)
class HomeFragment @Inject constructor(
    private val centerItemMotionEnlarger: RecyclerView.OnScrollListener,
) : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home

    @Inject
    override lateinit var defaultViewModelProviderFactory: ViewModelProvider.Factory
    private val viewModel: HomeViewModel by viewModels({ requireActivity() })

    private val posterAdapter: PosterAdapter by lazy {
        PosterAdapter()
    }

    private val lineupOfDayAdapter: LineUpItemOfDayAdapter by lazy {
        LineUpItemOfDayAdapter()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()
        setupAdapters()
        setupNavigateToScheduleButton()
        setupNavigateToExploreButton()
    }

    private fun setupNavigateToExploreButton() {
        binding.layoutTitleWithIcon.setOnClickListener {
            binding.logger.log(ExploreClickLogData(binding.logger.getBaseLogData()))

            startActivity(ExploreActivity.newIntent(requireContext()))
        }
    }

    private fun setupNavigateToScheduleButton() {
        binding.btnNavigateToSchedule.setOnClickListener {
            binding.logger.log(
                ScheduleClickLogData(
                    baseLogData = binding.logger.getBaseLogData(),
                ),
            )

            viewModel.navigateToScheduleClick()
        }
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
                    lineupOfDayAdapter.submitList(lineupUiState.lineups.getLineupItems())
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
        binding.rvHomeLineup.adapter = lineupOfDayAdapter
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

        if (posterUrls.isNotEmpty()) {
            posterAdapter.submitList(posterUrls) {
                scrollToInitialPosition(posterUrls.size)
            }
        }
        binding.logger.log(
            HomeViewLogData(
                baseLogData = binding.logger.getBaseLogData(),
                universityName = festivalUiState.organization.universityName,
                festivalId = festivalUiState.organization.id,
            ),
        )
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
            (centerItemMotionEnlarger as CenterItemMotionEnlarger).expandCenterItem(binding.rvHomePoster)
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

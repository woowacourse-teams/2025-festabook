package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.placeListBottomSheetFollowBehavior
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.behavior.BottomSheetFollowCallback
import com.daedan.festabook.presentation.placeList.behavior.MoveToInitialPositionCallback
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    PlaceClickListener,
    OnMenuItemReClickListener,
    OnMapReadyCallback {
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }
    private val childViewModel by viewModels<PlaceListChildViewModel> { PlaceListChildViewModel.Factory }

    private val placeAdapter by lazy {
        PlaceListAdapter(this)
    }

    private lateinit var moveToInitialPositionCallback: MoveToInitialPositionCallback

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            setUpPlaceAdapter()
            setBehaviorCallback()
            setUpObserver()
            setUpBinding()
        }
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        Timber.d("onPlaceClicked: $place")
        startPlaceDetailActivity(place)
    }

    override fun onMenuItemReClick() {
        if (binding.root.isGone || !isResumed || view == null) return
        val layoutParams = binding.layoutPlaceList.layoutParams as? CoordinatorLayout.LayoutParams
        val behavior = layoutParams?.behavior as? BottomSheetBehavior
        behavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    override fun onMapReady(naverMap: NaverMap) {
        binding.lbvCurrentLocation.map = naverMap
    }

    private fun setUpPlaceAdapter() {
        binding.rvPlaces.adapter = placeAdapter
        (binding.rvPlaces.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    private fun setUpObserver() {
        childViewModel.places.observe(viewLifecycleOwner) { places ->
            when (places) {
                is PlaceListUiState.Loading -> showSkeleton()
                is PlaceListUiState.Success -> {
                    hideSkeleton()
                    placeAdapter.submitList(places.value) {
                        binding.rvPlaces.scrollToPosition(0)
                    }
                }

                is PlaceListUiState.Error -> {
                    hideSkeleton()
                    binding.tvErrorToLoadPlaceInfo.visibility = View.VISIBLE
                    Timber.w(places.throwable, "PlaceListFragment: ${places.throwable.message}")
                    showErrorSnackBar(places.throwable)
                }
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { selectedPlace ->
            startPlaceDetailActivity(selectedPlace)
        }

        viewModel.selectedCategories.observe(viewLifecycleOwner) { selectedCategories ->
            if (selectedCategories.isEmpty()) {
                childViewModel.clearPlacesFilter()
            } else {
                childViewModel.filterPlaces(selectedCategories)
            }
        }

        viewModel.isExceededMaxLength.observe(viewLifecycleOwner) { isExceededMaxLength ->
            moveToInitialPositionCallback.setIsExceededMaxLength(isExceededMaxLength)
            binding.chipBackToInitialPosition.visibility = if (isExceededMaxLength) View.VISIBLE else View.GONE
        }
    }

    private fun setUpBinding() {
        binding.chipBackToInitialPosition.setOnClickListener {
            viewModel.onBackToInitialPositionClicked()
        }
        binding.rvPlaces.itemAnimator = null
    }

    private fun setBehaviorCallback() {
        moveToInitialPositionCallback = MoveToInitialPositionCallback(binding.chipBackToInitialPosition.id)

        binding.lbvCurrentLocation
            .placeListBottomSheetFollowBehavior()
            ?.setCallback(
                BottomSheetFollowCallback(binding.lbvCurrentLocation.id),
            )

        binding.chipBackToInitialPosition
            .placeListBottomSheetFollowBehavior()
            ?.setCallback(moveToInitialPositionCallback)
    }

    private fun startPlaceDetailActivity(place: PlaceUiModel) {
        viewModel.selectPlace(place.id)
    }

    private fun showSkeleton() {
        binding.tvErrorToLoadPlaceInfo.visibility = View.GONE
        binding.sflScheduleSkeleton.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.sflScheduleSkeleton.visibility = View.GONE
        binding.sflScheduleSkeleton.stopShimmer()
    }

    private fun startPlaceDetailActivity(placeDetail: PlaceDetailUiModel) {
        Timber.d("start detail activity")
        val intent = PlaceDetailActivity.newIntent(requireContext(), placeDetail)
        startActivity(intent)
    }
}

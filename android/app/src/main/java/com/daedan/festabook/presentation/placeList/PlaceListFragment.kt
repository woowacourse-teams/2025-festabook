package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.initialPadding
import com.daedan.festabook.presentation.common.placeListBottomSheetFollowBehavior
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.behavior.BottomSheetFollowCallback
import com.daedan.festabook.presentation.placeList.behavior.MoveToInitialPositionCallback
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.placeMap.MapManager
import com.daedan.festabook.presentation.placeList.placeMap.getMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    PlaceClickListener,
    OnMenuItemReClickListener {
    private val viewModel by viewModels<PlaceListViewModel> { PlaceListViewModel.Factory }

    private val placeAdapter by lazy {
        PlaceListAdapter(this)
    }

    private val locationSource by lazy {
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private lateinit var mapManager: MapManager

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            setUpPlaceAdapter()
            setUpMapManager()
            setUpObserver()
            setUpBinding()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapManager.clearMapManager()
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        startPlaceDetailActivity(place)
    }

    override fun onMenuItemReClick() {
        val layoutParams = binding.layoutPlaceList.layoutParams as? CoordinatorLayout.LayoutParams
        val behavior = layoutParams?.behavior as? BottomSheetBehavior
        behavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun setUpBinding() {
        binding.cgCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedCategories =
                checkedIds.mapNotNull {
                    val category = group.findViewById<Chip>(it).tag
                    category as? PlaceCategoryUiModel
                }
            binding.chipCategoryAll.isChecked = selectedCategories.isEmpty()

            if (selectedCategories.isEmpty()) {
                viewModel.clearPlacesFilter()
                mapManager.clearFilter()
            } else {
                viewModel.filterPlaces(selectedCategories)
                mapManager.filterPlace(selectedCategories)
            }
        }
        binding.chipBackToInitialPosition.setOnClickListener {
            mapManager.moveToInitialPosition()
        }
        setUpChipCategoryAllListener()
    }

    private fun setUpChipCategoryAllListener() {
        binding.chipCategoryAll.setOnClickListener {
            binding.cgCategories.children.forEach {
                val chip = (it as? Chip) ?: return@forEach
                chip.isChecked = chip.id == binding.chipCategoryAll.id
            }
        }
    }

    private suspend fun setUpMapManager() {
        val mapFragment = binding.fcvMapContainer.getFragment<MapFragment>()
        naverMap = mapFragment.getMap()
        binding.lbvCurrentLocation.map = naverMap
        naverMap.locationSource = locationSource
    }

    private fun setUpPlaceAdapter() {
        binding.rvPlaces.adapter = placeAdapter
        (binding.rvPlaces.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    private fun setUpObserver() {
        viewModel.places.observe(viewLifecycleOwner) { places ->
            when (places) {
                is PlaceListUiState.Loading -> showSkeleton()
                is PlaceListUiState.Success -> {
                    hideSkeleton()
                    placeAdapter.submitList(places.value) {
                        binding.rvPlaces.itemAnimator = null
                        binding.rvPlaces.scrollToPosition(0)
                    }
                }

                is PlaceListUiState.Error -> {
                    hideSkeleton()
                    binding.tvErrorToLoadPlaceInfo.visibility = View.VISIBLE
                    Timber.d("places: ${places.throwable.message}")
                    showErrorSnackBar(places.throwable)
                }
            }
        }

        viewModel.placeGeographies.observe(viewLifecycleOwner) { placeGeographies ->
            when (placeGeographies) {
                is PlaceListUiState.Loading -> Unit
                is PlaceListUiState.Success -> {
                    hideSkeleton()
                    mapManager.setPlaceLocation(placeGeographies.value)
                }

                is PlaceListUiState.Error -> {
                    Timber.d("placeGeographies: ${placeGeographies.throwable.message}")
                    showErrorSnackBar(placeGeographies.throwable)
                }
            }
        }

        viewModel.initialMapSetting.observe(viewLifecycleOwner) { initialMapSetting ->
            if (initialMapSetting !is PlaceListUiState.Success) return@observe
            if (!::mapManager.isInitialized) {
                mapManager =
                    MapManager(naverMap, binding.initialPadding().toInt(), initialMapSetting.value)
            }
            mapManager.setupMap()
            mapManager.setupBackToInitialPosition { isExceededMaxLength ->
                if (isExceededMaxLength) {
                    binding.chipBackToInitialPosition.visibility = View.VISIBLE
                } else {
                    binding.chipBackToInitialPosition.visibility = View.GONE
                }
            }
            setBehaviorCallback()
        }
    }

    private fun setBehaviorCallback() {
        binding.lbvCurrentLocation
            .placeListBottomSheetFollowBehavior()
            ?.setCallback(
                BottomSheetFollowCallback(binding.lbvCurrentLocation.id),
            )

        binding.chipBackToInitialPosition
            .placeListBottomSheetFollowBehavior()
            ?.setCallback(
                MoveToInitialPositionCallback(binding.chipBackToInitialPosition.id, mapManager),
            )
    }

    private fun startPlaceDetailActivity(place: PlaceUiModel) {
        startActivity(PlaceDetailActivity.newIntent(requireContext(), place))
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    }
}

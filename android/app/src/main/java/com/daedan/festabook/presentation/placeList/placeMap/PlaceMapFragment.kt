package com.daedan.festabook.presentation.placeList.placeMap

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceMapBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.main.MainActivity.Companion.newInstance
import com.daedan.festabook.presentation.placeList.PlaceListFragment
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
import com.daedan.festabook.presentation.placeList.placeCategory.PlaceCategoryFragment
import com.daedan.festabook.presentation.placeList.placeDetailPreview.PlaceDetailPreviewFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.getValue

class PlaceMapFragment :
    BaseFragment<FragmentPlaceMapBinding>(R.layout.fragment_place_map),
    OnMenuItemReClickListener {
    private lateinit var naverMap: NaverMap
    private val locationSource by lazy {
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }
    private var mapManager: MapManager? = null
    private val viewModel by viewModels<PlaceListViewModel> { PlaceListViewModel.Factory }

    private val placeListFragment by lazy {
        PlaceListFragment().newInstance()
    }

    private val placeDetailPreviewFragment by lazy {
        PlaceDetailPreviewFragment().newInstance()
    }

    private val placeCategoryFragment by lazy {
        PlaceCategoryFragment().newInstance()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.commit {
            add(R.id.fcv_place_list_container, placeListFragment, null)
            add(R.id.fcv_map_container, placeDetailPreviewFragment, null)
            add(R.id.fcv_place_category_container, placeCategoryFragment, null)
        }
        lifecycleScope.launch {
            setUpMapManager()
            setUpObserver()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapManager?.clearMapManager()
    }

    private suspend fun setUpMapManager() {
        val mapFragment = binding.fcvMapContainer.getFragment<MapFragment>()
        naverMap = mapFragment.getMap()
        (placeListFragment as? OnMapReadyCallback)?.onMapReady(naverMap)
        naverMap.locationSource = locationSource
    }

    private fun setUpObserver() {
        viewModel.placeGeographies.observe(viewLifecycleOwner) { placeGeographies ->
            when (placeGeographies) {
                is PlaceListUiState.Loading -> Unit
                is PlaceListUiState.Success -> {
                    mapManager?.setPlaceLocation(placeGeographies.value + DummyPlaceGeography.VALUE)
                }

                is PlaceListUiState.Error -> {
                    Timber.w(
                        placeGeographies.throwable,
                        "PlaceListFragment: ${placeGeographies.throwable.message}",
                    )
                    showErrorSnackBar(placeGeographies.throwable)
                }
            }
        }

        viewModel.initialMapSetting.observe(viewLifecycleOwner) { initialMapSetting ->
            if (initialMapSetting !is PlaceListUiState.Success) return@observe
            if (mapManager == null) {
                mapManager =
                    MapManager(
                        naverMap,
                        getInitialPadding(requireContext()),
                        MapClickListenerImpl(viewModel),
                        initialMapSetting.value,
                    )
                mapManager?.setupMap()
                mapManager?.setupBackToInitialPosition { isExceededMaxLength ->
                    viewModel.setIsExceededMaxLength(isExceededMaxLength)
                }
            }
        }

        viewModel.backToInitialPositionClicked.observe(viewLifecycleOwner) { event ->
            mapManager?.moveToPosition()
        }

        viewModel.selectedCategories.observe(viewLifecycleOwner) { selectedCategories ->
            if (selectedCategories.isEmpty()) {
                mapManager?.clearFilter()
            } else {
                mapManager?.filterPlace(selectedCategories)
            }
        }

        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            when (selectedPlace) {
                is SelectedPlaceUiState.Success -> {
                    mapManager?.selectMarker(selectedPlace.value.place.id)
                }

                is SelectedPlaceUiState.Empty -> {
                    mapManager?.unselectMarker()
                }

                is SelectedPlaceUiState.Secondary -> {
                    mapManager?.selectMarker(selectedPlace.placeId)
                }

                else -> Unit
            }
        }
    }

    override fun onMenuItemReClick() {
        val childFragments =
            listOf(
                placeListFragment,
                placeDetailPreviewFragment,
                placeCategoryFragment,
            )
        childFragments.forEach { fragment ->
            (fragment as? OnMenuItemReClickListener)?.onMenuItemReClick()
        }
        mapManager?.moveToPosition()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234

        private fun getInitialPadding(context: Context): Int = 254.toPx(context)
    }
}

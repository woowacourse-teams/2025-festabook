package com.daedan.festabook.presentation.placeList.placeMap

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceMapBinding
import com.daedan.festabook.domain.model.TimeTag
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
import com.daedan.festabook.presentation.placeList.placeDetailPreview.PlaceDetailPreviewSecondaryFragment
import com.daedan.festabook.presentation.placeList.placeMap.timeTagSpinner.adapter.TimeTagSpinnerAdapter
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import timber.log.Timber

interface OnTimeTagSelectedListener {
    fun onTimeTagSelected(item: TimeTag)

    fun onNothingSelected()
}

class PlaceMapFragment :
    BaseFragment<FragmentPlaceMapBinding>(R.layout.fragment_place_map),
    OnMenuItemReClickListener,
    OnTimeTagSelectedListener {
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

    private val placeDetailPreviewSecondaryFragment by lazy {
        PlaceDetailPreviewSecondaryFragment().newInstance()
    }

    private val mapFragment by lazy {
        MapFragment().newInstance() as MapFragment
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinnerSelectTimeTag.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    val item = parent.getItemAtPosition(position) as TimeTag

                    onTimeTagSelected(item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    onNothingSelected()
                }
            }

        childFragmentManager.commit {
            add(R.id.fcv_map_container, mapFragment, null)
            add(R.id.fcv_place_list_container, placeListFragment, null)
            add(R.id.fcv_map_container, placeDetailPreviewFragment, null)
            add(R.id.fcv_place_category_container, placeCategoryFragment, null)
            add(R.id.fcv_map_container, placeDetailPreviewSecondaryFragment, null)
            hide(placeDetailPreviewFragment)
            hide(placeDetailPreviewSecondaryFragment)
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
        naverMap = mapFragment.getMap()
        (placeListFragment as? OnMapReadyCallback)?.onMapReady(naverMap)
        naverMap.locationSource = locationSource
        binding.viewMapTouchEventIntercept.setOnMapDragListener {
            viewModel.onMapViewClick()
        }
    }

    private fun setUpObserver() {
        viewModel.timeTags.observe(viewLifecycleOwner) { timeTags ->
            // 타임태그가 없는 경우 메뉴 GONE
            binding.layoutMapMenu.visibility = if (timeTags.isNullOrEmpty()) View.GONE else View.VISIBLE

            if (binding.spinnerSelectTimeTag.adapter == null) {
                val adapter = TimeTagSpinnerAdapter(requireContext(), timeTags.toMutableList())
                binding.spinnerSelectTimeTag.adapter = adapter
            } else {
                val adapter = binding.spinnerSelectTimeTag.adapter as TimeTagSpinnerAdapter
                adapter.updateItems(timeTags)
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.placeGeographies.observe(viewLifecycleOwner) { placeGeographies ->
            when (placeGeographies) {
                is PlaceListUiState.Loading -> Unit
                is PlaceListUiState.Success -> {
                    mapManager?.setPlaceLocation(placeGeographies.value)
                    viewModel.selectedTimeTag.observe(viewLifecycleOwner) { selectedTimeTag ->
                        mapManager?.filterMarkersByTimeTag(selectedTimeTag.timeTagId)
                    }
                }

                is PlaceListUiState.Error -> {
                    Timber.w(
                        placeGeographies.throwable,
                        "PlaceListFragment: ${placeGeographies.throwable.message}",
                    )
                    showErrorSnackBar(placeGeographies.throwable)
                }

                else -> Unit
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
                mapManager?.filterMarkersByCategories(selectedCategories)
            }
        }

        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            childFragmentManager.commit {
                setReorderingAllowed(true)

                when (selectedPlace) {
                    is SelectedPlaceUiState.Success -> {
                        mapManager?.selectMarker(selectedPlace.value.place.id)
                        if (selectedPlace.isSecondary) {
                            hide(placeListFragment)
                            hide(placeDetailPreviewFragment)
                            show(placeDetailPreviewSecondaryFragment)
                        } else {
                            hide(placeListFragment)
                            hide(placeDetailPreviewSecondaryFragment)
                            show(placeDetailPreviewFragment)
                        }
                    }

                    is SelectedPlaceUiState.Empty -> {
                        mapManager?.unselectMarker()
                        hide(placeDetailPreviewFragment)
                        hide(placeDetailPreviewSecondaryFragment)
                        show(placeListFragment)
                    }

                    else -> Unit
                }
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

    override fun onTimeTagSelected(item: TimeTag) {
        viewModel.unselectPlace()
        viewModel.onDaySelected(item)
    }

    override fun onNothingSelected() {
    }
}

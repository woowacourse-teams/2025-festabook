package com.daedan.festabook.presentation.placeList.placeMap

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceMapBinding
import com.daedan.festabook.di.fragment.FragmentKey
import com.daedan.festabook.domain.model.TimeTag
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.placeList.PlaceListFragment
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.logging.LocationPermissionChanged
import com.daedan.festabook.presentation.placeList.logging.PlaceFragmentEnter
import com.daedan.festabook.presentation.placeList.logging.PlaceMarkerClick
import com.daedan.festabook.presentation.placeList.logging.PlaceTimeTagSelected
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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<Fragment>(),
)
@FragmentKey(PlaceMapFragment::class)
@Inject
class PlaceMapFragment(
    private val fragmentFactory: FragmentFactory,
    private val placeListFragment: PlaceListFragment,
    private val placeDetailPreviewFragment: PlaceDetailPreviewFragment,
    private val placeCategoryFragment: PlaceCategoryFragment,
    private val placeDetailPreviewSecondaryFragment: PlaceDetailPreviewSecondaryFragment,
    private val mapFragment: MapFragment,
) : BaseFragment<FragmentPlaceMapBinding>(),
    OnMenuItemReClickListener,
    OnTimeTagSelectedListener {
    override val layoutId: Int = R.layout.fragment_place_map

    @Inject
    override lateinit var defaultViewModelProviderFactory: ViewModelProvider.Factory

    private lateinit var naverMap: NaverMap

    private val locationSource by lazy {
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE).apply {
            activate {
                binding.logger.log(
                    LocationPermissionChanged(
                        baseLogData = binding.logger.getBaseLogData(),
                    ),
                )
            }
        }
    }
    private var mapManager: MapManager? = null

    private val viewModel: PlaceListViewModel by viewModels()

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
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.fcv_map_container, mapFragment)
                add(R.id.fcv_place_list_container, placeListFragment)
                add(R.id.fcv_map_container, placeDetailPreviewFragment)
                add(R.id.fcv_place_category_container, placeCategoryFragment)
                add(R.id.fcv_map_container, placeDetailPreviewSecondaryFragment)
                hide(placeDetailPreviewFragment)
                hide(placeDetailPreviewSecondaryFragment)
            }
        }
        lifecycleScope.launch {
            setUpMapManager()
            setUpObserver()
        }
        binding.logger.log(
            PlaceFragmentEnter(
                baseLogData = binding.logger.getBaseLogData(),
            ),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mapManager?.clearMapManager()
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

    override fun onTimeTagSelected(item: TimeTag) {
        viewModel.unselectPlace()
        viewModel.onDaySelected(item)
        binding.logger.log(
            PlaceTimeTagSelected(
                baseLogData = binding.logger.getBaseLogData(),
                timeTagName = item.name,
            ),
        )
    }

    override fun onNothingSelected() = Unit

    private suspend fun setUpMapManager() {
        naverMap = mapFragment.getMap()
        (placeListFragment as? OnMapReadyCallback)?.onMapReady(naverMap)
        naverMap.locationSource = locationSource
        binding.viewMapTouchEventIntercept.setOnMapDragListener {
            viewModel.onMapViewClick()
        }
    }

    private fun setupFragmentFactory() {
        childFragmentManager.fragmentFactory = fragmentFactory
    }

    private fun setUpObserver() {
        viewModel.timeTags.observe(viewLifecycleOwner) { timeTags ->
            // 타임태그가 없는 경우 메뉴 GONE
            binding.layoutMapMenu.visibility =
                if (timeTags.isNullOrEmpty()) View.GONE else View.VISIBLE

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

        viewModel.backToInitialPositionClicked.observe(viewLifecycleOwner) {
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
                        binding.logger.log(
                            PlaceMarkerClick(
                                baseLogData = binding.logger.getBaseLogData(),
                                placeId = selectedPlace.value.place.id,
                                timeTagName = viewModel.selectedTimeTag.value?.name ?: "undefined",
                                category = selectedPlace.value.place.category.name,
                            ),
                        )
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234

        private fun getInitialPadding(context: Context): Int = 254.toPx(context)
    }
}

package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import coil3.load
import coil3.request.placeholder
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.initialPadding
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
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
    PlaceClickListener {
    private val viewModel by viewModels<PlaceListViewModel> { PlaceListViewModel.Factory }

    private val placeAdapter by lazy {
        PlaceListAdapter(this)
    }

    private val locationSource by lazy {
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private lateinit var selectedPlaceBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var mapManager: MapManager

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        selectedPlaceBottomSheetBehavior = BottomSheetBehavior.from(binding.nsSelectedPlace)

        val bottomSheetCallback = PlaceListBottomSheetCallback(viewModel)
        selectedPlaceBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        lifecycleScope.launch {
            setUpPlaceAdapter()
            setUpMapManager()
            setUpObserver()
            setUpBinding()
        }
    }

    override fun onResume() {
        super.onResume()
        selectedPlaceBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        startPlaceDetailActivity(place)
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
        mapManager =
            MapManager(
                naverMap,
                binding.initialPadding(),
                onMarkerClickListener = { placeId, category ->
                    Timber.d("marker clicked $placeId $category")
                    viewModel.selectPlace(placeId, category)
                    binding.layoutPlaceList.visibility = View.GONE
                },
            )
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
            setUpMap(initialMapSetting)
        }

        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            if (selectedPlace == null) {
                binding.nsSelectedPlace.visibility = View.GONE
            } else {
                binding.nsSelectedPlace.visibility = View.VISIBLE
                updateSelectedPlaceUi(selectedPlace)
            }
        }
        viewModel.navigateToDetail.observe(viewLifecycleOwner) { selectedPlace ->
            startPlaceDetailActivity(selectedPlace)
        }
    }

    private fun setUpMap(initialMapSetting: PlaceListUiState.Success<InitialMapSettingUiModel>) {
        mapManager.setupMap(initialMapSetting.value)
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

    private fun updateSelectedPlaceUi(selectedPlace: PlaceDetailUiModel) {
        with(binding) {
            tvSelectedPlaceTitle.text = selectedPlace.place.title
            tvSelectedPlaceLocation.text = selectedPlace.place.location
            tvSelectedPlaceTime.text = selectedPlace.operatingHours
            tvSelectedPlaceHost.text = selectedPlace.host
            tvSelectedPlaceDescription.text = selectedPlace.place.description

            tvSelectedPlaceCategory.text =
                when (selectedPlace.place.category) {
                    PlaceCategoryUiModel.FOOD_TRUCK -> getString(R.string.map_category_food_truck)
                    PlaceCategoryUiModel.BAR -> getString(R.string.map_category_bar)
                    PlaceCategoryUiModel.BOOTH -> getString(R.string.map_category_booth)
                    else -> ""
                }

            ivSelectedPlaceImage.load(
                selectedPlace.featuredImage,
            ) {
                placeholder(R.color.gray300)
            }
        }
    }

    private fun startPlaceDetailActivity(placeDetail: PlaceDetailUiModel) {
        Timber.d("start detail activity")
        val intent = PlaceDetailActivity.newIntent(requireContext(), placeDetail)

        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(binding.tvSelectedPlaceTitle, "selected_place_title_transition"),
                Pair(binding.ivSelectedPlaceImage, "selected_place_image_transition"),
            )

        startActivity(intent, options.toBundle())
        selectedPlaceBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    }
}

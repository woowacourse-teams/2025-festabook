package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.bottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.common.initialPadding
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.adapter.PlaceListItemDecoration
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.placeMap.MapManager
import com.daedan.festabook.presentation.placeList.placeMap.getMap
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

    private val placeListItemDecoration by lazy {
        PlaceListItemDecoration(placeAdapter)
    }

    private val fragmentContainer = mutableMapOf<PlaceUiModel, PlaceDetailFragment>()

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

    override fun onPlaceClicked(place: PlaceUiModel) {
        startPlaceDetailFragment(place)
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
        mapManager = MapManager(naverMap, binding.initialPadding())
    }

    private fun setUpPlaceAdapter() {
        binding.rvPlaces.adapter = placeAdapter
        binding.rvPlaces.addItemDecoration(placeListItemDecoration)
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
                    binding.viewItemHeader.visibility = View.VISIBLE
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
    }

    private fun setUpMap(initialMapSetting: PlaceListUiState.Success<InitialMapSettingUiModel>) {
        mapManager.setupMap(initialMapSetting.value)
    }

    private fun startPlaceDetailFragment(place: PlaceUiModel) {
        parentFragmentManager.registerFragmentLifecycleCallbacks(
            bottomNavigationViewAnimationCallback,
            false,
        )
        parentFragmentManager.commitWithSavedFragment(place) {
            setCustomAnimations(
                R.anim.anim_fade_in_left,
                R.anim.anim_fade_out,
                R.anim.anim_fade_in_right,
                R.anim.anim_fade_out,
            )
        }
    }

    private fun FragmentManager.commitWithSavedFragment(
        place: PlaceUiModel,
        block: FragmentTransaction.() -> Unit,
    ) {
        val placeDetailFragment = getPlaceDetailFragment(place) ?: return

        commit {
            block()
            add(
                R.id.fcv_fragment_container,
                placeDetailFragment,
            )
            hide(this@PlaceListFragment)
            addToBackStack(null)
        }
    }

    private fun getPlaceDetailFragment(place: PlaceUiModel): Fragment? {
        fragmentContainer[place] ?: run {
            fragmentContainer[place] =
                PlaceDetailFragment.newInstance(place)
        }

        return fragmentContainer[place]
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

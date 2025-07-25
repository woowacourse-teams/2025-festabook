package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.bottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.common.initialPadding
import com.daedan.festabook.presentation.common.placeListScrollBehavior
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.placeMap.MapManager
import com.daedan.festabook.presentation.placeList.placeMap.MapScrollManager
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource

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

    private val fragmentContainer = mutableMapOf<PlaceUiModel, PlaceDetailFragment>()

    private val mapManager by lazy {
        MapManager(
            naverMap,
            binding.initialPadding(),
        )
    }

    private val mapScrollManager by lazy {
        MapScrollManager(naverMap)
    }

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlaceAdapter()
        setUpMap()
        setUpObserver()
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        viewModel.setPlace(place)
        startPlaceDetailFragment()
    }

    override fun onBookmarkClicked(place: PlaceUiModel) {
        viewModel.updateBookmark(place)
    }

    private fun setUpPlaceAdapter() {
        binding.rvPlaces.adapter = placeAdapter
        (binding.rvPlaces.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    private fun setUpObserver() {
        viewModel.places.observe(viewLifecycleOwner) { places ->
            if (places !is PlaceListUiState.Success) return@observe
            placeAdapter.submitList(places.value)
        }
    }

    private fun setUpMap() {
        val mapFragment = binding.fcvMapContainer.getFragment<MapFragment>()
        viewModel.initialMapSetting.observe(viewLifecycleOwner) { initialMapSetting ->
            if (initialMapSetting !is PlaceListUiState.Success) return@observe
            mapFragment.getMapAsync { map ->
                naverMap = map
                binding.lbvCurrentLocation.map = naverMap
                naverMap.locationSource = locationSource
                mapManager.setupMap(initialMapSetting.value)
                setPlaceListScrollListener()
            }
        }
    }

    private fun setPlaceListScrollListener() {
        val behavior = binding.layoutPlaceList.placeListScrollBehavior()
        behavior?.setOnScrollListener { dy ->
            mapScrollManager.cameraScroll(dy)
        }
    }

    private fun startPlaceDetailFragment() {
        parentFragmentManager.registerFragmentLifecycleCallbacks(
            bottomNavigationViewAnimationCallback,
            false,
        )
        parentFragmentManager.commitWithSavedFragment {
            setCustomAnimations(
                R.anim.anim_fade_in_left,
                R.anim.anim_fade_out,
                R.anim.anim_fade_in_right,
                R.anim.anim_fade_out,
            )
        }
    }

    private fun FragmentManager.commitWithSavedFragment(block: FragmentTransaction.() -> Unit) {
        val placeDetailFragment = getPlaceDetailFragment() ?: return

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

    private fun getPlaceDetailFragment(): Fragment? {
        val selectedPlace = viewModel.selectedPlace.value ?: return null
        fragmentContainer[selectedPlace] ?: run {
            fragmentContainer[selectedPlace] =
                PlaceDetailFragment.newInstance(selectedPlace)
        }

        return fragmentContainer[selectedPlace]
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    }
}

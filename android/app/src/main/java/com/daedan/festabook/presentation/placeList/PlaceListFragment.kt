package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.bottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.common.initialPadding
import com.daedan.festabook.presentation.common.placeListScrollBehavior
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.dummy.DummyMapData
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.placeMap.MapManager
import com.daedan.festabook.presentation.placeList.placeMap.MapScrollManager
import com.naver.maps.map.MapFragment
import com.naver.maps.map.util.FusedLocationSource

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    OnPlaceClickedListener {
    private val viewModel by viewModels<PlaceListViewModel>()

    private val placeAdapter by lazy {
        PlaceListAdapter(this)
    }

    private val locationSource by lazy {
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlaceAdapter()
        setUpMap()
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        viewModel.setPlace(place)
        startPlaceDetailFragment()
    }

    private fun setUpPlaceAdapter() {
        binding.rvPlaces.adapter = placeAdapter
        placeAdapter.submitList(DummyPlace.placeUiModelList)
    }

    private fun setUpMap() {
        val mapFragment = binding.fcvMapContainer.getFragment<MapFragment>()

        mapFragment.getMapAsync { map ->
            val initialPadding = binding.initialPadding()
            val mapScrollManager = MapScrollManager(map)
            binding.lbvCurrentLocation.map = map
            map.locationSource = locationSource

            MapManager(
                map,
                initialPadding,
                DummyMapData.initialMapSettingUiModel,
            ).setPlaceLocation(
                DummyMapData.placeCoordinates,
            )

            val behavior = binding.layoutPlaceList.placeListScrollBehavior()
            behavior?.setOnScrollListener { dy ->
                mapScrollManager.cameraScroll(dy)
            }
        }
    }

    private fun startPlaceDetailFragment() {
        parentFragmentManager.registerFragmentLifecycleCallbacks(
            bottomNavigationViewAnimationCallback,
            false,
        )
        parentFragmentManager.commit {
            setCustomAnimations(
                R.anim.anim_fade_in_left,
                R.anim.anim_fade_out,
                R.anim.anim_fade_in_right,
                R.anim.anim_fade_out,
            )
            add(
                R.id.fcv_fragment_container,
                PlaceDetailFragment.newInstance(
                    viewModel.place.value ?: return,
                ),
            )
            hide(this@PlaceListFragment)
            addToBackStack(null)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    }
}

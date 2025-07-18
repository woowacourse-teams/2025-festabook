package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.LOGO_MARGIN_TOP_PX
import com.daedan.festabook.presentation.common.getBottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.common.setContentPaddingBottom
import com.daedan.festabook.presentation.common.setLogoMarginBottom
import com.daedan.festabook.presentation.common.setPlaceLocation
import com.daedan.festabook.presentation.common.setUp
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.dummy.DummyMapData
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
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

    private lateinit var locationSource: FusedLocationSource

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter = placeAdapter
        placeAdapter.submitList(DummyPlace.placeUiModelList)

        setUpObservers()
        setUpMap()
        setUpLocation()
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        viewModel.publishClickEvent()
        viewModel.setPlace(place)
    }

    private fun setUpObservers() {
        viewModel.userActionEvent.observe(this) { event ->
            when (event) {
                PlaceListUserActionEvent.PLACE_CLICKED -> startPlaceDetailFragment()
            }
        }
    }

    private fun setUpLocation() {
        locationSource =
            FusedLocationSource(
                this,
                LOCATION_PERMISSION_REQUEST_CODE,
            )
    }

    private fun setUpMap() {
        val mapFragment = binding.fcvMapContainer.getFragment<MapFragment>()

        mapFragment.getMapAsync { map ->
            val initialPadding = binding.layoutPlaceList.height / 2
            binding.lbvCurrentLocation.map = map
            map.setUp(
                DummyMapData.initialMapSettingUiModel,
            )
            map.setContentPaddingBottom(initialPadding)
            map.setLogoMarginBottom(initialPadding - LOGO_MARGIN_TOP_PX)
            map.setPlaceLocation(DummyMapData.placeCoordinates)
            map.locationSource = locationSource

//            val behavior = binding.layoutPlaceList.placeListScrollBehavior()
//            behavior?.onScrollListener = { dy ->
//                map.cameraScroll(dy)
//            }
        }
    }

    private fun startPlaceDetailFragment() {
        parentFragmentManager.registerFragmentLifecycleCallbacks(
            getBottomNavigationViewAnimationCallback(),
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

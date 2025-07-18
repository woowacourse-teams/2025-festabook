package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.cameraScroll
import com.daedan.festabook.presentation.common.getBottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.common.placeListScrollBehavior
import com.daedan.festabook.presentation.common.setContentPaddingBottom
import com.daedan.festabook.presentation.common.setUp
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.dummy.DummyMapData
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.naver.maps.map.MapFragment

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    OnPlaceClickedListener {
    private val viewModel by viewModels<PlaceListViewModel>()

    private val placeAdapter by lazy {
        PlaceListAdapter(this)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter = placeAdapter
        placeAdapter.submitList(DummyPlace.placeUiModelList)

        setUpObservers()
        setUpMap()
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

    private fun setUpMap() {
        val mapFragment = binding.fcvMapContainer.getFragment<MapFragment>()
        mapFragment.getMapAsync { map ->
            binding.lbvCurrentLocation.map = map
            map.setUp(
                DummyMapData.initialMapSettingUiModel,
            )
            map.setContentPaddingBottom(
                binding.layoutPlaceList.height / 2,
            )
            val behavior = binding.layoutPlaceList.placeListScrollBehavior()
            behavior?.onScrollListener = { dy ->
                map.cameraScroll(dy)
            }
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
}

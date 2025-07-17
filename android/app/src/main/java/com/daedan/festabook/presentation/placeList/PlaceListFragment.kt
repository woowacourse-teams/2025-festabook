package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.getBottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    OnPlaceClickedListener {
    private val viewModel by viewModels<PlaceListViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter =
            PlaceListAdapter(this).apply {
                submitList(DummyPlace.placeUiModelList)
            }

        setUpObservers()
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

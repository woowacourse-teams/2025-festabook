package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace
import com.daedan.festabook.presentation.placeList.uimodel.Place
import com.daedan.festabook.presentation.placeList.uimodel.PlaceListEvent

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    PlaceListHandler {
    private val viewModel by viewModels<PlaceListViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter =
            PlaceListAdapter(this).apply {
                submitList(DummyPlace.placeList)
            }

        setUpObservers()
    }

    override fun onPlaceClicked(place: Place) {
        viewModel.publishClickEvent()
        viewModel.setPlace(place)
    }

    private fun setUpObservers() {
        viewModel.event.observe(this) { event ->
            when (event) {
                PlaceListEvent.PLACE_CLICKED -> startPlaceDetailFragment()
                PlaceListEvent.RUNNING -> Unit
            }
        }
    }

    private fun startPlaceDetailFragment() {
        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            replace(
                R.id.fcv_fragment_container,
                PlaceDetailFragment.newInstance(
                    viewModel.place.value ?: return,
                ),
            )
            addToBackStack(null)
        }
    }
}

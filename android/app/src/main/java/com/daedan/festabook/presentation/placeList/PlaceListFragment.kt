package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ) {
    private val viewModel by viewModels<PlaceListViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter =
            PlaceListAdapter().apply {
                submitList(DummyPlace.placeList)
            }

        setUpObservers()
    }

    override fun onPlaceClicked() {
        viewModel.publishClickEvent()
    }

    private fun setUpObservers() {
        viewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                PlaceListEvent.PLACE_CLICKED -> startPlaceDetailFragment()
                PlaceListEvent.RUNNING -> Unit
            }
        }
    }

    private fun startPlaceDetailFragment() {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fcv_fragment_container, PlaceDetailFragment())
            .addToBackStack(null)
            .commit()
    }
}

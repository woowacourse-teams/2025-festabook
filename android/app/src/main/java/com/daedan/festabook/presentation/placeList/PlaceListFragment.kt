package com.daedan.festabook.presentation.placeList

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter =
            PlaceListAdapter().apply {
                submitList(DummyPlace.placeList)
            }
    }
}

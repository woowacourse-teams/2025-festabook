package com.daedan.festabook.presentation.placeDetail

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.placeDetail.dummy.DummyNotice
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace

class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(R.layout.fragment_place_detail) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.place = DummyPlace.placeList[0]
        binding.rvPlaceNotice.adapter =
            PlaceNoticeAdapter().apply {
                submitList(DummyNotice.noticeList)
            }
    }
}

package com.daedan.festabook.presentation.placeDetail

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.placeDetail.dummy.DummyPlaceDetail
import com.daedan.festabook.presentation.placeList.uimodel.Place

class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(R.layout.fragment_place_detail) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val place = arguments?.getObject<Place>(TAG_PLACE_DETAIL_FRAGMENT) ?: return
        val dummyDetail = DummyPlaceDetail.create(place)
        binding.placeDetail = dummyDetail
        binding.rvPlaceNotice.adapter =
            PlaceNoticeAdapter().apply {
                submitList(dummyDetail.notices)
            }
        binding.vpPlaceImages.adapter = PlaceImageViewPagerAdapter(dummyDetail.images)
    }

    companion object {
        private const val TAG_PLACE_DETAIL_FRAGMENT = "placeDetailFragment"

        fun newInstance(place: Place): PlaceDetailFragment =
            PlaceDetailFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(TAG_PLACE_DETAIL_FRAGMENT, place)
                    }
            }
    }
}

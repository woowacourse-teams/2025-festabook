package com.daedan.festabook.presentation.placeDetail

import android.os.Bundle
import android.view.View
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceImageViewPagerAdapter
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceNoticeAdapter
import com.daedan.festabook.presentation.placeDetail.dummy.DummyPlaceDetail
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(R.layout.fragment_place_detail) {
    private val placeNoticeAdapter by lazy {
        PlaceNoticeAdapter()
    }

    private val placeImageAdapter by lazy {
        PlaceImageViewPagerAdapter()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val place = arguments?.getObject<PlaceUiModel>(TAG_PLACE_DETAIL_FRAGMENT) ?: return
        val dummyDetail = DummyPlaceDetail.create(place)
        setUpBinding(dummyDetail)
    }

    private fun setUpBinding(dummyDetail: PlaceDetailUiModel) {
        binding.placeDetail = dummyDetail
        binding.rvPlaceNotice.adapter = placeNoticeAdapter
        binding.vpPlaceImages.adapter = placeImageAdapter

        placeImageAdapter.submitList(dummyDetail.images)
        placeNoticeAdapter.submitList(dummyDetail.notices)
    }

    companion object {
        private const val TAG_PLACE_DETAIL_FRAGMENT = "placeDetailFragment"

        fun newInstance(place: PlaceUiModel): PlaceDetailFragment =
            PlaceDetailFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(TAG_PLACE_DETAIL_FRAGMENT, place)
                    }
            }
    }
}

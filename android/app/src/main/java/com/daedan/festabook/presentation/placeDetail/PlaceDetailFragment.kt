package com.daedan.festabook.presentation.placeDetail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceImageViewPagerAdapter
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceNoticeAdapter
import com.daedan.festabook.presentation.placeDetail.model.ImageUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(R.layout.fragment_place_detail) {
    private val placeNoticeAdapter by lazy {
        PlaceNoticeAdapter()
    }

    private val placeImageAdapter by lazy {
        PlaceImageViewPagerAdapter()
    }

    private lateinit var place: PlaceUiModel

    private val viewModel by viewModels<PlaceDetailViewModel> { PlaceDetailViewModel.factory(place) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        place = arguments?.getObject<PlaceUiModel>(TAG_PLACE_DETAIL_FRAGMENT) ?: return
        setUpBinding()
        setUpObserver()
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvPlaceNotice.adapter = placeNoticeAdapter
        binding.vpPlaceImages.adapter = placeImageAdapter
    }

    private fun setUpObserver() {
        viewModel.placeDetail.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PlaceDetailUiState.Error -> {}
                is PlaceDetailUiState.Loading -> {
                    Log.d("PlaceDetailFragment", "Loading")
                }
                is PlaceDetailUiState.Success -> {
                    loadPlaceDetail(result.placeDetail)
                }
            }
        }
    }

    private fun loadPlaceDetail(placeDetail: PlaceDetailUiModel) {
        binding.placeDetail = placeDetail

        if (placeDetail.images.isEmpty()) {
            placeImageAdapter.submitList(
                listOf(ImageUiModel()),
            )
        } else {
            placeImageAdapter.submitList(placeDetail.images)
        }

        if (placeDetail.notices.isEmpty()) {
            binding.rvPlaceNotice.visibility = View.GONE
            binding.tvNoNoticeDescription.visibility = View.VISIBLE
        } else {
            placeNoticeAdapter.submitList(placeDetail.notices)
        }
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

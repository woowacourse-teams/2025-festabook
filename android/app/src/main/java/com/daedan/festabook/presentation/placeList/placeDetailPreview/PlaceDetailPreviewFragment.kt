package com.daedan.festabook.presentation.placeList.placeDetailPreview

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import coil3.load
import coil3.request.placeholder
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailPreviewBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.PlaceListBottomSheetCallback
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.getValue

class PlaceDetailPreviewFragment :
    BaseFragment<FragmentPlaceDetailPreviewBinding>(
        R.layout.fragment_place_detail_preview,
    ) {
    private lateinit var selectedPlaceBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        selectedPlaceBottomSheetBehavior = BottomSheetBehavior.from(binding.nsSelectedPlace)

        val bottomSheetCallback = PlaceListBottomSheetCallback(viewModel)
        selectedPlaceBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        setUpObserver()
    }

    override fun onStart() {
        super.onStart()
        selectedPlaceBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setUpObserver() {
        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            binding.nsSelectedPlace.visibility =
                if (selectedPlace == SelectedPlaceUiState.Empty) View.GONE else View.VISIBLE

            when (selectedPlace) {
                is SelectedPlaceUiState.Loading -> {
                    binding.layoutSelectedPlace.visibility = View.INVISIBLE
                }
                is SelectedPlaceUiState.Success -> updateSelectedPlaceUi(selectedPlace.value)
                is SelectedPlaceUiState.Error -> showErrorSnackBar(selectedPlace.throwable)
                is SelectedPlaceUiState.Empty -> Unit
            }
        }
    }

    private fun updateSelectedPlaceUi(selectedPlace: PlaceDetailUiModel) {
        with(binding) {
            layoutSelectedPlace.visibility = View.VISIBLE
            tvSelectedPlaceTitle.text = selectedPlace.place.title
            tvSelectedPlaceLocation.text = selectedPlace.place.location
            tvSelectedPlaceTime.text = selectedPlace.operatingHours
            tvSelectedPlaceHost.text = selectedPlace.host
            tvSelectedPlaceDescription.text = selectedPlace.place.description

            tvSelectedPlaceCategory.text =
                when (selectedPlace.place.category) {
                    PlaceCategoryUiModel.FOOD_TRUCK -> getString(R.string.map_category_food_truck)
                    PlaceCategoryUiModel.BAR -> getString(R.string.map_category_bar)
                    PlaceCategoryUiModel.BOOTH -> getString(R.string.map_category_booth)
                    else -> ""
                }

            ivSelectedPlaceImage.load(
                selectedPlace.featuredImage,
            ) {
                placeholder(R.color.gray300)
            }
        }
    }
}

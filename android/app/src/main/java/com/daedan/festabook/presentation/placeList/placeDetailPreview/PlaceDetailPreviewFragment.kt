package com.daedan.festabook.presentation.placeList.placeDetailPreview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import coil3.load
import coil3.request.placeholder
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailPreviewBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
import kotlin.getValue

class PlaceDetailPreviewFragment :
    BaseFragment<FragmentPlaceDetailPreviewBinding>(
        R.layout.fragment_place_detail_preview,
    ),
    OnMenuItemReClickListener {
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setupBinding()
        setUpBackPressedCallback()
    }

    override fun onMenuItemReClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setupBinding() {
        binding.layoutSelectedPlace.setOnClickListener {
            val selectedPlaceState = viewModel.selectedPlace.value
            if (selectedPlaceState is SelectedPlaceUiState.Success) {
                startPlaceDetailActivity(selectedPlaceState.value.place)
            }
        }
    }

    private fun setUpBackPressedCallback() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.unselectPlace()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setUpObserver() {
        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            binding.layoutSelectedPlace.visibility =
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
            cvPlaceCategory.setCategory(selectedPlace.place.category)
            ivSelectedPlaceImage.load(
                selectedPlace.featuredImage,
            ) {
                placeholder(R.color.gray300)
            }
        }
    }

    private fun startPlaceDetailActivity(place: PlaceUiModel) {
        startActivity(PlaceDetailActivity.newIntent(requireContext(), place))
    }
}

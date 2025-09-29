package com.daedan.festabook.presentation.placeList.placeDetailPreview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailPreviewBinding
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.loadImage
import com.daedan.festabook.presentation.common.setFormatDate
import com.daedan.festabook.presentation.common.showBottomAnimation
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.logging.PlacePreviewClick
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState

class PlaceDetailPreviewFragment :
    BaseFragment<FragmentPlaceDetailPreviewBinding>(
        R.layout.fragment_place_detail_preview,
    ),
    OnMenuItemReClickListener {
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }
    private val backPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                viewModel.unselectPlace()
            }
        }

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
        viewModel.unselectPlace()
    }

    private fun setUpBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }

    private fun setupBinding() {
        binding.layoutSelectedPlace.setOnClickListener {
            val selectedPlaceState = viewModel.selectedPlace.value
            if (selectedPlaceState is SelectedPlaceUiState.Success) {
                startPlaceDetailActivity(selectedPlaceState.value)
                binding.logger.log(
                    PlacePreviewClick(
                        baseLogData = binding.logger.getBaseLogData(),
                        placeName = selectedPlaceState.value.place.title?:"undefined",
                        timeTag = viewModel.selectedTimeTag.value?.name?:"undefined",
                        category = selectedPlaceState.value.place.category.name
                    )
                )
            }
        }
    }

    private fun setUpObserver() {
        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            backPressedCallback.isEnabled = true
            binding.layoutSelectedPlace.visibility =
                if (selectedPlace == SelectedPlaceUiState.Empty) View.GONE else View.VISIBLE

            when (selectedPlace) {
                is SelectedPlaceUiState.Loading -> Unit
                is SelectedPlaceUiState.Success -> {
                    binding.layoutSelectedPlace.showBottomAnimation()
                    updateSelectedPlaceUi(selectedPlace.value)
                }

                is SelectedPlaceUiState.Error -> showErrorSnackBar(selectedPlace.throwable)
                is SelectedPlaceUiState.Empty -> backPressedCallback.isEnabled = false
            }
        }
    }

    private fun updateSelectedPlaceUi(selectedPlace: PlaceDetailUiModel) {
        with(binding) {
            layoutSelectedPlace.visibility = View.VISIBLE
            tvSelectedPlaceTitle.text =
                selectedPlace.place.title ?: getString(R.string.place_list_default_title)
            tvSelectedPlaceLocation.text =
                selectedPlace.place.location ?: getString(R.string.place_list_default_location)
            setFormatDate(
                binding.tvSelectedPlaceTime,
                selectedPlace.startTime,
                selectedPlace.endTime,
            )
            tvSelectedPlaceHost.text =
                selectedPlace.host ?: getString(R.string.place_detail_default_host)
            tvSelectedPlaceDescription.text = selectedPlace.place.description
                ?: getString(R.string.place_list_default_description)
            cvPlaceCategory.setCategory(selectedPlace.place.category)
            ivSelectedPlaceImage.loadImage(selectedPlace.featuredImage)
        }
    }

    private fun startPlaceDetailActivity(placeDetail: PlaceDetailUiModel) {
        startActivity(PlaceDetailActivity.newIntent(requireContext(), placeDetail))
    }
}

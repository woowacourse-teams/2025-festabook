package com.daedan.festabook.presentation.placeList.placeDetailPreview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.viewModels
import coil3.load
import coil3.request.placeholder
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailPreviewBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.setFormatDate
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
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
                startPlaceDetailActivity(selectedPlaceState.value)
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
                is SelectedPlaceUiState.Secondary -> updateSelectedSecondaryPlaceUi(selectedPlace.category)
            }
        }
    }

    private fun updateSelectedPlaceUi(selectedPlace: PlaceDetailUiModel) {
        with(binding) {
            layoutSelectedPlace.visibility = View.VISIBLE
            makeChildVisible()

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
            ivSelectedPlaceImage.load(
                selectedPlace.featuredImage,
            ) {
                placeholder(R.color.gray300)
            }
        }
    }

    private fun updateSelectedSecondaryPlaceUi(placeCategory: PlaceCategoryUiModel) {
        with(binding) {
            layoutSelectedPlace.visibility = View.VISIBLE
            makeChildGone()
            tvSelectedPlaceTitle.visibility = View.VISIBLE

            tvSelectedPlaceTitle.text =
                when (placeCategory) {
                    PlaceCategoryUiModel.TRASH_CAN -> getString(R.string.map_category_trash)
                    PlaceCategoryUiModel.TOILET -> getString(R.string.map_category_toilet)
                    PlaceCategoryUiModel.SMOKING_AREA -> getString(R.string.map_category_smoking_area)
                    PlaceCategoryUiModel.PARKING -> getString(R.string.map_category_parking)
                    PlaceCategoryUiModel.PRIMARY -> getString(R.string.map_category_primary)
                    PlaceCategoryUiModel.STAGE -> getString(R.string.map_category_stage)
                    else -> return
                }
        }
    }

    private fun FragmentPlaceDetailPreviewBinding.makeChildGone() {
        layoutSelectedPlace.children.forEach {
            it.visibility = View.GONE
        }
    }

    private fun FragmentPlaceDetailPreviewBinding.makeChildVisible() {
        layoutSelectedPlace.children.forEach {
            it.visibility = View.VISIBLE
        }
    }

    private fun startPlaceDetailActivity(placeDetail: PlaceDetailUiModel) {
        startActivity(PlaceDetailActivity.newIntent(requireContext(), placeDetail))
    }
}

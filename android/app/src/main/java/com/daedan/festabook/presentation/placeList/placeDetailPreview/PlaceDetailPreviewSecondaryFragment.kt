package com.daedan.festabook.presentation.placeList.placeDetailPreview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.viewModels
import coil3.load
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailPreviewSecondaryBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
import com.daedan.festabook.presentation.placeList.model.getIconId
import com.daedan.festabook.presentation.placeList.model.getTextId
import kotlin.getValue

class PlaceDetailPreviewSecondaryFragment :
    BaseFragment<FragmentPlaceDetailPreviewSecondaryBinding>(R.layout.fragment_place_detail_preview_secondary),
    OnMenuItemReClickListener {
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }
    private val backPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.unselectPlace()
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBackPressedCallback()
        setUpObserver()
    }

    override fun onMenuItemReClick() {
        viewModel.unselectPlace()
    }

    private fun setUpBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    private fun removeBackPressedCallback() {
        backPressedCallback.remove()
    }

    private fun setUpObserver() {
        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            when (selectedPlace) {
                is SelectedPlaceUiState.Success -> {
                    binding.makeChildVisible()
                    updateSelectedPlaceUi(selectedPlace.value)
                }
                is SelectedPlaceUiState.Error -> showErrorSnackBar(selectedPlace.throwable)
                is SelectedPlaceUiState.Loading -> binding.makeChildInvisible()
                is SelectedPlaceUiState.Empty -> removeBackPressedCallback()
            }
        }
    }

    private fun updateSelectedPlaceUi(selectedPlace: PlaceDetailUiModel) {
        with(binding) {
            ivSecondaryCategoryItem.load(selectedPlace.place.category.getIconId())
            tvSelectedPlaceTitle.text =
                selectedPlace.place.title ?: getString(selectedPlace.place.category.getTextId())
        }
    }

    private fun FragmentPlaceDetailPreviewSecondaryBinding.makeChildInvisible() {
        layoutSelectedPlace.children.forEach {
            it.visibility = View.INVISIBLE
        }
    }

    private fun FragmentPlaceDetailPreviewSecondaryBinding.makeChildVisible() {
        layoutSelectedPlace.children.forEach {
            it.visibility = View.VISIBLE
        }
    }
}

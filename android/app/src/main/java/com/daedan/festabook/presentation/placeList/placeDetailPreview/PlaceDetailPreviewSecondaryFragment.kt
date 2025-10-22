package com.daedan.festabook.presentation.placeList.placeDetailPreview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import coil3.load
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceDetailPreviewSecondaryBinding
import com.daedan.festabook.di.fragment.FragmentKey
import com.daedan.festabook.di.viewmodel.metroViewModels
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.showBottomAnimation
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.PlaceListFragment
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.logging.PlacePreviewClick
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
import com.daedan.festabook.presentation.placeList.model.getIconId
import com.daedan.festabook.presentation.placeList.model.getTextId
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoMap(scope = AppScope::class, binding = binding<Fragment>())
@FragmentKey(PlaceListFragment::class)
@Inject
class PlaceDetailPreviewSecondaryFragment :
    BaseFragment<FragmentPlaceDetailPreviewSecondaryBinding>(),
    OnMenuItemReClickListener {
    override val layoutId: Int = R.layout.fragment_place_detail_preview_secondary
    private val viewModel: PlaceListViewModel by metroViewModels { requireParentFragment() }
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
        setUpObserver()
        setUpBackPressedCallback()
    }

    override fun onMenuItemReClick() {
        viewModel.unselectPlace()
    }

    private fun setUpBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback,
        )
    }

    private fun setUpObserver() {
        viewModel.selectedPlace.observe(viewLifecycleOwner) { selectedPlace ->
            backPressedCallback.isEnabled = true
            when (selectedPlace) {
                is SelectedPlaceUiState.Success -> {
                    binding.layoutSelectedPlace.visibility = View.VISIBLE
                    binding.layoutSelectedPlace.showBottomAnimation()
                    updateSelectedPlaceUi(selectedPlace.value)
                    binding.logger.log(
                        PlacePreviewClick(
                            baseLogData = binding.logger.getBaseLogData(),
                            placeName = selectedPlace.value.place.title ?: "undefined",
                            timeTag = viewModel.selectedTimeTag.value?.name ?: "undefined",
                            category = selectedPlace.value.place.category.name,
                        ),
                    )
                }

                is SelectedPlaceUiState.Error -> showErrorSnackBar(selectedPlace.throwable)
                is SelectedPlaceUiState.Loading -> Unit
                is SelectedPlaceUiState.Empty -> backPressedCallback.isEnabled = false
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
}

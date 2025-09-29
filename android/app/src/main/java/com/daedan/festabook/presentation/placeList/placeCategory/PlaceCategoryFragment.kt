package com.daedan.festabook.presentation.placeList.placeCategory

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceCategoryBinding
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.logging.LocationPermissionChanged
import com.daedan.festabook.presentation.placeList.logging.PlaceCategoryClick
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.google.android.material.chip.Chip

class PlaceCategoryFragment : BaseFragment<FragmentPlaceCategoryBinding>(R.layout.fragment_place_category) {
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
    }

    private fun setUpBinding() {
        binding.cgCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedCategories =
                checkedIds.mapNotNull {
                    val category = group.findViewById<Chip>(it).tag
                    category as? PlaceCategoryUiModel
                }

            viewModel.unselectPlace()
            viewModel.setSelectedCategories(selectedCategories)
            binding.chipCategoryAll.isChecked = selectedCategories.isEmpty()
            binding.logger.log(
                PlaceCategoryClick(
                    baseLogData = binding.logger.getBaseLogData(),
                    currentCategories = selectedCategories.joinToString(",") { it.toString() }
                )
            )
        }

        setUpChipCategoryAllListener()
    }

    private fun setUpChipCategoryAllListener() {
        binding.chipCategoryAll.setOnClickListener {
            binding.cgCategories.children.forEach {
                val chip = (it as? Chip) ?: return@forEach
                chip.isChecked = chip.id == binding.chipCategoryAll.id
            }
        }
    }
}

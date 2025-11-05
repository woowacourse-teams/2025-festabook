package com.daedan.festabook.presentation.placeMap.placeCategory

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceCategoryBinding
import com.daedan.festabook.di.fragment.FragmentKey
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.placeMap.PlaceMapViewModel
import com.daedan.festabook.presentation.placeMap.logging.PlaceCategoryClick
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.google.android.material.chip.Chip
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoMap(scope = AppScope::class, binding = binding<Fragment>())
@FragmentKey(PlaceCategoryFragment::class)
@Inject
class PlaceCategoryFragment : BaseFragment<FragmentPlaceCategoryBinding>() {
    override val layoutId: Int = R.layout.fragment_place_category

    @Inject
    override lateinit var defaultViewModelProviderFactory: ViewModelProvider.Factory
    private val viewModel: PlaceMapViewModel by viewModels({ requireParentFragment() })

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
                    currentCategories = selectedCategories.joinToString(",") { it.toString() },
                ),
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

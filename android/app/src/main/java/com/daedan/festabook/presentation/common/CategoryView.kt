package com.daedan.festabook.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.daedan.festabook.R
import com.daedan.festabook.databinding.CategoryViewBinding
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt

class CategoryView(
    context: Context,
    attrs: AttributeSet? = null,
) : MaterialCardView(context, attrs) {
    private val binding: CategoryViewBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.category_view,
            this,
            true,
        )

    init {
        radius = 6f
        cardElevation = 0f
        strokeWidth = 0
    }

    fun setCategory(category: PlaceCategoryUiModel) {
        when (category) {
            PlaceCategoryUiModel.FOOD_TRUCK -> {
                binding.tvPlaceCategory.text =
                    context.getString(R.string.place_list_title_food_truck)
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_map_category_food_truck)
                binding.cvPlaceCategory.setCardBackgroundColor(
                    getAlphaColor(R.color.green400),
                )
            }

            PlaceCategoryUiModel.BAR -> {
                binding.tvPlaceCategory.text =
                    context.getString(R.string.place_list_title_bar)
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_map_category_bar)
                binding.cvPlaceCategory.setCardBackgroundColor(
                    getAlphaColor(R.color.orange400),
                )
            }

            PlaceCategoryUiModel.BOOTH -> {
                binding.tvPlaceCategory.text =
                    context.getString(R.string.place_list_title_booth)
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_map_category_booth)
                binding.cvPlaceCategory.setCardBackgroundColor(
                    getAlphaColor(R.color.blue400),
                )
            }

            PlaceCategoryUiModel.SMOKING_AREA, PlaceCategoryUiModel.TOILET, PlaceCategoryUiModel.TRASH_CAN -> Unit
        }
    }

    private fun getAlphaColor(
        @ColorRes colorRes: Int,
    ): Int {
        val original = context.getColor(colorRes)
        val alpha = (MAX_ALPHA * ALPHA_RATIO).roundToInt()
        return ColorUtils.setAlphaComponent(original, alpha)
    }

    companion object {
        private const val MAX_ALPHA = 255
        private const val ALPHA_RATIO = 0.10f

        @JvmStatic
        @BindingAdapter("category")
        fun setCategory(
            view: CategoryView,
            category: PlaceCategoryUiModel?,
        ) {
            category ?: return
            view.setCategory(category)
        }
    }
}

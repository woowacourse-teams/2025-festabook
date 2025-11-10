package com.daedan.festabook.presentation.placeMap.placeList

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import com.daedan.festabook.R
import com.daedan.festabook.databinding.CategoryViewBinding
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt

class CategoryView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : MaterialCardView(context, attrs, defStyleAttr) {
        private val binding: CategoryViewBinding =
            CategoryViewBinding.inflate(LayoutInflater.from(context), this, true)

        init {
            radius = 12f
            cardElevation = 0f
            strokeWidth = 0
        }

        fun setCategory(category: PlaceCategoryUiModel) {
            when (category) {
                PlaceCategoryUiModel.FOOD_TRUCK -> {
                    updateUI(
                        R.string.place_list_title_food_truck,
                        R.drawable.ic_map_category_food_truck,
                        R.color.green400,
                    )
                }

                PlaceCategoryUiModel.BAR -> {
                    updateUI(
                        R.string.place_list_title_bar,
                        R.drawable.ic_map_category_bar,
                        R.color.orange400,
                    )
                }

                PlaceCategoryUiModel.BOOTH -> {
                    updateUI(
                        R.string.place_list_title_booth,
                        R.drawable.ic_map_category_booth,
                        R.color.blue400,
                    )
                }

                else -> Unit
            }
        }

        private fun updateUI(
            titleResId: Int,
            iconResId: Int,
            colorResId: Int,
        ) {
            binding.tvPlaceCategory.text = context.getString(titleResId)
            binding.ivCategoryIcon.setImageResource(iconResId)
            setCardBackgroundColor(getAlphaColor(colorResId))
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

package com.daedan.festabook.presentation.news.lost.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.daedan.festabook.databinding.ItemLostBinding
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel

class LostItemViewHolder(
    val binding: ItemLostBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: LostItemUiModel) {
        binding.ivLostItem.load(item.imageUrl) {
            transformations(RoundedCornersTransformation(LOST_ITEM_IMAGE_RADIUS))
        }
    }

    companion object {
        private const val LOST_ITEM_IMAGE_RADIUS = 16f

        fun from(parent: ViewGroup): LostItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemLostBinding.inflate(inflater, parent, false)
            return LostItemViewHolder(binding)
        }
    }
}

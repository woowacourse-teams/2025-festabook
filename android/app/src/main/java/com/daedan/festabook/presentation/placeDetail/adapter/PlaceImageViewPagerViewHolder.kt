package com.daedan.festabook.presentation.placeDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemPlaceImageBinding
import com.daedan.festabook.presentation.placeDetail.model.ImageUiModel

class PlaceImageViewPagerViewHolder(
    val binding: ItemPlaceImageBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(imageUiModel: ImageUiModel) {
        binding.image = imageUiModel
    }

    companion object {
        fun from(parent: ViewGroup): PlaceImageViewPagerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaceImageBinding.inflate(layoutInflater, parent, false)
            return PlaceImageViewPagerViewHolder(binding)
        }
    }
}

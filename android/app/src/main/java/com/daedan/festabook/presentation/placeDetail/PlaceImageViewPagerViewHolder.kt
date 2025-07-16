package com.daedan.festabook.presentation.placeDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemPlaceImageBinding

class PlaceImageViewPagerViewHolder private constructor(
    val binding: ItemPlaceImageBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(imageUrl: String) {
        binding.imageUrl = imageUrl
    }

    companion object {
        fun of(parent: ViewGroup): PlaceImageViewPagerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaceImageBinding.inflate(layoutInflater, parent, false)
            return PlaceImageViewPagerViewHolder(binding)
        }
    }
}

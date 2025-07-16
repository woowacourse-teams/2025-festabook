package com.daedan.festabook.presentation.placeDetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PlaceImageViewPagerAdapter(
    private val imageUrls: List<String>,
) : RecyclerView.Adapter<PlaceImageViewPagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceImageViewPagerViewHolder = PlaceImageViewPagerViewHolder.of(parent)

    override fun onBindViewHolder(
        holder: PlaceImageViewPagerViewHolder,
        position: Int,
    ) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size
}

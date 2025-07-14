package com.daedan.festabook.presentation.placeList

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.placeList.uimodel.Place

class PlaceListAdapter :
    ListAdapter<Place, PlaceListViewHolder>(
        object : DiffUtil.ItemCallback<Place>() {
            override fun areItemsTheSame(
                oldItem: Place,
                newItem: Place,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Place,
                newItem: Place,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceListViewHolder =
        when (viewType) {
            PlaceListViewHolder.VIEW_TYPE_HEADER -> PlaceListViewHolder.Header.of(parent)
            PlaceListViewHolder.VIEW_TYPE_ITEM -> PlaceListViewHolder.PlaceViewHolder.of(parent)
            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun onBindViewHolder(
        holder: PlaceListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        if (position == 0) {
            PlaceListViewHolder.VIEW_TYPE_HEADER
        } else {
            PlaceListViewHolder.VIEW_TYPE_ITEM
        }
}

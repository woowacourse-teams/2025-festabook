package com.daedan.festabook.presentation.placeList

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.placeList.PlaceListViewHolder.Header
import com.daedan.festabook.presentation.placeList.PlaceListViewHolder.PlaceViewHolder
import com.daedan.festabook.presentation.placeList.uimodel.Place

class PlaceListAdapter : ListAdapter<Place, PlaceListViewHolder>(DIFF_UTIL) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceListViewHolder =
        when (PlaceListViewHolder.ViewType.find(viewType)) {
            PlaceListViewHolder.ViewType.HEADER -> Header.of(parent)
            PlaceListViewHolder.ViewType.ITEM -> PlaceViewHolder.of(parent)
        }

    override fun onBindViewHolder(
        holder: PlaceListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        if (position == POSITION_HEADER) {
            Header.VIEW_TYPE
        } else {
            PlaceViewHolder.VIEW_TYPE
        }

    companion object {
        private const val POSITION_HEADER = 0
        private val DIFF_UTIL =
            object : DiffUtil.ItemCallback<Place>() {
                override fun areItemsTheSame(
                    oldItem: Place,
                    newItem: Place,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Place,
                    newItem: Place,
                ): Boolean = oldItem == newItem
            }
    }
}

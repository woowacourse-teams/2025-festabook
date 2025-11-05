package com.daedan.festabook.presentation.placeMap.placeList.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import com.daedan.festabook.presentation.placeMap.placeList.PlaceClickListener

class PlaceListAdapter(
    private val handler: PlaceClickListener,
) : ListAdapter<PlaceUiModel, PlaceViewHolder>(DIFF_UTIL) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceViewHolder = PlaceViewHolder.from(parent, handler)

    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_UTIL =
            object : DiffUtil.ItemCallback<PlaceUiModel>() {
                override fun areItemsTheSame(
                    oldItem: PlaceUiModel,
                    newItem: PlaceUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: PlaceUiModel,
                    newItem: PlaceUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

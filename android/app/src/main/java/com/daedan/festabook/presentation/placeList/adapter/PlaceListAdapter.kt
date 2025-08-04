package com.daedan.festabook.presentation.placeList.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.placeList.PlaceClickListener
import com.daedan.festabook.presentation.placeList.adapter.PlaceListViewHolder.Header
import com.daedan.festabook.presentation.placeList.adapter.PlaceListViewHolder.PlaceViewHolder
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

class PlaceListAdapter(
    private val handler: PlaceClickListener,
) : ListAdapter<PlaceUiModel, PlaceListViewHolder>(DIFF_UTIL) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceListViewHolder =
        when (PlaceListViewHolder.ViewType.find(viewType)) {
            PlaceListViewHolder.ViewType.HEADER -> Header.from(parent)
            PlaceListViewHolder.ViewType.ITEM -> PlaceViewHolder.from(parent, handler)
        }

    override fun onBindViewHolder(
        holder: PlaceListViewHolder,
        position: Int,
    ) {
        if (position == POSITION_HEADER) return
        holder.bind(getItem(position - 1))
    }

    override fun getItemViewType(position: Int): Int =
        if (position == POSITION_HEADER) {
            Header.VIEW_TYPE
        } else {
            PlaceViewHolder.VIEW_TYPE
        }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()
        return if (itemCount > 0) itemCount + 1 else itemCount
    }

    companion object {
        private const val POSITION_HEADER = 0
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

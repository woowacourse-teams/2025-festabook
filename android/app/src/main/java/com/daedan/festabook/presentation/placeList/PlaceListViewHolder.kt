package com.daedan.festabook.presentation.placeList

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.daedan.festabook.databinding.ItemPlaceListBinding
import com.daedan.festabook.databinding.ItemPlaceListHeaderBinding
import com.daedan.festabook.presentation.placeList.uimodel.Place

sealed class PlaceListViewHolder(
    binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(place: Place)

    class PlaceViewHolder private constructor(
        private val binding: ItemPlaceListBinding,
    ) : PlaceListViewHolder(binding) {
        override fun bind(place: Place) {
            binding.place = place
        }

        companion object {
            val VIEW_TYPE = 0

            fun of(parent: ViewGroup): PlaceViewHolder =
                PlaceViewHolder(
                    ItemPlaceListBinding.inflate(
                        android.view.LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                )
        }
    }

    class Header private constructor(
        binding: ItemPlaceListHeaderBinding,
    ) : PlaceListViewHolder(binding) {
        override fun bind(place: Place) = Unit

        companion object {
            val VIEW_TYPE = 1

            fun of(parent: ViewGroup): Header =
                Header(
                    ItemPlaceListHeaderBinding.inflate(
                        android.view.LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                )
        }
    }

    enum class ViewType {
        HEADER,
        ITEM,
        ;

        companion object {
            fun find(viewType: Int): ViewType =
                when (viewType) {
                    Header.VIEW_TYPE -> HEADER
                    PlaceViewHolder.VIEW_TYPE -> ITEM
                    else -> throw IllegalArgumentException("Invalid view type")
                }
        }
    }
}

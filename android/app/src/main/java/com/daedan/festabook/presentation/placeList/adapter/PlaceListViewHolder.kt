package com.daedan.festabook.presentation.placeList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.daedan.festabook.databinding.ItemPlaceListBinding
import com.daedan.festabook.databinding.ItemPlaceListHeaderBinding
import com.daedan.festabook.presentation.placeList.PlaceClickListener
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

sealed class PlaceListViewHolder(
    binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(placeUiModel: PlaceUiModel)

    class PlaceViewHolder private constructor(
        private val binding: ItemPlaceListBinding,
        private val handler: PlaceClickListener,
    ) : PlaceListViewHolder(binding) {
        override fun bind(placeUiModel: PlaceUiModel) {
            binding.place = placeUiModel
            binding.handler = handler
        }

        companion object {
            const val VIEW_TYPE = 0

            fun from(
                parent: ViewGroup,
                handler: PlaceClickListener,
            ): PlaceViewHolder =
                PlaceViewHolder(
                    ItemPlaceListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                    handler,
                )
        }
    }

    class Header private constructor(
        binding: ItemPlaceListHeaderBinding,
    ) : PlaceListViewHolder(binding) {
        override fun bind(place: PlaceUiModel) = Unit

        companion object {
            const val VIEW_TYPE = 1

            fun from(parent: ViewGroup): Header =
                Header(
                    ItemPlaceListHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
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

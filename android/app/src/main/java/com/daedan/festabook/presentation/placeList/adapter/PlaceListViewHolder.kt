package com.daedan.festabook.presentation.placeList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemPlaceListBinding
import com.daedan.festabook.presentation.placeList.PlaceClickListener
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

class PlaceViewHolder private constructor(
    private val binding: ItemPlaceListBinding,
    private val handler: PlaceClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(placeUiModel: PlaceUiModel) {
        binding.place = placeUiModel
        binding.handler = handler
    }

    companion object {
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

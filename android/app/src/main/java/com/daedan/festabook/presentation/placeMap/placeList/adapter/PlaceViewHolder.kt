package com.daedan.festabook.presentation.placeMap.placeList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemPlaceListBinding
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import com.daedan.festabook.presentation.placeMap.placeList.OnPlaceClickListener

class PlaceViewHolder private constructor(
    private val binding: ItemPlaceListBinding,
    private val listener: OnPlaceClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(placeUiModel: PlaceUiModel) {
        binding.place = placeUiModel
        binding.listener = listener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: OnPlaceClickListener,
        ): PlaceViewHolder =
            PlaceViewHolder(
                ItemPlaceListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                listener,
            )
    }
}

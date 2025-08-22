package com.daedan.festabook.presentation.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import coil3.request.CachePolicy
import coil3.request.placeholder
import com.daedan.festabook.databinding.ItemHomeLineupBinding
import com.daedan.festabook.presentation.common.loadImage
import com.daedan.festabook.presentation.home.LineupItemUiModel

class LineupItemViewHolder(
    val binding: ItemHomeLineupBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(lineupItemUiModel: LineupItemUiModel) {
        binding.itemLineupImage.loadImage(lineupItemUiModel.imageUrl) {
            placeholder(Color.LTGRAY.toDrawable())
            memoryCachePolicy(CachePolicy.ENABLED)
        }
        binding.tvLineupName.text = lineupItemUiModel.name
    }

    companion object {
        fun from(parent: ViewGroup): LineupItemViewHolder {
            val binding = ItemHomeLineupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LineupItemViewHolder(binding)
        }
    }
}

package com.daedan.festabook.presentation.news.lost.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.daedan.festabook.databinding.ItemLostBinding
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class LostItemViewHolder(
    private val binding: ItemLostBinding,
    private val onNewsClickListener: OnNewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: LostItemUiModel) {
        binding.root.setOnClickListener {
            onNewsClickListener.onLostItemClick(item)
        }
        binding.ivLostItem.load(item.imageUrl) {
            transformations(
                RoundedCornersTransformation(
                    LOST_ITEM_IMAGE_RADIUS.toPx(binding.ivLostItem.context).toFloat(),
                ),
            )
        }
    }

    companion object {
        private const val LOST_ITEM_IMAGE_RADIUS: Int = 16

        fun from(
            parent: ViewGroup,
            onNewsClickListener: OnNewsClickListener,
        ): LostItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemLostBinding.inflate(inflater, parent, false)
            return LostItemViewHolder(binding, onNewsClickListener)
        }
    }
}

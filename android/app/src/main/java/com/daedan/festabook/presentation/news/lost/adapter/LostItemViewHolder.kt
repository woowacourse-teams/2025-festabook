package com.daedan.festabook.presentation.news.lost.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.daedan.festabook.databinding.ItemLostBinding
import com.daedan.festabook.presentation.common.loadImage
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class LostItemViewHolder private constructor(
    private val binding: ItemLostBinding,
    private val onNewsClickListener: OnNewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var lostItem: LostItemUiModel? = null

    init {
        binding.root.setOnClickListener {
            lostItem
                ?.let {
                    onNewsClickListener.onLostItemClick(it)
                }
        }
    }

    fun bind(item: LostItemUiModel) {
        lostItem = item
        binding.ivLostItem.loadImage(item.imageUrl) {
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

package com.daedan.festabook.presentation.news.lost.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemLostBinding
import com.daedan.festabook.presentation.common.loadImage
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import timber.log.Timber

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
                } ?: run {
                Timber.w("${this::class.java.simpleName} LostItem이 null입니다.")
            }
        }
    }

    fun bind(item: LostItemUiModel) {
        lostItem = item
        binding.ivLostItem.loadImage(item.imageUrl)
    }

    companion object {
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

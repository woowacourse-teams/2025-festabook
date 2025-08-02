package com.daedan.festabook.presentation.news.faq.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class FAQAdapter(
    private val onNewsClickListener: OnNewsClickListener,
) : ListAdapter<FAQItemUiModel, FAQViewHolder>(FAQItemDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FAQViewHolder = FAQViewHolder.from(parent, onNewsClickListener)

    override fun onBindViewHolder(
        holder: FAQViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val FAQItemDiffCallback =
            object : DiffUtil.ItemCallback<FAQItemUiModel>() {
                override fun areItemsTheSame(
                    oldItem: FAQItemUiModel,
                    newItem: FAQItemUiModel,
                ): Boolean = oldItem.questionId == newItem.questionId

                override fun areContentsTheSame(
                    oldItem: FAQItemUiModel,
                    newItem: FAQItemUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

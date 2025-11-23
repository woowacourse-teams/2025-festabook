package com.daedan.festabook.presentation.news.faq.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.NewsClickListener

class FAQAdapter(
    private val newsClickListener: NewsClickListener,
) : ListAdapter<FAQItemUiModel, FAQViewHolder>(faqItemDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FAQViewHolder = FAQViewHolder.from(parent, newsClickListener)

    override fun onBindViewHolder(
        holder: FAQViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val faqItemDiffCallback =
            object : DiffUtil.ItemCallback<FAQItemUiModel>() {
                override fun areItemsTheSame(
                    oldItem: FAQItemUiModel,
                    newItem: FAQItemUiModel,
                ): Boolean = newItem.questionId == oldItem.questionId

                override fun areContentsTheSame(
                    oldItem: FAQItemUiModel,
                    newItem: FAQItemUiModel,
                ): Boolean = newItem == oldItem
            }
    }
}

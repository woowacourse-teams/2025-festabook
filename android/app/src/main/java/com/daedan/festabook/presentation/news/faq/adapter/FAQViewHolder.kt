package com.daedan.festabook.presentation.news.faq.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemFaqBinding
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel

class FAQViewHolder(
    private val binding: ItemFaqBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FAQItemUiModel) {
        binding.faqItem = item
    }

    companion object {
        fun from(parent: ViewGroup): FAQViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFaqBinding.inflate(inflater, parent, false)

            return FAQViewHolder(binding)
        }
    }
}

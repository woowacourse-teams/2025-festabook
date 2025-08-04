package com.daedan.festabook.presentation.news.faq.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemFaqBinding
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class FAQViewHolder(
    private val binding: ItemFaqBinding,
    private val onNewsClickListener: OnNewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FAQItemUiModel) {
        binding.clFaqItem.setOnClickListener {
            onNewsClickListener.onFAQClick(item)
        }
        binding.tvFaqQuestion.text =
            binding.tvFaqQuestion.context.getString(R.string.tab_faq_question, item.question)
        binding.tvFaqAnswer.text = item.answer
        binding.ivFaqExpand.setImageResource(if (item.isExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down)
        binding.tvFaqAnswer.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onNewsClickListener: OnNewsClickListener,
        ): FAQViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFaqBinding.inflate(inflater, parent, false)

            return FAQViewHolder(binding, onNewsClickListener)
        }
    }
}

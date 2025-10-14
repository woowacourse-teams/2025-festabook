package com.daedan.festabook.presentation.news.faq.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.daedan.festabook.databinding.ItemFaqBinding
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener
import timber.log.Timber

class FAQViewHolder(
    private val binding: ItemFaqBinding,
    private val onNewsClickListener: OnNewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var faqItem: FAQItemUiModel? = null

    init {
        binding.root.setOnClickListener {
            faqItem?.let {
                onNewsClickListener.onFAQClick(it)
            } ?: run {
                Timber.w("${this::class.java.simpleName} : FAQ 아이템이 null입니다.")
            }
        }
    }

    fun bind(faqItem: FAQItemUiModel) {
        this.faqItem = faqItem
        setupTopMargin()
        binding.tvFaqQuestion.text = faqItem.question
        binding.tvFaqAnswer.text = faqItem.answer
        binding.tvFaqAnswer.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(binding.clFaqItem, AutoTransition())

        val targetRotation =
            if (faqItem.isExpanded) ICON_ROTATION_EXPANDED else ICON_ROTATION_COLLAPSED
        binding.ivFaqExpand
            .animate()
            .rotation(targetRotation)
            .setDuration(ICON_DURATION)
            .start()
    }

    private fun setupTopMargin() {
        val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (layoutPosition == 0) {
            layoutParams.topMargin = TOP_MARGIN.toPx(binding.clFaqItem.context)
        } else {
            layoutParams.topMargin = 0
        }
    }

    companion object {
        private const val TOP_MARGIN: Int = 12
        private const val ICON_ROTATION_EXPANDED: Float = 180F
        private const val ICON_ROTATION_COLLAPSED: Float = 0F
        private const val ICON_DURATION: Long = 300L

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

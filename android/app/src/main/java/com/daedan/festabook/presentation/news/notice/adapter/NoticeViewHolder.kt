package com.daedan.festabook.presentation.news.notice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import timber.log.Timber

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
    private val onNewsClickListener: OnNewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var noticeItem: NoticeUiModel? = null

    init {
        binding.layoutNoticeItem.setOnClickListener {
            noticeItem?.let {
                onNewsClickListener.onNoticeClick(it)
            } ?: run {
                Timber.w("${this::class.java.simpleName} 공지 아이템이 null입니다.")
            }
        }
    }

    fun bind(notice: NoticeUiModel) {
        setupTopMargin()
        noticeItem = notice
        binding.notice = notice
        binding.tvNoticeDescription.visibility =
            if (notice.isExpanded) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(binding.layoutNoticeItem, AutoTransition())

        if (notice.isPinned) {
            binding.ivNoticeIcon.setImageResource(R.drawable.ic_pin)
            binding.layoutNoticeItem.setBackgroundResource(R.drawable.bg_gray100_stroke_gray200_radius_16dp)
        } else {
            binding.ivNoticeIcon.setImageResource(R.drawable.ic_speaker)
            binding.layoutNoticeItem.setBackgroundResource(R.drawable.bg_stroke_gray200_radius_16dp)
        }
    }

    private fun setupTopMargin() {
        val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (layoutPosition == 0) {
            layoutParams.topMargin = TOP_MARGIN.toPx(binding.layoutNoticeItem.context)
        } else {
            layoutParams.topMargin = 0
        }
    }

    companion object {
        private const val TOP_MARGIN = 8

        fun from(
            parent: ViewGroup,
            listener: OnNewsClickListener,
        ): NoticeViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoticeViewHolder(binding, listener)
        }
    }
}

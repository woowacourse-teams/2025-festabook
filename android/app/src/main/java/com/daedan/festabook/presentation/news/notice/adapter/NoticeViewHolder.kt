package com.daedan.festabook.presentation.news.notice.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
    private val onNewsClickListener: OnNewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notice: NoticeUiModel) {
        binding.notice = notice

        binding.layoutNoticeItem.setOnClickListener {
            onNewsClickListener.onNoticeClick(notice)
        }
        if (notice.isPinned) {
            binding.ivNoticeIcon.setImageResource(R.drawable.ic_pin)
            binding.layoutNoticeItem.setBackgroundResource(R.drawable.bg_gray100_stroke_gray400_radius_10dp)
        } else {
            binding.ivNoticeIcon.setImageResource(R.drawable.ic_speaker)
            binding.layoutNoticeItem.setBackgroundResource(R.drawable.bg_stroke_gray400_radius_10dp)
        }

        binding.tvNoticeDescription.maxLines =
            if (notice.isExpanded) Integer.MAX_VALUE else DEFAULT_LINE_COUNT
        binding.tvNoticeDescription.ellipsize =
            if (notice.isExpanded) null else TextUtils.TruncateAt.END
    }

    companion object {
        private const val DEFAULT_LINE_COUNT = 2

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

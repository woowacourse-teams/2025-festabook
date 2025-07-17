package com.daedan.festabook.presentation.news.notice.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
    noticeItemClickListener: OnNoticeClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = noticeItemClickListener
    }

    fun bind(notice: NoticeUiModel) {
        binding.notice = notice

        binding.tvNoticeDescription.maxLines =
            if (notice.isExpanded) Integer.MAX_VALUE else DEFAULT_LINE_COUNT
        binding.tvNoticeDescription.ellipsize =
            if (notice.isExpanded) null else TextUtils.TruncateAt.END
    }

    companion object {
        private const val DEFAULT_LINE_COUNT = 2

        fun from(
            parent: ViewGroup,
            listener: OnNoticeClickListener,
        ): NoticeViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoticeViewHolder(binding, listener)
        }
    }
}

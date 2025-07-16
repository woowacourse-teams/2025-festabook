package com.daedan.festabook.presentation.news.notice.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
    private val listener: OnNoticeClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notice: NoticeUiModel) {
        binding.notice = notice

        binding.tvNoticeDescription.maxLines =
            if (notice.isExpanded) Integer.MAX_VALUE else 2
        binding.tvNoticeDescription.ellipsize =
            if (notice.isExpanded) null else TextUtils.TruncateAt.END

        binding.root.setOnClickListener {
            listener.onNoticeClick(notice)
        }
    }

    companion object {
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

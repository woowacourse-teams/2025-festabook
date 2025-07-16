package com.daedan.festabook.presentation.news.notice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notice: NoticeUiModel) {
        binding.notice = notice
    }

    companion object {
        fun from(parent: ViewGroup): NoticeViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoticeViewHolder(binding)
        }
    }
}

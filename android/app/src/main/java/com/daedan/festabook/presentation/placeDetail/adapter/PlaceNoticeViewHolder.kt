package com.daedan.festabook.presentation.placeDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class PlaceNoticeViewHolder private constructor(
    private val binding: ItemNoticeBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(noticeUiModel: NoticeUiModel) {
        binding.notice = noticeUiModel
    }

    companion object {
        fun from(parent: ViewGroup): PlaceNoticeViewHolder =
            PlaceNoticeViewHolder(
                ItemNoticeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
            )
    }
}

package com.daedan.festabook.presentation.news.notice.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.news.notice.NoticeUiModel

class NoticeAdapter : ListAdapter<NoticeUiModel, NoticeViewHolder>(noticeDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NoticeViewHolder = NoticeViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: NoticeViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val noticeDiffCallback =
            object : DiffUtil.ItemCallback<NoticeUiModel>() {
                override fun areItemsTheSame(
                    oldItem: NoticeUiModel,
                    newItem: NoticeUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: NoticeUiModel,
                    newItem: NoticeUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

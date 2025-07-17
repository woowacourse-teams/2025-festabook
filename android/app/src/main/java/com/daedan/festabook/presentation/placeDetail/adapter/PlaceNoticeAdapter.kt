package com.daedan.festabook.presentation.placeDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class PlaceNoticeAdapter : ListAdapter<NoticeUiModel, PlaceNoticeViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceNoticeViewHolder = PlaceNoticeViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: PlaceNoticeViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
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

package com.daedan.festabook.presentation.news.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

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
                ): Boolean = oldItem.title == newItem.title

                override fun areContentsTheSame(
                    oldItem: NoticeUiModel,
                    newItem: NoticeUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

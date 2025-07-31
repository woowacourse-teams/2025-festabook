package com.daedan.festabook.presentation.placeDetail.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemNoticeBinding
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

class PlaceNoticeViewHolder private constructor(
    private val binding: ItemNoticeBinding,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.setListener {
            binding.root.isSelected = !binding.root.isSelected
            binding.tvNoticeDescription.maxLines =
                if (binding.root.isSelected) Integer.MAX_VALUE else DEFAULT_LINE_COUNT
            binding.tvNoticeDescription.ellipsize =
                if (binding.root.isSelected) null else TextUtils.TruncateAt.END
        }
    }

    fun bind(noticeUiModel: NoticeUiModel) {
        binding.notice = noticeUiModel
    }

    companion object {
        private const val DEFAULT_LINE_COUNT = 1

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

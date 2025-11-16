package com.daedan.festabook.presentation.news.lost.adapter

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemLostGuideBinding
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.news.notice.adapter.NewsClickListener

class LostGuideItemViewHolder private constructor(
    private val binding: ItemLostGuideBinding,
    private val newsClickListener: NewsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var lostGuideItem: LostUiModel.Guide? = null

    init {
        binding.root.setOnClickListener {
            lostGuideItem?.let {
                newsClickListener.onLostGuideItemClick()
            }
        }
    }

    fun bind(lostGuideItem: LostUiModel.Guide) {
        this.lostGuideItem = lostGuideItem
        binding.tvLostGuideDescription.text = lostGuideItem.description
        binding.tvLostGuideDescription.visibility =
            if (lostGuideItem.isExpanded) View.VISIBLE else View.GONE

        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())

        val targetRotation =
            if (lostGuideItem.isExpanded) ICON_ROTATION_EXPANDED else ICON_ROTATION_COLLAPSED

        binding.ivLostGuideExpand
            .animate()
            .rotation(targetRotation)
            .setDuration(ICON_DURATION)
            .start()
    }

    companion object {
        private const val ICON_ROTATION_EXPANDED: Float = 180F
        private const val ICON_ROTATION_COLLAPSED: Float = 0F
        private const val ICON_DURATION: Long = 300L

        fun from(
            parent: ViewGroup,
            newsClickListener: NewsClickListener,
        ): LostGuideItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemLostGuideBinding.inflate(inflater, parent, false)

            return LostGuideItemViewHolder(binding, newsClickListener)
        }
    }
}

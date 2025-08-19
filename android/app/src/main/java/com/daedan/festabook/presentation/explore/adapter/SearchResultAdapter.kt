package com.daedan.festabook.presentation.explore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.domain.model.University

class SearchResultAdapter(
    private val onUniversityClickListener: OnUniversityClickListener,
) : ListAdapter<University, SearchResultViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchResultViewHolder = SearchResultViewHolder.from(parent, onUniversityClickListener)

    override fun onBindViewHolder(
        holder: SearchResultViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<University>() {
                override fun areItemsTheSame(
                    oldItem: University,
                    newItem: University,
                ): Boolean = oldItem.festivalId == newItem.festivalId

                override fun areContentsTheSame(
                    oldItem: University,
                    newItem: University,
                ): Boolean = oldItem == newItem
            }
    }
}

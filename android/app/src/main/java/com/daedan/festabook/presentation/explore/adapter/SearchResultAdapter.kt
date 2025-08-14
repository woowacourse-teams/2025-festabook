package com.daedan.festabook.presentation.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemSearchResultBinding
import com.daedan.festabook.domain.model.University

interface OnUniversityClickListener {
    fun onUniversityClick(university: University)
}

class SearchResultViewHolder private constructor(
    private val binding: ItemSearchResultBinding,
    private val onUniversityClickListener: OnUniversityClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(university: University) {
        binding.root.setOnClickListener {
            onUniversityClickListener.onUniversityClick(university)
        }
        binding.university = university
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onUniversityClickListener: OnUniversityClickListener,
        ): SearchResultViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemSearchResultBinding.inflate(inflater, parent, false)
            return SearchResultViewHolder(binding, onUniversityClickListener)
        }
    }
}

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

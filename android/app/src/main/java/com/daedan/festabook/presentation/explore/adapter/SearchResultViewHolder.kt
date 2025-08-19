package com.daedan.festabook.presentation.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemSearchResultBinding
import com.daedan.festabook.domain.model.University

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

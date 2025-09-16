package com.daedan.festabook.presentation.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemSearchResultBinding
import com.daedan.festabook.presentation.explore.model.SearchResultUiModel
import timber.log.Timber

class SearchResultViewHolder private constructor(
    private val binding: ItemSearchResultBinding,
    private val onUniversityClickListener: OnUniversityClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(searchResult: SearchResultUiModel) {
        binding.root.setOnClickListener {
            Timber.d("아이템 클릭 - ${searchResult.universityName}")
            onUniversityClickListener.onUniversityClick(searchResult)
        }
        binding.searchResult = searchResult
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

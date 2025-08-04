package com.daedan.festabook.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PosterAdapter(
    private val posters: List<String>,
) : RecyclerView.Adapter<PosterItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PosterItemViewHolder = PosterItemViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: PosterItemViewHolder,
        position: Int,
    ) {
        if (posters.isEmpty()) return

        val actualIndex = position % posters.size
        holder.bind(posters[actualIndex])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}

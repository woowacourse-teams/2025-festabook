package com.daedan.festabook.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemHomePosterBinding

class PosterAdapter(
    private val posters: List<Int>,
) : RecyclerView.Adapter<PosterItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PosterItemViewHolder {
        val binding =
            ItemHomePosterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return PosterItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PosterItemViewHolder,
        position: Int,
    ) {
        if (posters.isEmpty()) return

        val realPosition = position % posters.size
        holder.bind(posters[realPosition])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}

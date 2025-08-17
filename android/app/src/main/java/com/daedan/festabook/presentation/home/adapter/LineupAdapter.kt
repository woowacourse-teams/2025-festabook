package com.daedan.festabook.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.presentation.home.LineupItemUiModel

class LineupAdapter(
    private val lineupItems: List<LineupItemUiModel>,
) : RecyclerView.Adapter<LineupItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LineupItemViewHolder = LineupItemViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: LineupItemViewHolder,
        position: Int,
    ) {
        holder.bind(lineupItems[position])
    }

    override fun getItemCount(): Int = lineupItems.size
}

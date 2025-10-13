package com.daedan.festabook.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemHomeLineupOfDayBinding
import com.daedan.festabook.presentation.home.LineUpItemOfDayUiModel
import java.time.format.DateTimeFormatter

class LineUpItemOfDayViewHolder(
    private val binding: ItemHomeLineupOfDayBinding,
    private val adapter: LineupAdapter,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(lineUpItemOfDayUiModel: LineUpItemOfDayUiModel) {
        binding.rvHomeLineup.adapter = adapter
        adapter.submitList(lineUpItemOfDayUiModel.lineupItems)
        binding.tvLineupDay.text = DateTimeFormatter.ofPattern("MM.dd").format(lineUpItemOfDayUiModel.date)
        binding.tvIsDDay.visibility = if (lineUpItemOfDayUiModel.isDDay) View.VISIBLE else View.GONE
    }

    companion object {
        fun from(
            parent: ViewGroup,
            adapter: LineupAdapter,
        ): LineUpItemOfDayViewHolder {
            val binding =
                ItemHomeLineupOfDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return LineUpItemOfDayViewHolder(binding, adapter)
        }
    }
}

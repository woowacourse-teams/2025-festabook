package com.daedan.festabook.presentation.placeMap.timeTagSpinner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.daedan.festabook.databinding.ItemSpinnerDropdownBinding
import com.daedan.festabook.databinding.ItemSpinnerSelectedBinding
import com.daedan.festabook.domain.model.TimeTag

class TimeTagSpinnerAdapter(
    context: Context,
    private val items: MutableList<TimeTag>,
) : ArrayAdapter<TimeTag>(context, 0, items) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val binding: ItemSpinnerSelectedBinding
        val view: View

        if (convertView == null) {
            binding =
                ItemSpinnerSelectedBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false,
                )
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSpinnerSelectedBinding
        }

        binding.tvSelectedItem.text = items[position].name
        return view
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val binding: ItemSpinnerDropdownBinding
        val view: View

        if (convertView == null) {
            binding =
                ItemSpinnerDropdownBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false,
                )
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSpinnerDropdownBinding
        }

        binding.tvDropdownItem.text = items[position].name
        return view
    }

    fun updateItems(newItems: List<TimeTag>) {
        items.clear()
        items.addAll(newItems)
    }
}

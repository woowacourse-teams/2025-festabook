package com.daedan.festabook.presentation.placeList.adapter

import android.graphics.Canvas
import android.view.View
import androidx.core.graphics.withTranslation
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView

class PlaceListItemDecoration(
    private val adapter: PlaceListAdapter,
) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.onDrawOver(c, parent, state)
        if (parent.isEmpty()) {
            return
        }

        val headerViewHolder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(HEADER_INDEX))
        adapter.onBindViewHolder(headerViewHolder, HEADER_INDEX)

        val headerView = headerViewHolder.itemView
        headerView.measure(
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(START_POINT, View.MeasureSpec.UNSPECIFIED),
        )
        headerView.layout(START_POINT, START_POINT, parent.width, headerView.measuredHeight)

        c.withTranslation(START_POINT.toFloat(), START_POINT.toFloat()) {
            headerView.draw(this)
        }
    }

    companion object {
        private const val START_POINT = 0
        private const val HEADER_INDEX = 0
    }
}

package com.daedan.festabook.presentation.news.lost

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LostItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildLayoutPosition(view)
        val column = position % spanCount

        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount
        outRect.top = spacing
        if (position >= state.itemCount - 1) outRect.bottom = spacing
    }
}

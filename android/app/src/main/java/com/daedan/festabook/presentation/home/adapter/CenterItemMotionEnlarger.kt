package com.daedan.festabook.presentation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CenterItemMotionEnlarger(
    private val centerThresholdDp: Int = DEFAULT_CENTER_THRESHOLD_DP,
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(
        rv: RecyclerView,
        dx: Int,
        dy: Int,
    ) {
        val centerThresholdPx =
            (centerThresholdDp * rv.context.resources.displayMetrics.density).toInt()
        val center = rv.width / 2

        for (i in 0 until rv.childCount) {
            val child = rv.getChildAt(i)
            val holder = rv.getChildViewHolder(child) as? PosterItemViewHolder ?: continue

            val childCenter = (child.left + child.right) / 2
            val distanceFromCenter = abs(center - childCenter)

            if (distanceFromCenter < centerThresholdPx) {
                holder.transitionToExpanded()
            } else {
                holder.transitionToCollapsed()
            }
        }
    }

    companion object {
        const val DEFAULT_CENTER_THRESHOLD_DP = 10
    }
}

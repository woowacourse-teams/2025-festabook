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
        val thresholdPx = (centerThresholdDp * rv.context.resources.displayMetrics.density).toInt()
        updateChildAnimations(rv, thresholdPx)
    }

    fun expandCenterItem(recyclerView: RecyclerView) {
        val thresholdPx = (centerThresholdDp * recyclerView.context.resources.displayMetrics.density).toInt()
        updateChildAnimations(recyclerView, thresholdPx)
    }

    private fun updateChildAnimations(
        recyclerView: RecyclerView,
        thresholdPx: Int,
    ) {
        val center = recyclerView.width / 2
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val holder = recyclerView.getChildViewHolder(child) as? PosterItemViewHolder ?: continue

            val childCenter = (child.left + child.right) / 2
            val distanceFromCenter = abs(center - childCenter)

            if (distanceFromCenter < thresholdPx) {
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

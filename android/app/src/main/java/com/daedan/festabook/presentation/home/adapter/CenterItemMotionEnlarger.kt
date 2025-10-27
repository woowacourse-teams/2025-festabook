package com.daedan.festabook.presentation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.math.abs

@ContributesBinding(AppScope::class)
@Inject
class CenterItemMotionEnlarger : RecyclerView.OnScrollListener() {
    override fun onScrolled(
        rv: RecyclerView,
        dx: Int,
        dy: Int,
    ) {
        val thresholdPx =
            (DEFAULT_CENTER_THRESHOLD_DP * rv.context.resources.displayMetrics.density).toInt()
        updateChildAnimations(rv, thresholdPx)
    }

    fun expandCenterItem(recyclerView: RecyclerView) {
        val thresholdPx =
            (DEFAULT_CENTER_THRESHOLD_DP * recyclerView.context.resources.displayMetrics.density).toInt()
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
        private const val DEFAULT_CENTER_THRESHOLD_DP = 10
    }
}

package com.daedan.festabook.presentation.home

import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.presentation.home.adapter.PosterItemViewHolder
import kotlin.math.abs

class CenterItemMotionEnlarger : RecyclerView.OnScrollListener() {
    override fun onScrolled(
        rv: RecyclerView,
        dx: Int,
        dy: Int,
    ) {
        val center = rv.width / 2

        for (i in 0 until rv.childCount) {
            val child = rv.getChildAt(i)
            val holder = rv.getChildViewHolder(child) as? PosterItemViewHolder ?: continue // 안전한 캐스팅

            val childCenter = (child.left + child.right) / 2
            val distanceFromCenter = abs(center - childCenter)

            if (distanceFromCenter < 10) {
                holder.binding.motionLayout.transitionToState(R.id.expanded)
            } else {
                holder.binding.motionLayout.transitionToState(R.id.collapsed)
            }
        }
    }
}

package com.daedan.festabook.presentation.placeList.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup

class PlaceListBottomSheetBehavior<V : View>(
    context: Context,
    attrs: AttributeSet,
) : BottomSheetBehavior<V>(
        context,
        attrs,
    ) {
    private lateinit var recyclerView: RecyclerView
    private var headerRange: IntRange = 0..0

    init {
        state = STATE_HALF_EXPANDED
        isGestureInsetBottomIgnored = true
        addBottomSheetCallback(
            object : BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                    if (newState == STATE_HALF_EXPANDED && ::recyclerView.isInitialized) {
                        recyclerView.scrollToPosition(HEADER_POSITION)
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) = Unit
            },
        )
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: V,
        layoutDirection: Int,
    ): Boolean {
        recyclerView = child.findViewById<RecyclerView>(R.id.rv_places)
        expandedOffset = parent.findViewById<ChipGroup>(R.id.cg_categories).height
        recyclerView.getChildAt(HEADER_POSITION)?.let {
            headerRange = expandedOffset..(expandedOffset + it.height)
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent,
    ): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN &&
            state == STATE_EXPANDED &&
            event.y.toInt() in headerRange
        ) {
            state = STATE_COLLAPSED
        }
        return super.onInterceptTouchEvent(parent, child, event)
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed,
        )
        if (!recyclerView.canScrollVertically(-1)) {
            state = STATE_HALF_EXPANDED
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        type: Int,
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)

        if (!recyclerView.canScrollVertically(-1) && state == STATE_EXPANDED) {
            state = STATE_HALF_EXPANDED
        }
    }

    companion object {
        private const val HEADER_POSITION = 0
    }
}

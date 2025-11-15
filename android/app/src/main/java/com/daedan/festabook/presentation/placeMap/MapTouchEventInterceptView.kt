package com.daedan.festabook.presentation.placeMap

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout

class MapTouchEventInterceptView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(
        context,
        attrs,
    ) {
    private var onMapDragListener: OnMapDragListener? = null

    private var isMapDragging = false

    private val gestureDetector by lazy {
        GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    if (!isMapDragging) {
                        onMapDragListener?.onDrag()
                        isMapDragging = true
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float,
                ): Boolean {
                    if ((distanceY > 0 || distanceX > 0) && !isMapDragging) {
                        isMapDragging = true
                        onMapDragListener?.onDrag()
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
            },
        )
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_UP) {
                isMapDragging = false
            }
            gestureDetector.onTouchEvent(it)
        }
        return false
    }

    fun setOnMapDragListener(listener: OnMapDragListener) {
        onMapDragListener = listener
    }
}

package com.daedan.festabook.presentation.placeList.placeMap

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
    private var onMapDragListener: (() -> Unit)? = null

    private var isMapDragging = false

    private val gestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (!isMapDragging) {
                    onMapDragListener?.invoke()
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
                    onMapDragListener?.invoke()
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        event?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                isMapDragging = false
            }
            gestureDetector.onTouchEvent(event)
        }
        return false
    }

    fun setOnMapDragListener(listener: () -> Unit) {
        onMapDragListener = listener
    }
}
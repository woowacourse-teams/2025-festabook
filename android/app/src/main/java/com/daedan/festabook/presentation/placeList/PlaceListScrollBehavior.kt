package com.daedan.festabook.presentation.placeList

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.canScrollUp
import com.daedan.festabook.presentation.common.scrollAnimation

class PlaceListScrollBehavior(
    private val context: Context,
    attrs: AttributeSet,
) : CoordinatorLayout.Behavior<ConstraintLayout>() {
    private var initialY: Float = DEFAULT_VALUE
    private var minimumY: Float = DEFAULT_VALUE
    private var recyclerViewId: Int = DEFAULT_VALUE.toInt()
    private var companionViewId: Int = DEFAULT_VALUE.toInt()
    private var recyclerView: RecyclerView? = null
    private var companionView: View? = null
    private var isInitialized = false

    init {
        context.withStyledAttributes(attrs, R.styleable.PlaceListScrollBehavior) {
            initialY = getDimension(R.styleable.PlaceListScrollBehavior_initialY, DEFAULT_VALUE)
            minimumY = getDimension(R.styleable.PlaceListScrollBehavior_minimumY, DEFAULT_VALUE)
            recyclerViewId = getResourceId(R.styleable.PlaceListScrollBehavior_recyclerView, DEFAULT_VALUE.toInt())
            companionViewId = getResourceId(R.styleable.PlaceListScrollBehavior_companionView, DEFAULT_VALUE.toInt())
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: ConstraintLayout,
        layoutDirection: Int,
    ): Boolean {
        if (!isInitialized) {
            recyclerView = parent.findViewById(recyclerViewId)
            companionView = parent.findViewById(companionViewId)
            isInitialized = true
            child.translationY = child.rootView.height - initialY
        }
        companionView.setCompanionHeight(child)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ConstraintLayout,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int,
    ): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ConstraintLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int,
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        companionView.setCompanionHeight(child)

        recyclerView?.let {
            // 아래로 스크롤 하고, 리사이클러 뷰의 최상단에 도달하지 않았을 때
            if (dy < 0 && it.canScrollUp()) {
                child.background = AppCompatResources.getDrawable(context, R.drawable.bg_place_list)
                consumed[1] = 0
                return
            }

            // 리사이클러 뷰 스크롤 전 배경 스크롤 처리
            child.apply {
                val currentTranslationY = translationY
                val newTranslationY = currentTranslationY - dy
                val maxHeight = rootView.height.toFloat()
                val limitedTranslationY = newTranslationY.coerceIn(DEFAULT_VALUE, maxHeight - minimumY)
                translationY = limitedTranslationY
                scrollAnimation(limitedTranslationY)
                consumed[1] = limitedTranslationY.toInt()
            }
        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ConstraintLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {
        // 리스트가 모두 펼쳐지면 모서리 부분을 없앱니다
        if (dyUnconsumed == 0) {
            child.background = ContextCompat.getColor(context, R.color.gray050).toDrawable()
        }

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
    }

    private fun View?.setCompanionHeight(child: ConstraintLayout) {
        this?.apply {
            y = child.translationY - height
        }
    }

    companion object {
        private const val DEFAULT_VALUE = 0f
    }
}

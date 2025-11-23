package com.daedan.festabook.presentation.placeMap.placeList.behavior

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.canScrollUp
import com.daedan.festabook.presentation.common.getSystemBarHeightCompat
import com.daedan.festabook.presentation.common.scrollAnimation
import com.google.android.material.chip.ChipGroup

/**
 * @deprecated
 * @see
 *  이 클래스는 더 이상 사용되지 않으며, 향후 버전에서 제거될 예정입니다.
 *  대안으로 현재 PlaceListBottomSheetBehavior이 사용되고 있습니다
 *  네이버 지도 검색 UI를 본딴 동작을 수행합니다
 *  자세한 내용은 해당 링크를 참조해주세요
 * "https://github.com/woowacourse-teams/2025-festabook/pull/174"
 */
class PlaceListScrollBehavior(
    context: Context,
    attrs: AttributeSet,
) : CoordinatorLayout.Behavior<ConstraintLayout>() {
    private lateinit var attribute: Attribute
    private lateinit var state: BehaviorState
    private var isInitialized: Boolean = false
    private lateinit var minimumHeightView: View

    init {
        context.withStyledAttributes(attrs, R.styleable.PlaceListScrollBehavior) {
            setAttribute()
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: ConstraintLayout,
        layoutDirection: Int,
    ): Boolean {
        minimumHeightView = parent.findViewById<ChipGroup>(R.id.cg_categories)
        if (!isInitialized) {
            val recyclerView: RecyclerView? = parent.findViewById(attribute.recyclerViewId)
            val companionView: View? = parent.findViewById(attribute.companionViewId)
            isInitialized = true

            // 기기 높이 - 시스템 바 높이
            val rootViewHeight = child.rootView.height - child.getSystemBarHeightCompat()
            child.translationY = rootViewHeight - attribute.initialY
            state = BehaviorState(recyclerView, companionView, rootViewHeight)
        }
        state.companionView.setCompanionHeight(child)
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
        state.companionView.setCompanionHeight(child)

        val isAlreadyConsumed = child.consumeIfRecyclerViewCanScrollUp(dy, consumed)
        if (isAlreadyConsumed) return
        child.consumeBackgroundLayoutScroll(dy, consumed)
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
        if (dyUnconsumed == 0) {
            state.companionView?.visibility = View.GONE
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

    fun setOnScrollListener(listener: (dy: Float) -> Unit) {
        state = state.copy(onScrollListener = listener)
    }

    private fun TypedArray.setAttribute() {
        val initialY =
            getDimension(R.styleable.PlaceListScrollBehavior_initialY, UNINITIALIZED_VALUE)
        val minimumY =
            getDimension(R.styleable.PlaceListScrollBehavior_minimumY, UNINITIALIZED_VALUE)
        val recyclerViewId =
            getResourceId(
                R.styleable.PlaceListScrollBehavior_recyclerView,
                UNINITIALIZED_VALUE.toInt(),
            )
        val companionViewId =
            getResourceId(
                R.styleable.PlaceListScrollBehavior_companionView,
                UNINITIALIZED_VALUE.toInt(),
            )
        attribute =
            Attribute(
                initialY,
                minimumY,
                recyclerViewId,
                companionViewId,
            )
    }

    private fun View?.setCompanionHeight(child: ConstraintLayout) {
        this?.apply {
            y = child.translationY - height
        }
    }

    private fun ViewGroup.consumeBackgroundLayoutScroll(
        dy: Int,
        consumed: IntArray,
    ) {
        apply {
            // 최대 높이 (0일수록 천장에 가깝고, contentAreaHeight일수록 바닥에 가까움), 즉 maxHeight 까지만 스크롤을 내릴 수 있습니다
            val maxHeight = state.rootViewHeight - attribute.minimumY
            val requestedTranslationY = translationY - dy
            val newTranslationY = getNewTranslationY(requestedTranslationY, maxHeight)

            // 외부 레이아웃이 스크롤이 되었을 때만 스크롤 리스너 적용
            if (requestedTranslationY in minimumHeightView.height.toFloat()..maxHeight) {
                state.onScrollListener?.invoke(dy.toFloat())
            }
            translationY = newTranslationY
            scrollAnimation(newTranslationY)
            if (newTranslationY.toInt() == minimumHeightView.height) {
                consumed[1] = 0
            } else {
                consumed[1] = newTranslationY.toInt()
            }
        }
    }

    private fun ViewGroup.getNewTranslationY(
        requestedTranslationY: Float,
        maxHeight: Float,
    ): Float = requestedTranslationY.coerceIn(minimumHeightView.height.toFloat(), maxHeight)

    private fun ViewGroup.consumeIfRecyclerViewCanScrollUp(
        dy: Int,
        consumed: IntArray,
    ): Boolean {
        state.recyclerView?.let {
            // 리사이클러 뷰가 위로 스크롤 될 수 있을 때
            if (dy < 0 && it.canScrollUp()) {
                state.companionView?.visibility = View.VISIBLE
                consumed[1] = 0
                return true
            }
        }

        return false
    }

    private data class Attribute(
        val initialY: Float,
        val minimumY: Float,
        val recyclerViewId: Int,
        val companionViewId: Int,
    )

    private data class BehaviorState(
        val recyclerView: RecyclerView?,
        val companionView: View?,
        val rootViewHeight: Int,
        val onScrollListener: ((dy: Float) -> Unit)? = null,
    )

    companion object {
        private const val UNINITIALIZED_VALUE = 0f
    }
}

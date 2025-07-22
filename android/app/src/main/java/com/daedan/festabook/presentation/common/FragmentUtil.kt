package com.daedan.festabook.presentation.common

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.placeList.PlaceListScrollBehavior

inline fun <reified T : Parcelable> Bundle.getObject(key: String): T? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key)
    }

fun ConstraintLayout.placeListScrollBehavior(): PlaceListScrollBehavior? {
    val layoutParams = layoutParams as? CoordinatorLayout.LayoutParams
    val behavior = layoutParams?.behavior as? PlaceListScrollBehavior
    return behavior
}

fun FragmentPlaceListBinding.initialPadding() = layoutPlaceList.height / 2

fun Int.toPx(context: Context) =
    TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics,
        ).toInt()

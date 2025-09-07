package com.daedan.festabook.presentation.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.daedan.festabook.R
import com.daedan.festabook.data.util.ApiResultException
import com.daedan.festabook.presentation.placeList.behavior.PlaceListBottomSheetFollowBehavior
import com.google.android.material.snackbar.Snackbar

inline fun <reified T : Parcelable> Bundle.getObject(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key)
    }

inline fun <reified T : Parcelable> Intent.getObject(key: String): T? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        getParcelableExtra(key)
    }

fun Int.toPx(context: Context) =
    TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics,
        ).toInt()

fun View.getSystemBarHeightCompat() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rootWindowInsets
            .getInsets(
                WindowInsetsCompat.Type.systemBars(),
            ).bottom
    } else {
        rootWindowInsets.systemWindowInsetBottom
    }

fun Fragment.showErrorSnackBar(exception: Throwable?) {
    requireActivity().showErrorSnackBar(exception)
}

fun Activity.showErrorSnackBar(msg: String) {
    val snackBar = Snackbar.make(window.decorView.rootView, msg, Snackbar.LENGTH_SHORT)
    snackBar
        .setAction(
            getString(R.string.fail_snackbar_confirm),
        ) {
            snackBar.dismiss()
        }.setActionTextColor(getColor(R.color.blue400))
        .show()
}

fun Activity.showErrorSnackBar(exception: Throwable?) {
    when (exception) {
        is ApiResultException.ClientException -> {
            showErrorSnackBar(
                getString(R.string.error_client_exception),
            )
        }

        is ApiResultException.ServerException -> {
            showErrorSnackBar(
                getString(R.string.error_server_exception),
            )
        }

        is ApiResultException.NetworkException -> {
            showErrorSnackBar(
                getString(R.string.error_network_exception),
            )
        }

        is ApiResultException.UnknownException -> {
            showErrorSnackBar(
                getString(R.string.error_unknown_exception),
            )
        }

        else -> {
            showErrorSnackBar(
                exception?.message ?: getString(R.string.error_unknown_exception),
            )
        }
    }
}

fun View.placeListBottomSheetFollowBehavior(): PlaceListBottomSheetFollowBehavior? {
    val params = layoutParams as? CoordinatorLayout.LayoutParams
    return params?.behavior as? PlaceListBottomSheetFollowBehavior
}

package com.daedan.festabook.presentation.common

import android.Manifest
import android.content.Context
import android.widget.Toast
import com.daedan.festabook.R

fun Context.showToast(text: String) {
    Toast
        .makeText(
            this,
            text,
            Toast.LENGTH_SHORT,
        ).show()
}

fun Int.isGranted(): Boolean = this == 0

fun Context.toLocationPermissionDeniedTextOrNull(rawText: String) =
    when (rawText) {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
            getString(R.string.map_request_location_permission_message)
        }

        else -> null
    }

package com.daedan.festabook.presentation.common

import android.Manifest
import android.content.Context
import com.daedan.festabook.R

fun Int.isGranted(): Boolean = this == 0

fun Context.toLocationPermissionDeniedTextOrNull(rawText: String) =
    when (rawText) {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
            getString(R.string.map_request_location_permission_message)
        }

        else -> null
    }

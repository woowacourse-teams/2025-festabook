package com.daedan.festabook.presentation.common

import android.Manifest
import android.content.Context
import android.widget.Toast
import com.daedan.festabook.R

fun Context.showToastWithFormattedPermission(rawPermission: String) {
    val text = getString(R.string.map_request_location_permission_message)
    when (rawPermission) {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
            Toast
                .makeText(
                    this,
                    text,
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }
}

fun Int.isGranted(): Boolean = this.toInt() == 1

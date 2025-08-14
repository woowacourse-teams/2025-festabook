package com.daedan.festabook.presentation

import androidx.activity.result.ActivityResultLauncher

interface NotificationPermissionRequester {
    val permissionLauncher: ActivityResultLauncher<String>

    fun onPermissionGranted()

    fun onPermissionDenied()

    fun shouldShowPermissionRationale(permission: String): Boolean
}

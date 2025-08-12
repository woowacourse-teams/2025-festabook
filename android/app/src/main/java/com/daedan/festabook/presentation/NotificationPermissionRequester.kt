package com.daedan.festabook.presentation

import androidx.activity.result.ActivityResultLauncher

interface NotificationPermissionRequester {
    val permissionLauncher: ActivityResultLauncher<String>

    fun shouldShowPermissionRationale(permission: String): Boolean
}

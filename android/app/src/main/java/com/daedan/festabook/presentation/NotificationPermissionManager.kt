package com.daedan.festabook.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.daedan.festabook.R
import timber.log.Timber

class NotificationPermissionManager(
    private val requester: NotificationPermissionRequester,
    private val onPermissionGranted: () -> Unit = {},
    private val onPermissionDenied: () -> Unit = {},
) {
    fun requestNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 이미 권한이 허용됨
                    Timber.d("Notification permission already granted")
                    onPermissionGranted()
                }

                requester.shouldShowPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // 이전에 거부했지만 "다시 묻지 않음"을 선택하지 않은 경우
                    // 권한이 필요한 이유를 설명하는 UI(예: AlertDialog)를 표시
                    Timber.d("Show rationale for notification permission")
                    showRationaleDialog(context)
                }

                else -> {
                    // 권한이 없으며, 이전에 "다시 묻지 않음"을 선택하지 않았거나 첫 요청인 경우
                    // 바로 권한 요청 다이얼로그 표시
                    Timber.d("Requesting notification permission for the first time or after 'don't ask again'")
                    requester.permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Timber.d("Notification permission not required for API < 33")
        }
    }

    private fun showRationaleDialog(context: Context) {
        AlertDialog
            .Builder(context)
            .setTitle(R.string.notification_permission_title)
            .setMessage(R.string.notification_permission_message)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                requester.permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                onPermissionGranted()
                dialog.dismiss()
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                Timber.d("Notification permission denied")
                onPermissionDenied()
                dialog.dismiss()
            }.setCancelable(false)
            .show()
    }
}

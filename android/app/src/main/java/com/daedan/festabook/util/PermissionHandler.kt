package com.daedan.festabook.util

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PermissionHandler(
    private val activity: AppCompatActivity,
    private val onPermissionResult: (permission: String, granted: Boolean) -> Unit,
) {
    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            currentPermission?.let {
                onPermissionResult(it, granted)
            }
        }

    private var currentPermission: String? = null

    fun request(permission: String) {
        currentPermission = permission

        when {
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionResult(permission, true)
            }

            activity.shouldShowRequestPermissionRationale(permission) -> {
                showRationaleDialog(permission) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        launcher.launch(permission)
                    } else {
                        onPermissionResult(permission, true)
                    }
                }
            }

            else -> {
                launcher.launch(permission)
            }
        }
    }

    private fun showRationaleDialog(
        permission: String,
        onContinue: () -> Unit,
    ) {
        val message = "$permission 권한이 필요합니다."

        AlertDialog
            .Builder(activity)
            .setTitle("권한이 필요해요")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> onContinue() }
            .setNegativeButton("취소") { _, _ ->
                onPermissionResult(permission, false)
            }.setCancelable(false)
            .show()
    }
}

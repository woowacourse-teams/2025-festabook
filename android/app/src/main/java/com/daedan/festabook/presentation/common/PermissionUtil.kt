package com.daedan.festabook.presentation.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import com.daedan.festabook.R
import com.google.android.material.snackbar.Snackbar

fun Int.isGranted(): Boolean = this == 0

fun showNotificationDeniedSnackbar(
    view: View,
    context: Context,
    text: String = context.getString(R.string.notification_permission_denied_message),
) {
    Snackbar
        .make(
            view,
            text,
            Snackbar.LENGTH_LONG,
        ).setAnchorView(view.rootView.findViewById(R.id.bab_menu))
        .setAction(context.getString(R.string.move_to_setting_text)) {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context.startActivity(intent)
        }.show()
}

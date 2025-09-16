package com.daedan.festabook.presentation.splash

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.daedan.festabook.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Activity.updateDialog(listener: () -> Unit): AlertDialog {
    val dialogView =
        LayoutInflater.from(this).inflate(R.layout.view_app_update_alert_dialog, null)
    val dialog =
        MaterialAlertDialogBuilder(this, R.style.MainAlarmDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()

    dialogView.findViewById<Button>(R.id.btn_dialog_confirm)?.setOnClickListener {
        listener()
        dialog.dismiss()
    }
    return dialog
}

fun Activity.exitDialog(): AlertDialog =
    MaterialAlertDialogBuilder(this, R.style.MainAlarmDialogTheme)
        .setView(R.layout.view_app_update_failed_alert_dialog)
        .setNegativeButton(getString(R.string.update_failed_confirm)) { _, _ ->
            finish()
        }.setCancelable(false)
        .create()

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return when {
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

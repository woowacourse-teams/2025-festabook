package com.daedan.festabook.presentation.splash

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.updateDialog(listener: DialogInterface.OnClickListener) =
    MaterialAlertDialogBuilder(this)
        .setTitle("신규 버전 출시")
        .setMessage("새로운 버전이 출시되었습니다. 업데이트 하셔야 앱 사용이 가능합니다")
        .setCancelable(false)
        .setPositiveButton("업데이트", listener)
        .create()

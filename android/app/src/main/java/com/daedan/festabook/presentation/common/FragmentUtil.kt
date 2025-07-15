package com.daedan.festabook.presentation.common

import android.os.Bundle

inline fun <reified T> Bundle.getObject(key: String): T? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key)
    }

package com.daedan.festabook.logging

import android.os.Parcelable

interface LogData : Parcelable {
    val baseLogData: BaseLogData
}

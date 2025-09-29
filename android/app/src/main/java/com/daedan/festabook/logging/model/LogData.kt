package com.daedan.festabook.logging.model

import android.os.Bundle
import android.os.Parcelable
import java.lang.reflect.Field

interface LogData : Parcelable {
    fun writeToBundle(bundle: Bundle = Bundle()) =
        bundle.apply {
            this@LogData.javaClass.declaredFields.forEach { field ->
                putObject(field)
            }
        }

    private fun Bundle.putObject(field: Field) {
        field.isAccessible = true
        when (val value = field.get(this@LogData)) {
            is String -> putString(field.name, value)
            is Int -> putInt(field.name, value)
            is Long -> putLong(field.name, value)
            is Boolean -> putBoolean(field.name, value)
            is Float -> putFloat(field.name, value)
            is Double -> putDouble(field.name, value)
            is LogData -> value.writeToBundle(this)
        }
    }
}

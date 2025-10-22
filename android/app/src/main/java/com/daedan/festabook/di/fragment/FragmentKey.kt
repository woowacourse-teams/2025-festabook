package com.daedan.festabook.di.fragment

import androidx.fragment.app.Fragment
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentKey(
    val value: KClass<out Fragment>,
)

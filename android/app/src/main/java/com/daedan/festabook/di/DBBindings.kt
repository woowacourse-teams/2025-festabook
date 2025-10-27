package com.daedan.festabook.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
object DBBindings {
    // 추후에 Room으로 바뀔 예정이라 파일명 이렇게 해놓을게요.
    private const val PREFS_NAME = "app_prefs"

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

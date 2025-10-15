package com.daedan.festabook.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

private const val PREFS_NAME = "app_prefs"

@DependencyGraph(
    AppScope::class,
    bindingContainers = [NetworkBindings::class],
)
interface FestaBookAppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides application: Application,
        ): FestaBookAppGraph
    }

    // dataSource
    val deviceLocalDataSource: DeviceLocalDataSource
    val festivalLocalDataSource: FestivalLocalDataSource
    val fcmDataSource: FcmDataSource
    val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

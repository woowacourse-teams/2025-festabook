package com.daedan.festabook.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.di.viewmodel.ViewModelGraph
import com.daedan.festabook.logging.DefaultFirebaseLogger
import com.google.firebase.analytics.FirebaseAnalytics
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
    val fcmDataSource: FcmDataSource
    val deviceLocalDataSource: DeviceLocalDataSource

    // logger
    val defaultFirebaseLogger: DefaultFirebaseLogger

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory

    @Provides
    fun providesFirebaseAnalytics(application: Application): FirebaseAnalytics = FirebaseAnalytics.getInstance(application)

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

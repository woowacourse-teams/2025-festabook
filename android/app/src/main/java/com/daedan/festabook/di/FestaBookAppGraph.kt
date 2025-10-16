package com.daedan.festabook.di

import android.app.Application
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.di.viewmodel.ViewModelGraph
import com.daedan.festabook.logging.DefaultFirebaseLogger
import com.google.firebase.messaging.FirebaseMessaging
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import timber.log.Timber

@DependencyGraph(
    AppScope::class,
    bindingContainers = [NetworkBindings::class, FirebaseBindings::class, RoomBindings::class],
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
    val firebaseAnalyticsTree: Timber.Tree

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory

    // firebaseMessa
    val firebaseMessaging: FirebaseMessaging
}

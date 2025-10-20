package com.daedan.festabook.di

import android.app.Application
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.di.viewmodel.ViewModelGraph
import com.daedan.festabook.logging.DefaultFirebaseLogger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

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

    fun inject(app: FestaBookApp)

    // logger
    val defaultFirebaseLogger: DefaultFirebaseLogger

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory
}

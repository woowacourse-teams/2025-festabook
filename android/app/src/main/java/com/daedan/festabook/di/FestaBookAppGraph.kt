package com.daedan.festabook.di

import android.app.Application
import androidx.fragment.app.Fragment
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.di.viewmodel.ViewModelGraph
import com.daedan.festabook.logging.DefaultFirebaseLogger
import com.daedan.festabook.presentation.main.MainActivity
import com.daedan.festabook.presentation.placeList.behavior.PlaceListBottomSheetBehavior
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface FestaBookAppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides application: Application,
        ): FestaBookAppGraph
    }

    fun inject(app: FestaBookApp)

    fun inject(activity: MainActivity)

    fun inject(placeListBottomSheetBehavior: PlaceListBottomSheetBehavior<*>)

    @Multibinds(allowEmpty = true)
    val fragmentProviders: Map<KClass<out Fragment>, Provider<Fragment>>

    // logger
    val defaultFirebaseLogger: DefaultFirebaseLogger

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory
}

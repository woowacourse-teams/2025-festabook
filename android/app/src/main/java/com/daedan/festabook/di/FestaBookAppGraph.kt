package com.daedan.festabook.di

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.di.viewmodel.MetroViewModelFactory
import com.daedan.festabook.logging.DefaultFirebaseLogger
import com.daedan.festabook.presentation.main.MainActivity
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeMap.placeList.behavior.PlaceListBottomSheetBehavior
import com.daedan.festabook.presentation.schedule.ScheduleViewModel
import com.daedan.festabook.presentation.splash.SplashActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

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

    fun inject(activity: SplashActivity)

    fun inject(activity: PlaceDetailActivity)

    fun inject(placeListBottomSheetBehavior: PlaceListBottomSheetBehavior<*>)

    // splashActivity
    @Provides
    fun provideAppUpdateManager(application: Application): AppUpdateManager =
        AppUpdateManagerFactory.create(application)

    // logger
    val defaultFirebaseLogger: DefaultFirebaseLogger

    val metroViewModelFactory: MetroViewModelFactory

    val scheduleViewModelFactory: ScheduleViewModel.Factory
}

val Context.appGraph get() = (applicationContext as FestaBookApp).festaBookGraph

val Fragment.appGraph get() = (requireContext().applicationContext as FestaBookApp).festaBookGraph

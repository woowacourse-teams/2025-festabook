package com.daedan.festabook.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.datasource.remote.lineup.LineupDataSource
import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSource
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSource
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.presentation.placeDetail.PlaceDetailViewModel
import com.daedan.festabook.presentation.schedule.ScheduleViewModel
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
    val scheduleDataSource: ScheduleDataSource
    val noticeDataSource: NoticeDataSource
    val festivalDataSource: FestivalDataSource
    val festivalNotificationDataSource: FestivalNotificationDataSource
    val placeDataSource: PlaceDataSource
    val faqDataSource: FAQDataSource
    val lostItemDataSource: LostItemDataSource
    val lineupDataSource: LineupDataSource
    val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory
    val metroViewModelFactory: MetroViewModelFactory
    val scheduleViewModelFactory: ScheduleViewModel.Factory
    val placeDetailViewModelPlaceFactory: PlaceDetailViewModel.PlaceFactory

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

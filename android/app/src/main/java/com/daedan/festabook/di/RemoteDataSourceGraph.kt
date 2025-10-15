package com.daedan.festabook.di

import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.datasource.remote.lineup.LineupDataSource
import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSource
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSource
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import dev.zacsweers.metro.GraphExtension

@GraphExtension(DataSourceScope::class)
interface RemoteDataSourceGraph {
    val scheduleDataSource: ScheduleDataSource
    val noticeDataSource: NoticeDataSource
    val festivalDataSource: FestivalDataSource
    val festivalNotificationDataSource: FestivalNotificationDataSource
    val placeDataSource: PlaceDataSource
    val faqDataSource: FAQDataSource
    val lostItemDataSource: LostItemDataSource
    val lineupDataSource: LineupDataSource
    val fcmDataSource: FcmDataSource
}

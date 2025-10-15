package com.daedan.festabook.di

import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.domain.repository.ExploreRepository
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.domain.repository.LostItemRepository
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.domain.repository.ScheduleRepository
import dev.zacsweers.metro.GraphExtension

@GraphExtension(RepositoryScope::class)
interface RepositoryGraph {
    val deviceRepository: DeviceRepository
    val festivalRepository: FestivalRepository
    val scheduleRepository: ScheduleRepository
    val noticeRepository: NoticeRepository
    val faqRepository: FAQRepository
    val lostItemRepository: LostItemRepository
    val exploreRepository: ExploreRepository
    val festivalNotificationRepository: FestivalNotificationRepository
    val placeListRepository: PlaceListRepository
    val placeDetailRepository: PlaceDetailRepository
}

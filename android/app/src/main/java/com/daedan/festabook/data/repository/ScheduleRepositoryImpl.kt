package com.daedan.festabook.data.repository

import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus
import com.daedan.festabook.domain.repository.ScheduleRepository

class ScheduleRepositoryImpl : ScheduleRepository {
    override val dummyScheduleEvents =
        listOf(
            ScheduleEvent(
                id = 1L,
                eventDayId = 101L,
                status = ScheduleEventStatus.UPCOMING,
                startTime = "09:00",
                endTime = "10:00",
                title = "개막식",
                location = "운동장",
            ),
            ScheduleEvent(
                id = 2L,
                eventDayId = 101L,
                status = ScheduleEventStatus.ONGOING,
                startTime = "10:30",
                endTime = "11:30",
                title = "버스킹 공연",
                location = "중앙 광장",
            ),
            ScheduleEvent(
                id = 3L,
                eventDayId = 102L,
                status = ScheduleEventStatus.COMPLETED,
                startTime = "12:00",
                endTime = "13:00",
                title = "푸드트럭 런치타임",
                location = "푸드트럭 존",
            ),
            ScheduleEvent(
                id = 4L,
                eventDayId = 102L,
                status = ScheduleEventStatus.UPCOMING,
                startTime = "13:30",
                endTime = "14:30",
                title = "게임 이벤트",
                location = "이벤트 홀",
            ),
            ScheduleEvent(
                id = 5L,
                eventDayId = 103L,
                status = ScheduleEventStatus.ONGOING,
                startTime = "15:00",
                endTime = "16:30",
                title = "학생회 토크쇼",
                location = "대강당",
            ),
            ScheduleEvent(
                id = 6L,
                eventDayId = 103L,
                status = ScheduleEventStatus.COMPLETED,
                startTime = "17:00",
                endTime = "18:00",
                title = "폐막식",
                location = "운동장",
            ),
        )
}

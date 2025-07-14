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
                description = "축제의 시작을 알리는 개막식입니다.",
                location = "운동장",
                isBookmarked = false,
            ),
            ScheduleEvent(
                id = 2L,
                eventDayId = 101L,
                status = ScheduleEventStatus.ONGOING,
                startTime = "10:30",
                endTime = "11:30",
                title = "버스킹 공연",
                description = "동아리의 자유로운 버스킹 무대.",
                location = "중앙 광장",
                isBookmarked = true,
            ),
            ScheduleEvent(
                id = 3L,
                eventDayId = 102L,
                status = ScheduleEventStatus.COMPLETED,
                startTime = "12:00",
                endTime = "13:00",
                title = "푸드트럭 런치타임",
                description = "다양한 먹거리를 즐겨보세요.",
                location = "푸드트럭 존",
                isBookmarked = false,
            ),
            ScheduleEvent(
                id = 4L,
                eventDayId = 102L,
                status = ScheduleEventStatus.UPCOMING,
                startTime = "13:30",
                endTime = "14:30",
                title = "게임 이벤트",
                description = "재미있는 미니게임 대회!",
                location = "이벤트 홀",
                isBookmarked = true,
            ),
            ScheduleEvent(
                id = 5L,
                eventDayId = 103L,
                status = ScheduleEventStatus.ONGOING,
                startTime = "15:00",
                endTime = "16:30",
                title = "학생회 토크쇼",
                description = "학생회와 함께하는 소통의 시간.",
                location = "대강당",
                isBookmarked = true,
            ),
            ScheduleEvent(
                id = 6L,
                eventDayId = 103L,
                status = ScheduleEventStatus.COMPLETED,
                startTime = "17:00",
                endTime = "18:00",
                title = "폐막식",
                description = "축제의 마지막을 장식하는 시간.",
                location = "운동장",
                isBookmarked = false,
            ),
        )
}

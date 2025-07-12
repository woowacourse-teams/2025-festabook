package com.daedan.festabook.data.repository

import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.repository.ScheduleRepository

class ScheduleRepositoryImpl : ScheduleRepository {
    override val dummyScheduleEvents =
        listOf(
            ScheduleEvent(
                id = 1L,
                eventDayId = 100L,
                status = "예정",
                startTime = "10:00",
                endTime = "11:00",
                title = "개막식",
                location = "운동장",
            ),
            ScheduleEvent(
                id = 2L,
                eventDayId = 100L,
                status = "진행중",
                startTime = "11:30",
                endTime = "12:30",
                title = "버스킹 공연",
                location = "중앙광장",
            ),
            ScheduleEvent(
                id = 3L,
                eventDayId = 100L,
                status = "종료",
                startTime = "13:00",
                endTime = "14:00",
                title = "먹거리 부스 운영",
                location = "부스거리",
            ),
            ScheduleEvent(
                id = 4L,
                eventDayId = 101L,
                status = "예정",
                startTime = "15:00",
                endTime = "16:00",
                title = "e스포츠 대회",
                location = "강당",
            ),
            ScheduleEvent(
                id = 5L,
                eventDayId = 101L,
                status = "진행중",
                startTime = "16:30",
                endTime = "17:30",
                title = "연극 공연",
                location = "소극장",
            ),
            ScheduleEvent(
                id = 6L,
                eventDayId = 102L,
                status = "예정",
                startTime = "18:00",
                endTime = "19:00",
                title = "초청 가수 무대",
                location = "운동장",
            ),
            ScheduleEvent(
                id = 7L,
                eventDayId = 102L,
                status = "종료",
                startTime = "19:30",
                endTime = "20:00",
                title = "불꽃놀이",
                location = "운동장",
            ),
        )
}

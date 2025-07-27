package com.daedan.festabook.schedule

import com.daedan.festabook.domain.model.ScheduleDate
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus
import com.daedan.festabook.presentation.schedule.ScheduleEventsUiState
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiStatus
import java.time.LocalDate

val FAKE_SCHEDULE_EVENTS =
    listOf(
        ScheduleEvent(
            id = 1L,
            status = ScheduleEventStatus.UPCOMING, // 필요 시 enum 정의에 맞게 변경
            startTime = "2025-07-26T10:00:00",
            endTime = "2025-07-26T11:00:00",
            title = "안드로이드 스터디",
            location = "서울 강남구 어딘가",
        ),
    )
val FAKE_SCHEDULE_EVENTS_UI_MODELS =
    listOf(
        ScheduleEventUiModel(
            id = 1L,
            status = ScheduleEventUiStatus.UPCOMING, // enum이나 클래스에 맞게 수정
            startTime = "10:00",
            endTime = "11:00",
            title = "안드로이드 스터디",
            location = "서울 강남구 어딘가",
            isBookmarked = false,
        ),
    )

val FAKE_SCHEDULE_EVENTS_UI_STATE = ScheduleEventsUiState.Success(FAKE_SCHEDULE_EVENTS_UI_MODELS)

val FAKE_SCHEDULE_DATES =
    listOf(
        ScheduleDate(id = 1L, date = LocalDate.of(2025, 7, 26)),
    )

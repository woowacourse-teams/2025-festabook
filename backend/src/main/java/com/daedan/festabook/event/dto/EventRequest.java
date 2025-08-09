package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventDate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record EventRequest(

        @Schema(description = "일정 날짜 ID", example = "1")
        Long eventDateId,

        @Schema(description = "일정 시작 시간", example = "01:00")
        LocalTime startTime,

        @Schema(description = "일정 종료 시간", example = "02:00")
        LocalTime endTime,

        @Schema(description = "일정 제목", example = "미소가 알려주는 고급 알고리즘 컨퍼런스")
        String title,

        @Schema(description = "일정 진행 위치", example = "알고리즘 밸리 정문")
        String location
) {

    public Event toEntity(EventDate eventDate) {
        return new Event(
                eventDate,
                startTime,
                endTime,
                title,
                location
        );
    }
}

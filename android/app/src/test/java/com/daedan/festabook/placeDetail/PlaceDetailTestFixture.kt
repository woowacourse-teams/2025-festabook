package com.daedan.festabook.placeDetail

import com.daedan.festabook.domain.model.PlaceDetail
import com.daedan.festabook.domain.model.PlaceDetailImage
import com.daedan.festabook.news.FAKE_NOTICES
import com.daedan.festabook.placeList.FAKE_PLACES
import java.time.LocalTime

val FAKE_PLACE_DETAIL =
    PlaceDetail(
        id = 1,
        place = FAKE_PLACES.first(),
        notices = FAKE_NOTICES,
        host = "테스트 1",
        startTime = LocalTime.of(9, 0, 0),
        endTime = LocalTime.of(18, 0, 0),
        images =
            listOf(
                PlaceDetailImage(
                    id = 1,
                    imageUrl = "",
                    sequence = 1,
                ),
            ),
    )

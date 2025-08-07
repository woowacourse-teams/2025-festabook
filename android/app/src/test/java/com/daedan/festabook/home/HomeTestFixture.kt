package com.daedan.festabook.home

import com.daedan.festabook.domain.model.Festival
import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.domain.model.Poster
import java.time.LocalDate

val FAKE_ORGANIZATION =
    Organization(
        id = 1,
        universityName = "하버드 대학교",
        festival =
            Festival(
                festivalName = "하버드 대동제",
                festivalImages =
                    listOf(
                        Poster(
                            id = 1,
                            imageUrl = "",
                            sequence = 1,
                        ),
                    ),
                startDate = LocalDate.of(2025, 1, 1),
                endDate = LocalDate.of(2025, 1, 3),
            ),
    )

package com.daedan.festabook.presentation.placeList.dummy

import com.daedan.festabook.presentation.placeList.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

object DummyPlace {
    val placeUiModelList =
        buildList {
            repeat(100) { i ->
                add(
                    PlaceUiModel(
                        id = i.toLong(),
                        imageUrl = "https://cdn-dantats.stunning.kr/prod/portfolios/2f38e380-1ddd-4916-bd91-2ea3e901ff50/contents/YuoATBxT6sxa7ey2.%EC%82%B0%EB%94%94-10110-%EB%B0%B0%ED%98%9C%EC%97%B0.jpg.small?q=80&f=webp&t=crop&s=3508x4961",
                        category = PlaceCategory.BOOTH,
                        title = "코딩하며 한잔$i",
                        description = "시원한 맥주와 맛있는 파전!",
                        location = "공학관 앞",
                    ),
                )
            }
        }
}

package com.daedan.festabook.presentation.placeDetail.model

import android.os.Parcelable
import com.daedan.festabook.domain.model.PlaceDetail
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import com.daedan.festabook.presentation.placeMap.model.toUiModel
import kotlinx.parcelize.Parcelize
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Parcelize
data class PlaceDetailUiModel(
    val place: PlaceUiModel,
    val notices: List<NoticeUiModel>,
    val host: String?,
    val startTime: String?,
    val endTime: String?,
    val images: List<ImageUiModel>,
) : Parcelable {
    val featuredImage: String?
        get() = images.firstOrNull()?.url

    val operatingHours: String
        get() = "$startTime ~ $endTime"
}

fun PlaceDetail.toUiModel() =
    PlaceDetailUiModel(
        place = place.toUiModel(),
        notices = sortedNotices.map { it.toUiModel() },
        host = host,
        startTime = startTime.toFormattedString(),
        endTime = endTime.toFormattedString(),
        images = sortedImages.map { it.toUiModel() },
    )

private fun LocalTime?.toFormattedString(): String? =
    this?.format(
        DateTimeFormatter.ofPattern("HH:mm"),
    )

package com.daedan.festabook.data.model.response.place

import com.daedan.festabook.data.model.response.place.PlaceGeographyResponse.PlaceCategory
import com.daedan.festabook.domain.model.Notice
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceDetail
import com.daedan.festabook.domain.model.PlaceDetailImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data class PlaceDetailResponse(
    @SerialName("placeId")
    val id: Long,
    @SerialName("category")
    val category: PlaceCategory,
    @SerialName("description")
    val description: String?,
    @SerialName("startTime")
    val startTime: String?,
    @SerialName("endTime")
    val endTime: String?,
    @SerialName("host")
    val host: String?,
    @SerialName("location")
    val location: String?,
    @SerialName("placeAnnouncements")
    val placeAnnouncements: List<PlaceAnnouncement>,
    @SerialName("placeImages")
    val placeImages: List<PlaceImage>,
    @SerialName("title")
    val title: String?,
    @SerialName("timeTags")
    val timeTags: List<TimeTagResponse>,
) {
    @Serializable
    data class PlaceAnnouncement(
        @SerialName("id")
        val id: Long,
        @SerialName("title")
        val title: String,
        @SerialName("content")
        val content: String,
        @SerialName("createdAt")
        val createdAt: String,
    )

    @Serializable
    data class PlaceImage(
        @SerialName("id")
        val id: Long,
        @SerialName("imageUrl")
        val imageUrl: String,
        @SerialName("sequence")
        val sequence: Int,
    )
}

fun PlaceDetailResponse.toDomain() =
    PlaceDetail(
        id = id,
        place = toPlace(),
        notices = toNotices(),
        host = host,
        startTime = startTime.toLocalTime(),
        endTime = endTime.toLocalTime(),
        images = toPlaceDetailImages(),
    )

private fun PlaceDetailResponse.toPlace() =
    Place(
        id = id,
        title = title,
        category = category.toDomain(),
        description = description,
        imageUrl = placeImages.find { it.sequence == 1 }?.imageUrl,
        location = location,
        timeTags = timeTags.map { it.toDomain() },
    )

private fun PlaceDetailResponse.toNotices() =
    placeAnnouncements.map {
        Notice(
            id = it.id,
            title = it.title,
            content = it.content,
            isPinned = false,
            createdAt = LocalDateTime.parse(it.createdAt),
        )
    }

private fun PlaceDetailResponse.toPlaceDetailImages() =
    placeImages.map {
        PlaceDetailImage(
            id = it.id,
            imageUrl = it.imageUrl,
            sequence = it.sequence,
        )
    }

private fun String?.toLocalTime(): LocalTime? =
    runCatching {
        LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrNull()

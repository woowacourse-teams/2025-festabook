package com.daedan.festabook.data.model.response

import com.daedan.festabook.domain.model.Festival
import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.domain.model.Poster
import com.daedan.festabook.domain.model.toLocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("universityName")
    val universityName: String,
    @SerialName("festivalImages")
    val festivalImages: List<FestivalImage>,
    @SerialName("festivalName")
    val festivalName: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
) {
    @Serializable
    data class FestivalImage(
        @SerialName("id")
        val id: Long,
        @SerialName("imageUrl")
        val imageUrl: String,
        @SerialName("sequence")
        val sequence: Int,
    )
}

fun OrganizationResponse.toDomain() =
    Organization(
        id = id,
        universityName = universityName,
        festival =
            Festival(
                festivalImages = festivalImages.map { it.toDomain() },
                festivalName = festivalName,
                startDate = startDate.toLocalDate(),
                endDate = endDate.toLocalDate(),
            ),
    )

fun OrganizationResponse.FestivalImage.toDomain() =
    Poster(
        id = id,
        imageUrl = imageUrl,
        sequence = sequence,
    )

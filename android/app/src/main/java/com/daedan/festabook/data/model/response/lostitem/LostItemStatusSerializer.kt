package com.daedan.festabook.data.model.response.lostitem

import com.daedan.festabook.domain.model.LostItemStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LostItemStatusSerializer : KSerializer<LostItemStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "LostItemStatus",
            PrimitiveKind
                .STRING,
        )

    override fun deserialize(decoder: Decoder): LostItemStatus {
        val decoded = decoder.decodeString()
        return LostItemStatus.entries.find { it.name == decoded } ?: LostItemStatus.UNKNOWN
    }

    override fun serialize(
        encoder: Encoder,
        value: LostItemStatus,
    ) {
        encoder.encodeString(value.name)
    }
}

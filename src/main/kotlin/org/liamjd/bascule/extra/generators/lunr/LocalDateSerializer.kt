package org.liamjd.bascule.extra.generators.lunr

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * This serializer only works to ISO_DATE
 */
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("date", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, obj: LocalDate) {
		encoder.encodeString(obj.format(DateTimeFormatter.ISO_DATE))
	}

	override fun deserialize(decoder: Decoder): LocalDate {
		return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE)
	}
}

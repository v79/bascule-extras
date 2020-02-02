package org.liamjd.bascule.extra.generators.lunr

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * This serializer only works to ISO_DATE
 */
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
	override val descriptor: SerialDescriptor =
		StringDescriptor.withName("date")

	override fun serialize(encoder: Encoder, obj: LocalDate) {
		encoder.encodeString(obj.format(DateTimeFormatter.ISO_DATE))
	}

	override fun deserialize(decoder: Decoder): LocalDate {
		return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE)
	}
}

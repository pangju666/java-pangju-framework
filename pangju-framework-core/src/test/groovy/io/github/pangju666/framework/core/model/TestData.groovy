package io.github.pangju666.framework.core.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.pangju666.framework.core.enums.DesensitizedType
import io.github.pangju666.framework.core.enums.TestEnum
import io.github.pangju666.framework.core.jackson.annotation.DesensitizeFormat
import io.github.pangju666.framework.core.jackson.databind.deserializer.*
import io.github.pangju666.framework.core.jackson.databind.serializer.DateJsonSerializer
import io.github.pangju666.framework.core.jackson.databind.serializer.LocalDateJsonSerializer
import io.github.pangju666.framework.core.jackson.databind.serializer.LocalDateTimeJsonSerializer

import java.time.LocalDate
import java.time.LocalDateTime

class TestData {
	@DesensitizeFormat(type = DesensitizedType.PHONE_NUMBER)
	String phoneNumber
	@JsonDeserialize(using = EnumJsonDeserializer.class)
	TestEnum value
	@JsonSerialize(using = DateJsonSerializer.class)
	@JsonDeserialize(using = DateJsonDeserializer.class)
	Date date
	@JsonSerialize(using = LocalDateJsonSerializer.class)
	@JsonDeserialize(using = LocalDateJsonDeserializer.class)
	LocalDate localDate
	@JsonSerialize(using = LocalDateTimeJsonSerializer.class)
	@JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
	LocalDateTime localDateTime
	@JsonDeserialize(using = BigDecimalJsonDeserializer.class)
	BigDecimal decimal
	@JsonDeserialize(using = ClassJsonDeserializer.class)
	Class<?> aClass
}

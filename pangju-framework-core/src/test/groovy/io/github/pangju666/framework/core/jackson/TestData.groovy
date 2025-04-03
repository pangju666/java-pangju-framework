package io.github.pangju666.framework.core.jackson

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.pangju666.framework.core.jackson.databind.deserializer.EnumJsonDeserializer

class TestData {
	@JsonDeserialize(using = EnumJsonDeserializer.class)
	TestEnum value
}

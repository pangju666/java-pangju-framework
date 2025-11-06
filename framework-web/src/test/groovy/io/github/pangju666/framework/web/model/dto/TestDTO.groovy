package io.github.pangju666.framework.web.model.dto

import org.hibernate.validator.constraints.UUID

class TestDTO {
	String key
	String value
	@UUID
	String uuid

	TestDTO() {
	}

	TestDTO(String key, String value) {
		this.key = key
		this.value = value
	}
}

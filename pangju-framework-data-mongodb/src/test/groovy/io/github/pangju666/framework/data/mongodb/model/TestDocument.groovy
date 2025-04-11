package io.github.pangju666.framework.data.mongodb.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "test")
class TestDocument extends BaseDocument {
	String name
	String value

	String getName() {
		return name
	}

	void setName(String name) {
		this.name = name
	}

	String getValue() {
		return value
	}

	void setValue(String value) {
		this.value = value
	}
}

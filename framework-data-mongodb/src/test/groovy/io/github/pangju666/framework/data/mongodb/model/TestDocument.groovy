package io.github.pangju666.framework.data.mongodb.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "test")
class TestDocument extends BaseDocument {
	String name
	String value

	boolean equals(o) {
		if (this.is(o)) return true
		if (!(o instanceof TestDocument)) return false

		TestDocument that = (TestDocument) o

		if (name != that.name) return false
		if (value != that.value) return false

		return true
	}

	int hashCode() {
		int result
		result = (name != null ? name.hashCode() : 0)
		result = 31 * result + (value != null ? value.hashCode() : 0)
		return result
	}
}
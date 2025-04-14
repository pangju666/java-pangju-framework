package io.github.pangju666.framework.web.client.model.dto;

public class TestDTO {
	private String key;
	private String value;

	public TestDTO() {
	}

	public TestDTO(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TestDTO{" +
			"key='" + key + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}

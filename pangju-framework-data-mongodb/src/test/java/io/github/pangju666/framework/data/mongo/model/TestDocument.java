package io.github.pangju666.framework.data.mongo.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "test")
public class TestDocument extends BasicDocument {
	String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

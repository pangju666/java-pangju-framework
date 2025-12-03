package io.github.pangju666.framework.data.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
class UserDocument {
	@Id
	private Long id;
	private String name;
	private Integer age;
	private String email;

	UserDocument() {}

	UserDocument(Long id, String name, Integer age, String email) {
		this.id = id; this.name = name; this.age = age; this.email = email;
	}

	Long getId() { return id; }

	void setId(Long id) { this.id = id; }

	String getName() { return name; }

	void setName(String name) { this.name = name; }

	Integer getAge() { return age; }

	void setAge(Integer age) { this.age = age; }

	String getEmail() { return email; }

	void setEmail(String email) { this.email = email; }
}
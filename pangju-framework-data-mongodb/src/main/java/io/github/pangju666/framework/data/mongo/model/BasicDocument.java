package io.github.pangju666.framework.data.mongo.model;

import io.github.pangju666.framework.data.mongo.repository.BaseRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

public abstract class BasicDocument implements Serializable {
	@Id
	@Field(name = BaseRepository.ID_FIELD_NAME)
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
package io.github.pangju666.framework.data.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.pangju666.framework.data.mybatisplus.type.handler.JsonTypeHandler;

import java.util.List;
import java.util.Map;

@TableName(value = "TEST")
public class TestEntity {
	@TableId(value = "ID", type = IdType.AUTO)
	private Long id;
	@TableField(value = "NAME")
	private String name;
	@TableField(value = "JSON_VALUE", typeHandler = JsonTypeHandler.class)
	private Map<String, Object> jsonValue;
	@TableField(value = "JSON_ARRAY", typeHandler = JsonTypeHandler.class)
	private List<String> jsonArray;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(Map<String, Object> jsonValue) {
		this.jsonValue = jsonValue;
	}

	public List<String> getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(List<String> jsonArray) {
		this.jsonArray = jsonArray;
	}
}
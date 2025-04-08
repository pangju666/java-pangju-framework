package io.github.pangju666.framework.data.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.pangju666.framework.data.mybatisplus.type.handler.JsonTypeHandler;

import java.util.List;
import java.util.Map;

@TableName(value = "test", autoResultMap = true)
public class MySQLTestEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private String name;
	@TableField(typeHandler = JsonTypeHandler.class)
	private Map<String, Object> jsonValue;
	@TableField(typeHandler = JsonTypeHandler.class)
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
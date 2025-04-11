package io.github.pangju666.framework.data.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.pangju666.framework.data.mybatisplus.model.entity.auto.AutoIdLogicBasicDO;
import io.github.pangju666.framework.data.mybatisplus.type.handler.ClassTypeHandler;
import io.github.pangju666.framework.data.mybatisplus.type.handler.JsonTypeHandler;
import io.github.pangju666.framework.data.mybatisplus.type.handler.list.LongVarcharToListTypeHandler;

import java.util.List;
import java.util.Map;

@TableName(value = "test", autoResultMap = true)
public class TestEntity extends AutoIdLogicBasicDO {
	private String name;
	@TableField(typeHandler = JsonTypeHandler.class)
	private Map<String, Object> jsonValue;
	@TableField(typeHandler = JsonTypeHandler.class)
	private List<String> jsonArray;
	@TableField(typeHandler = LongVarcharToListTypeHandler.class)
	private List<Long> list;
	@TableField(typeHandler = ClassTypeHandler.class)
	private Class<?> clz;

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

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public List<Long> getList() {
		return list;
	}

	public void setList(List<Long> list) {
		this.list = list;
	}
}
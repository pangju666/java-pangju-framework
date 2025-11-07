package io.github.pangju666.framework.data.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.pangju666.framework.data.mybatisplus.model.entity.BaseEntity;
import io.github.pangju666.framework.data.mybatisplus.type.handler.JsonTypeHandler;

import java.util.List;
import java.util.Map;

@TableName(value = "test", autoResultMap = true)
public class TestDO extends BaseEntity {
	@TableId(type = IdType.AUTO)
	Long id;
	String name;
	/*@TableField(typeHandler = JsonTypeHandler.class)
	private Map<String, Object> tags;*/
	@TableField(typeHandler = JsonTypeHandler.class)
	Map<String, Object> metaData;
	@TableField(typeHandler = JsonTypeHandler.class)
	List<String> tags;
/*	@TableField(typeHandler = LongListTypeHandler.class)
	private List<Long> list;
	@TableField(typeHandler = ClassTypeHandler.class)
	private Class<?> clz;*/

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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}
}
package io.github.pangju666.framework.data.mybatisplus.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.github.pangju666.framework.data.mybatisplus.model.entity.BaseEntity
import io.github.pangju666.framework.data.mybatisplus.type.handler.JsonTypeHandler

@TableName(value = "test", autoResultMap = true)
class TestDO extends BaseEntity {
	@TableId(type = IdType.AUTO)
	String name;
	/*@TableField(typeHandler = JsonTypeHandler.class)
	private Map<String, Object> tags;*/
	@TableField(typeHandler = JsonTypeHandler.class)
	List<String> tags;
/*	@TableField(typeHandler = LongListTypeHandler.class)
	private List<Long> list;
	@TableField(typeHandler = ClassTypeHandler.class)
	private Class<?> clz;*/
}
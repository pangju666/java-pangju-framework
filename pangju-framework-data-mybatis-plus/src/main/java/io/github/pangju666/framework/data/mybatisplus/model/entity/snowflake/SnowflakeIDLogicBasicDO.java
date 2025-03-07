package io.github.pangju666.framework.data.mybatisplus.model.entity.snowflake;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.pangju666.framework.data.mybatisplus.model.entity.base.LogicBasicDO;

public abstract class SnowflakeIDLogicBasicDO extends LogicBasicDO<String> implements SnowflakeId {
	@TableId(type = IdType.ASSIGN_ID)
	protected Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}

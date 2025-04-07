package io.github.pangju666.framework.data.mybatisplus.model.entity.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.pangju666.framework.data.mybatisplus.model.entity.base.VersionLogicBasicDO;

public abstract class AutoIdVersionLogicBasicDO extends VersionLogicBasicDO<Long> implements AutoId {
	@TableId(type = IdType.AUTO)
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

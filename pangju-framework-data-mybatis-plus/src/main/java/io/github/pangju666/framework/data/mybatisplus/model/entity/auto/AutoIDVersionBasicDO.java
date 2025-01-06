package io.github.pangju666.framework.data.mybatisplus.model.entity.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.pangju666.framework.data.mybatisplus.model.entity.base.VersionBasicDO;

public abstract class AutoIDVersionBasicDO extends VersionBasicDO implements AutoID {
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

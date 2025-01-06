package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

import com.baomidou.mybatisplus.annotation.Version;

public abstract class VersionLogicStatusBasicDO<ID> extends LogicStatusBasicDO<ID> {
	@Version
	private Integer version;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}

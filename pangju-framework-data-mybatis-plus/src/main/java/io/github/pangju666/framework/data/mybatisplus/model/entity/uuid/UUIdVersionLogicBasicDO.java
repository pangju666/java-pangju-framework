package io.github.pangju666.framework.data.mybatisplus.model.entity.uuid;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.pangju666.framework.data.mybatisplus.model.entity.base.VersionLogicBasicDO;

public abstract class UUIdVersionLogicBasicDO extends VersionLogicBasicDO<String> implements UUId {
	@TableId(type = IdType.ASSIGN_UUID)
	protected String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}

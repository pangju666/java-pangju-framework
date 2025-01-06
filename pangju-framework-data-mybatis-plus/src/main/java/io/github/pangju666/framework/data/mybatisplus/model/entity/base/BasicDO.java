package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

public abstract class BasicDO implements Serializable {
	protected Date createTime;
	@TableField(update = "CURRENT_TIMESTAMP")
	protected Date updateTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}

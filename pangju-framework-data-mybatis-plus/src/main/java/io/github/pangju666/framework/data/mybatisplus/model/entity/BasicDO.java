package io.github.pangju666.framework.data.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

public abstract class BasicDO extends AutoIDBasicDO {
	protected Date createTime;
	@TableField(update = "CURRENT_TIMESTAMP")
	protected Date updateTime;

	@Deprecated(forRemoval = true)
	public static String formatId(Long id) {
		String idStr = id.toString();
		return "0".repeat(Math.max(0, 4 - idStr.length())) + id;
	}

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

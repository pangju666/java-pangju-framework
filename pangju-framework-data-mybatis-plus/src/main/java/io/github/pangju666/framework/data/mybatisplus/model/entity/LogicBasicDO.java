package io.github.pangju666.framework.data.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.github.pangju666.framework.data.mybatisplus.annotation.TableLogicFill;

import java.util.Date;

public abstract class LogicBasicDO extends BasicDO {
	@TableLogicFill("CURRENT_TIMESTAMP")
	protected Date deleteTime;
	@TableLogic(value = "0", delval = "id")
	protected Long deleteStatus;

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public Long getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Long deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
}

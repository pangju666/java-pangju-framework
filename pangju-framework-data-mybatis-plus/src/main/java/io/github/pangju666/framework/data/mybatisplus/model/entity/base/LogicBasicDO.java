package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableLogic;

import java.util.Date;

public abstract class LogicBasicDO<ID> extends BasicDO {
	protected Date deleteTime;
	@TableLogic(value = "0", delval = "id")
	protected ID deleteStatus;

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public ID getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(ID deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
}

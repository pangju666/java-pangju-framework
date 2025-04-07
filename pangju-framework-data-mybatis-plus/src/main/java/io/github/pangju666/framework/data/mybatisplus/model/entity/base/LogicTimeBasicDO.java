package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.util.Date;

public abstract class LogicTimeBasicDO extends BasicDO {
	@TableField("delete_time")
	@TableLogic(value = "null", delval = "CURRENT_TIMESTAMP")
	protected Date deleteTime;

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}
}

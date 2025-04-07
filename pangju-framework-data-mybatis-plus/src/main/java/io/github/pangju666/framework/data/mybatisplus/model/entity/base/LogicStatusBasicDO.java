package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

public abstract class LogicStatusBasicDO<ID> extends BasicDO {
	@TableField("delete_status")
	@TableLogic(value = "0", delval = "id")
	protected ID deleteStatus;

	public ID getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(ID deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
}

package io.github.pangju666.framework.data.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;

public abstract class LogicStatusBasicDO extends BasicDO {
    @TableLogic(value = "0", delval = "id")
    protected Long deleteStatus;

    public Long getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Long deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
}

/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import io.github.pangju666.commons.lang.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 * <p>
 * 提供基础的创建时间和更新时间字段。
 * 所有实体类的基类，实现了序列化接口。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class BasicDO implements Serializable {
	/**
	 * 创建时间，默认为当前时间
	 *
	 * @since 1.0.0
	 */
	@TableField("create_time")
	protected Date createTime = DateUtils.nowDate();
	/**
	 * 更新时间，数据更新时自动设置为当前时间
	 *
	 * @since 1.0.0
	 */
	@TableField(value = "update_time", update = "CURRENT_TIMESTAMP")
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

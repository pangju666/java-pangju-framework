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

package io.github.pangju666.framework.data.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.util.Date;

/**
 * 逻辑删除时间基础实体类
 * <p>
 * 在{@link BaseEntity}基础上增加了删除时间字段，
 * 用于支持仅使用时间戳的逻辑删除功能。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class LogicTimeBaseEntity extends BaseEntity {
	/**
	 * 删除时间，null表示未删除，删除时设置为当前时间戳
	 *
	 * @since 1.0.0
	 */
	@TableField("delete_time")
	@TableLogic(value = "null", delval = "CURRENT_TIMESTAMP")
	private Date deleteTime;

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}
}

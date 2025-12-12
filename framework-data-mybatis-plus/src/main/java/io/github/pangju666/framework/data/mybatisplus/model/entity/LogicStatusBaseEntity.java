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

/**
 * 逻辑删除状态基础实体类
 * <p>
 * 在{@link BaseEntity}基础上增加了删除状态字段，
 * 用于支持仅使用状态标记的逻辑删除功能。
 * </p>
 *
 * @param <ID> ID的类型参数
 * @author pangju666
 * @since 1.0.0
 */
public abstract class LogicStatusBaseEntity<ID> extends BaseEntity {
	/**
	 * 删除状态，0表示未删除，删除时设置为表数据行ID
	 *
	 * @since 1.0.0
	 */
	@TableField("delete_status")
	@TableLogic(value = "0", delval = "id")
	private ID deleteStatus;

	public ID getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(ID deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
}

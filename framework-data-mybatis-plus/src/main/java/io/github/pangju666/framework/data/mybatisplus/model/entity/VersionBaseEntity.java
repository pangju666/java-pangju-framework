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
import com.baomidou.mybatisplus.annotation.Version;

/**
 * 乐观锁基础实体类
 * <p>
 * 在{@link BaseEntity}基础上增加了版本号字段，
 * 用于支持乐观锁功能。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class VersionBaseEntity extends BaseEntity {
	/**
	 * 版本号，用于乐观锁控制
	 *
	 * @since 1.0.0
	 */
	@TableField("version")
	@Version
	private Integer version;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}

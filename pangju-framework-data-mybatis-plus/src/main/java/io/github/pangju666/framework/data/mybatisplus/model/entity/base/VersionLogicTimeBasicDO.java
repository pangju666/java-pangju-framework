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
import com.baomidou.mybatisplus.annotation.Version;

/**
 * 乐观锁和逻辑删除时间基础实体类
 * <p>
 * 结合了{@link VersionBasicDO}和{@link LogicTimeBasicDO}的功能，
 * 同时支持乐观锁和时间戳的逻辑删除功能。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class VersionLogicTimeBasicDO extends LogicTimeBasicDO {
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

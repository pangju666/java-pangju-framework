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

package io.github.pangju666.framework.data.mybatisplus.model.entity.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.pangju666.framework.data.mybatisplus.model.entity.base.VersionLogicTimeBasicEntity;

/**
 * 自增ID、乐观锁和逻辑删除时间基础实体类
 * <p>
 * 基于{@link VersionLogicTimeBasicEntity}，使用自增ID作为主键ID。
 * 适用于需要自增主键、乐观锁和时间戳逻辑删除功能的实体类。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class AutoIdVersionLogicTimeBasicEntity extends VersionLogicTimeBasicEntity implements AutoId {
	/**
	 * 自增主键ID
	 *
	 * @since 1.0.0
	 */
	@TableId(type = IdType.AUTO)
	protected Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}

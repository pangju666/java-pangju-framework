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

import io.github.pangju666.framework.data.mybatisplus.model.entity.base.Id;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自增ID接口
 * <p>
 * 定义使用自增Long类型作为主键ID的实体类接口。
 * 提供从实体集合中提取ID的工具方法。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public interface AutoId extends Id<Long> {
	/**
	 * 获取实体集合中的ID列表
	 * <p>
	 * 提取集合中所有非空的ID值，保持原有顺序。
	 * </p>
	 *
	 * @param collection 实体集合
	 * @return ID列表，如果集合为空则返回空列表
	 * @since 1.0.0
	 */
	static List<Long> getIdList(final Collection<? extends AutoId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(AutoId::getId)
			.filter(Objects::nonNull)
			.toList();
	}

	/**
	 * 获取实体集合中的ID集合
	 * <p>
	 * 提取集合中所有非空的ID值，自动去重。
	 * </p>
	 *
	 * @param collection 实体集合
	 * @return ID集合，如果集合为空则返回空集合
	 * @since 1.0.0
	 */
	static Set<Long> getIdSet(final Collection<? extends AutoId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(AutoId::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	/**
	 * 获取实体集合中的唯一ID列表
	 * <p>
	 * 提取集合中所有非空的ID值，去重并保持顺序。
	 * </p>
	 *
	 * @param collection 实体集合
	 * @return 去重后的ID列表，如果集合为空则返回空列表
	 * @since 1.0.0
	 */
	static List<Long> getUniqueIdList(final Collection<? extends AutoId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(AutoId::getId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();
	}
}

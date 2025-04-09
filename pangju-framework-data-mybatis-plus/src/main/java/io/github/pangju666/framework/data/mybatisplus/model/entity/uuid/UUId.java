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

package io.github.pangju666.framework.data.mybatisplus.model.entity.uuid;

import io.github.pangju666.framework.data.mybatisplus.model.entity.base.Id;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UUID主键接口
 * <p>
 * 定义使用UUID字符串类型作为主键ID的实体类接口。
 * 提供从实体集合中提取UUID的工具方法。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public interface UUId extends Id<String> {
	/**
	 * 获取实体集合中的UUID列表
	 * <p>
	 * 提取集合中所有非空的UUID值，保持原有顺序。
	 * 会过滤掉空白字符串。
	 * </p>
	 *
	 * @param collection 实体集合
	 * @return UUID列表，如果集合为空则返回空列表
	 */
	static List<String> getIdList(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(StringUtils::isNotBlank)
			.toList();
	}

	/**
	 * 获取实体集合中的UUID集合
	 * <p>
	 * 提取集合中所有非空的UUID值，自动去重。
	 * 会过滤掉空白字符串。
	 * </p>
	 *
	 * @param collection 实体集合
	 * @return UUID集合，如果集合为空则返回空集合
	 */
	static Set<String> getIdSet(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.toSet());
	}

	/**
	 * 获取实体集合中的唯一UUID列表
	 * <p>
	 * 提取集合中所有非空的UUID值，去重并保持顺序。
	 * 会过滤掉空白字符串。
	 * </p>
	 *
	 * @param collection 实体集合
	 * @return 去重后的UUID列表，如果集合为空则返回空列表
	 */
	static List<String> getUniqueIdList(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(StringUtils::isNotBlank)
			.distinct()
			.toList();
	}
}

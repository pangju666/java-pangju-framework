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
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.pangju666.commons.lang.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
public abstract class BaseEntity implements Serializable {
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

	public static <E extends BaseEntity, V> List<V> getFieldValueList(final Collection<E> collection,
																	  final SFunction<E, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(sFunction)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	static <E extends BaseEntity, V> Set<V> getFieldValueSet(final Collection<E> collection,
															 final SFunction<E, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(sFunction)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	static <E extends BaseEntity, V> List<V> getUniqueFieldValueList(final Collection<E> collection,
																	 final SFunction<E, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(sFunction)
			.filter(Objects::nonNull)
			.distinct()
			.collect(Collectors.toList());
	}

	static <E extends BaseEntity, V> Map<V, E> mapByField(final Collection<E> collection, final SFunction<E, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.collect(Collectors.toMap(sFunction, item -> item));
	}

	static <E extends BaseEntity, V> Map<V, List<E>> groupByField(final Collection<E> collection, final SFunction<E, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.collect(Collectors.groupingBy(sFunction, Collectors.mapping(
				item -> item, Collectors.toList())));
	}

	static <E extends BaseEntity> long sumFieldValue(final Collection<E> collection, final SFunction<E, Number> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return 0;
		}
		return collection.stream()
			.mapToLong(item -> sFunction.apply(item).longValue())
			.filter(Objects::nonNull)
			.sum();
	}

	static <E extends BaseEntity> Double averageFieldValue(final Collection<E> collection, final SFunction<E, Number> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return 0d;
		}
		return collection.stream()
			.mapToDouble(item -> sFunction.apply(item).doubleValue())
			.filter(Objects::nonNull)
			.average()
			.orElse(0d);
	}

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

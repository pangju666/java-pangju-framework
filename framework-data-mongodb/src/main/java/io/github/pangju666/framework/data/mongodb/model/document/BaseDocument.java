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

package io.github.pangju666.framework.data.mongodb.model.document;

import io.github.pangju666.framework.data.mongodb.pool.MongoConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MongoDB基础文档类
 *
 * <ul>
 * <li>提供通用的ID处理功能。</li>
 * <li>使用{@link ObjectId}的十六进制字符串作为文档ID。</li>
 * <li>实现了序列化接口以支持序列化操作。</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class BaseDocument implements Serializable {
	/**
	 * 文档ID
	 * <p>使用MongoDB的_id字段，类型为字符串</p>
	 *
	 * @since 1.0.0
	 */
	@MongoId(value = FieldType.STRING)
	@Field(name = MongoConstants.ID_FIELD_NAME)
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取文档集合中的ID列表
	 * <p>
	 * 提取集合中所有非空的文档ID，保持原有顺序。
	 * 会过滤掉空白字符串。
	 * </p>
	 *
	 * @param collection 文档集合
	 * @return ID列表，如果集合为空则返回空列表
	 * @since 1.0.0
	 */
	public static List<String> getIdList(final Collection<? extends BaseDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(BaseDocument::getId)
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.toList());
	}

	/**
	 * 获取文档集合中的ID集合
	 * <p>
	 * 提取集合中所有非空的文档ID，自动去重。
	 * 会过滤掉空白字符串。
	 * </p>
	 *
	 * @param collection 文档集合
	 * @return ID集合，如果集合为空则返回空集合
	 * @since 1.0.0
	 */
	public static Set<String> getIdSet(final Collection<? extends BaseDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(BaseDocument::getId)
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.toSet());
	}

	/**
	 * 获取文档集合中的唯一ID列表
	 * <p>
	 * 提取集合中所有非空的文档ID，去重并保持顺序。
	 * 会过滤掉空白字符串。
	 * </p>
	 *
	 * @param collection 文档集合
	 * @return 去重后的ID列表，如果集合为空则返回空列表
	 * @since 1.0.0
	 */
	public static List<String> getUniqueIdList(final Collection<? extends BaseDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(BaseDocument::getId)
			.filter(StringUtils::isNotBlank)
			.distinct()
			.collect(Collectors.toList());
	}

	public static Map<String, ? extends BaseDocument> mapByField(final Collection<? extends BaseDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.collect(Collectors.toMap(BaseDocument::getId, item -> item));
	}

	public static <E extends BaseDocument> Map<String, List<E>> groupByField(final Collection<E> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.collect(Collectors.groupingBy(BaseDocument::getId, Collectors.mapping(
				item -> item, Collectors.toList())));
	}
}
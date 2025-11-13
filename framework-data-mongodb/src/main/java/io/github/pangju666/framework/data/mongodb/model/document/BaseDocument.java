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

import io.github.pangju666.framework.data.mongodb.lang.MongoConstants;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MongoDB 基础文档类。
 *
 * <ul>
 *   <li>统一提供文档 ID 的定义与常用处理能力（读取、设置、转换）。</li>
 *   <li>默认将文档 ID 映射为字符串（{@link FieldType#STRING}），通常为 {@link ObjectId} 的 24 位十六进制表示。</li>
 *   <li>提供集合工具方法以便批量提取 ID（列表、集合）与按 ID 构建映射。</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 */
public abstract class BaseDocument implements Serializable {
	/**
	 * 文档 ID。
	 * <p>
	 * 映射到 MongoDB 的 {@code _id} 字段，类型为字符串（{@link FieldType#STRING}），字段名为
	 * {@link MongoConstants#ID_FIELD_NAME}。一般情况下，该字符串为 {@link ObjectId} 的 24 位十六进制表示。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	@MongoId(value = FieldType.STRING)
	@Field(name = MongoConstants.ID_FIELD_NAME)
	private String id;

	/**
	 * 以文档 ID 为键构建映射。
	 * <p>
	 * 将输入集合中的每个文档以其 {@code id} 映射到对应的文档实例；当输入集合为空时返回空映射。
	 * 该实现会过滤掉空白或 {@code null} 的 ID。
	 * </p>
	 *
	 * <p>注意：</p>
	 * <ul>
	 *   <li>若出现重复 ID，将抛出 {@link IllegalStateException}（由 {@link Collectors#toMap} 的默认策略触发）。</li>
	 *   <li>过滤后生成的 Map 不包含空键。</li>
	 * </ul>
	 *
	 * @param collection 文档集合
	 * @return 以非空白 ID 为键的映射；当集合为空或无有效 ID 时返回空映射
	 * @throws IllegalStateException 当存在重复 ID 时抛出
	 * @since 1.0.0
	 */
	public static Map<String, ? extends BaseDocument> mapById(final Collection<? extends BaseDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.filter(item -> StringUtils.isNotBlank(item.getId()))
			.collect(Collectors.toMap(BaseDocument::getId, item -> item));
	}

	/**
	 * 获取文档 ID。
	 *
	 * @return 文档 ID；可能为 {@code null}
	 * @since 1.0.0
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置文档 ID。
	 * <p>
	 * 该方法不进行格式校验；若需要将 ID 转换为 {@link ObjectId} 请使用 {@link #getObjectId()}，并确保 ID 为合法的 24 位十六进制字符串。
	 * </p>
	 *
	 * @param id 文档 ID；允许为 {@code null}
	 * @since 1.0.0
	 */
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
	 * 将字符串 ID 转换为 {@link ObjectId}。
	 *
	 * <ul>
	 *   <li>当 {@code id} 为空时返回 {@code null}。</li>
	 *   <li>当 {@code id} 非法（不是 24 位十六进制字符串）时，抛出 {@link IllegalArgumentException}。</li>
	 * </ul>
	 *
	 * @return 对应的 {@link ObjectId}；当 ID 为空时返回 {@code null}
	 * @throws IllegalArgumentException 当 ID 非法时抛出
	 * @since 1.0.0
	 */
	public ObjectId getObjectId() {
		return Objects.nonNull(id) ? new ObjectId(id) : null;
	}
}
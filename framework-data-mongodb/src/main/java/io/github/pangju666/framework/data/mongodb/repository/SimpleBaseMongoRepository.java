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

package io.github.pangju666.framework.data.mongodb.repository;

import io.github.pangju666.framework.data.mongodb.utils.QueryUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;

/**
 * {@code BaseMongoRepository}的基础实现
 *
 * @param <T>  实体类型
 * @param <ID> ID类型
 * @author pangju666
 * @see BaseMongoRepository
 * @see SimpleMongoRepository
 * @since 1.0.0
 */
public class SimpleBaseMongoRepository<T, ID> extends SimpleMongoRepository<T, ID> implements BaseMongoRepository<T, ID> {
	/**
	 * 底层 Mongo 操作入口。
	 *
	 * @since 1.0.0
	 */
	protected final MongoOperations mongoOperations;

	/**
	 * 实体元信息（包含实体类型、集合名、主键属性等）。
	 *
	 * @since 1.0.0
	 */
	protected final MongoEntityInformation<T, ID> entityInformation;

	/**
	 * 实体 Java 类型。
	 *
	 * @since 1.0.0
	 */
	protected final Class<T> entityClass;

	/**
	 * 目标集合名称。
	 *
	 * @since 1.0.0
	 */
	protected final String collectionName;

	/**
	 * 构造函数。
	 *
	 * @param metadata        实体元信息
	 * @param mongoOperations Mongo 操作对象
	 * @since 1.0.0
	 */
	public SimpleBaseMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
		super(metadata, mongoOperations);
		this.mongoOperations = mongoOperations;
		this.entityInformation = metadata;
		this.entityClass = metadata.getJavaType();
		this.collectionName = metadata.getCollectionName();
	}

	/**
	 * 将可迭代对象转换为集合。
	 *
	 * @param iterable 可迭代对象
	 * @param <E>      元素类型
	 * @return 集合视图（会过滤 null 元素）
	 * @since 1.0.0
	 */
	protected static <E> Collection<E> toCollection(Iterable<E> iterable) {
		if (iterable instanceof Collection<E> collection) {
			return collection.stream()
				.filter(Objects::nonNull)
				.toList();
		}
		return StreamUtils.createStreamFromIterator(iterable.iterator()).toList();
	}

	/**
	 * 判断是否存在指定键与值匹配的文档。
	 *
	 * @param key   字段名（不可为空）
	 * @param value 字段值（可为 null）
	 * @return 是否存在匹配文档
	 */
	@Override
	public boolean existsByKeyValue(String key, @Nullable Object value) {
		Query query = QueryUtils.queryByKeyValue(key, value);
		return mongoOperations.exists(query, this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件判断是否存在匹配文档。
	 *
	 * @param query 查询条件（为 null 时返回 false）
	 * @return 是否存在匹配文档
	 */
	@Override
	public boolean exists(Query query) {
		if (Objects.isNull(query)) {
			return false;
		}
		return mongoOperations.exists(query, this.entityClass, this.collectionName);
	}

	/**
	 * 根据键值匹配查询单个文档。
	 *
	 * @param key   字段名（不可为空）
	 * @param value 字段值（可为 null）
	 * @return 匹配的文档，可能为空
	 */
	@Override
	public Optional<T> findOneByKeyValue(String key, @Nullable Object value) {
		Query query = QueryUtils.queryByKeyValue(key, value);
		return Optional.ofNullable(mongoOperations.findOne(query, this.entityClass, this.collectionName));
	}

	/**
	 * 根据查询条件查询单个文档。
	 *
	 * @param query 查询条件（为 null 时返回空）
	 * @return 匹配的文档，可能为空
	 */
	@Override
	public Optional<T> findOne(Query query) {
		if (Objects.isNull(query)) {
			return Optional.empty();
		}
		return Optional.ofNullable(mongoOperations.findOne(query, this.entityClass, this.collectionName));
	}

	/**
	 * 统计查询条件匹配的文档数量。
	 *
	 * @param query 查询条件（为 null 时返回 0）
	 * @return 匹配文档数量
	 */
	@Override
	public long count(Query query) {
		if (Objects.isNull(query)) {
			return 0;
		}
		return mongoOperations.count(query, this.collectionName);
	}

	/**
	 * 获取指定字段的去重值列表。
	 *
	 * @param key        字段名（不可为空）
	 * @param valueClass 值类型（不可为 null）
	 * @param <V>        返回值类型
	 * @return 去重后的值列表
	 */
	@Override
	public <V> List<V> findDistinctKeyValues(String key, Class<V> valueClass) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(valueClass, "valueClass 不可为null");

		Query query = QueryUtils.queryByKeyNotNull(key);
		return mongoOperations.findDistinct(query, key, this.collectionName, this.entityClass, valueClass);
	}

	/**
	 * 根据查询条件获取指定字段的去重值列表。
	 *
	 * @param query      查询条件（为 null 时返回空列表）
	 * @param key        字段名（不可为空）
	 * @param valueClass 值类型（不可为 null）
	 * @param <V>        返回值类型
	 * @return 去重后的值列表
	 */
	@Override
	public <V> List<V> findDistinctKeyValues(Query query, String key, Class<V> valueClass) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(valueClass, "valueClass 不可为null");

		if (Objects.isNull(query)) {
			return Collections.emptyList();
		}
		return mongoOperations.findDistinct(query, key, this.collectionName, this.entityClass, valueClass);
	}

	/**
	 * 根据查询条件查询所有匹配的文档。
	 *
	 * @param query 查询条件（为 null 时返回空列表）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAll(Query query) {
		if (Objects.isNull(query)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query, entityClass, collectionName);
	}

	/**
	 * 查询字段等于指定值的所有文档。
	 *
	 * @param key   字段名（不可为空）
	 * @param value 字段值（可为 null）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyValue(String key, @Nullable Object value) {
		return findAll(QueryUtils.queryByKeyValue(key, value));
	}

	/**
	 * 查询字段不等于指定值的所有文档。
	 *
	 * @param key   字段名（不可为空）
	 * @param value 字段值（可为 null）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyNotValue(String key, @Nullable Object value) {
		return findAll(QueryUtils.queryByKeyNotValue(key, value));
	}

	/**
	 * 查询字段在给定集合中的所有文档。
	 *
	 * @param key    字段名（不可为空）
	 * @param values 值集合（空集合返回空列表）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyValues(String key, Iterable<?> values) {
		Assert.hasText(key, "key 不可为空");

		if (IterableUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		Collection<?> collection = toCollection(values);
		if (collection.isEmpty()) {
			return Collections.emptyList();
		}
		return findAll(QueryUtils.queryByKeyValues(key, collection));
	}

	/**
	 * 查询字段不在给定集合中的所有文档。
	 *
	 * @param key    字段名（不可为空）
	 * @param values 值集合（空集合返回空列表）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyNotValues(String key, Iterable<?> values) {
		Assert.hasText(key, "key 不可为空");

		if (IterableUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		Collection<?> collection = toCollection(values);
		if (collection.isEmpty()) {
			return Collections.emptyList();
		}
		return findAll(QueryUtils.queryByKeyNotValues(key, collection));
	}

	/**
	 * 查询字段值为 null 的所有文档。
	 *
	 * @param key 字段名（不可为空）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyNull(String key) {
		return findAll(QueryUtils.queryByKeyNull(key));
	}

	/**
	 * 查询字段值不为 null 的所有文档。
	 *
	 * @param key 字段名（不可为空）
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyNotNull(String key) {
		return findAll(QueryUtils.queryByKeyNotNull(key));
	}

	/**
	 * 查询字段不匹配指定正则表达式的所有文档。
	 *
	 * @param key   字段名（不可为空）
	 * @param regex 正则表达式
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyNotRegex(String key, String regex) {
		return findAll(QueryUtils.queryByKeyNotRegex(key, regex));
	}

	/**
	 * 查询字段不匹配指定正则模式的所有文档。
	 *
	 * @param key     字段名（不可为空）
	 * @param pattern 正则模式
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyNotRegex(String key, Pattern pattern) {
		return findAll(QueryUtils.queryByKeyNotRegex(key, pattern));
	}

	/**
	 * 查询字段匹配指定正则表达式的所有文档。
	 *
	 * @param key   字段名（不可为空）
	 * @param regex 正则表达式
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyRegex(String key, String regex) {
		return findAll(QueryUtils.queryByKeyRegex(key, regex));
	}

	/**
	 * 查询字段匹配指定正则模式的所有文档。
	 *
	 * @param key     字段名（不可为空）
	 * @param pattern 正则模式
	 * @return 匹配的文档列表
	 */
	@Override
	public List<T> findAllByKeyRegex(String key, Pattern pattern) {
		return findAll(QueryUtils.queryByKeyRegex(key, pattern));
	}

	/**
	 * 根据查询条件进行分页查询。
	 *
	 * @param pageable 分页参数（不可为 null）
	 * @param query    查询条件（为 null 时返回空分页）
	 * @return 分页结果
	 */
	@Override
	public Page<T> findAll(Pageable pageable, Query query) {
		Assert.notNull(pageable, "pageable 不可为null");

		if (Objects.isNull(query)) {
			return Page.empty();
		}
		long count = count(query);
		List<T> list = findAll(query.with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	/**
	 * 根据主键更新单条文档。
	 *
	 * @param update 更新内容（不可为 null）
	 * @param id     主键值（不可为 null）
	 */
	@Override
	public void updateById(UpdateDefinition update, ID id) {
		Assert.notNull(update, "update 不可为null");
		Assert.notNull(id, "id 不可为null");

		Query query = Query.query(Criteria.where(entityInformation.getIdAttribute()).is(id));
		mongoOperations.updateFirst(query, update, this.entityClass, this.collectionName);
	}

	/**
	 * 根据主键集合批量更新文档。
	 *
	 * @param update 更新内容（不可为 null）
	 * @param ids    主键集合（为空时不执行）
	 */
	@Override
	public void updateAllById(UpdateDefinition update, Iterable<ID> ids) {
		Assert.notNull(update, "update 不可为null");

		Collection<?> collection = toCollection(ids);
		if (collection.isEmpty()) {
			return;
		}

		Query query = new Query(new Criteria(entityInformation.getIdAttribute())
			.in(collection));
		mongoOperations.updateMulti(query, update, this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件批量更新文档。
	 *
	 * @param update 更新内容（不可为 null）
	 * @param query  查询条件（为 null 时不执行）
	 */
	@Override
	public void updateAll(UpdateDefinition update, Query query) {
		Assert.notNull(update, "update 不可为null");

		if (Objects.isNull(query)) {
			return;
		}
		mongoOperations.updateMulti(query, update, this.entityClass, this.collectionName);
	}

	/**
	 * 将指定字段的旧值批量更新为新值。
	 *
	 * @param key      字段名（不可为空）
	 * @param newValue 新值（可为 null）
	 * @param oldValue 旧值（可为 null）
	 * @param <V>      字段值类型
	 */
	@Override
	public <V> void updateAllByKeyValue(String key, @Nullable V newValue, @Nullable V oldValue) {
		Assert.hasText(key, "key 不可为空");

		mongoOperations.updateMulti(Query.query(Criteria.where(key).is(oldValue)),
			new Update().set(key, newValue), this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件删除匹配的文档。
	 *
	 * @param query 查询条件（为 null 时不执行）
	 */
	@Override
	public void deleteAll(Query query) {
		if (Objects.nonNull(query)) {
			mongoOperations.remove(query, this.entityClass, this.collectionName);
		}
	}
}
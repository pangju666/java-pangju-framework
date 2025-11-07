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

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.data.mongodb.utils.QueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MongoDB基础仓储类
 * <p>
 * 提供MongoDB文档操作的基础功能，包括：
 * <ul>
 *     <li>文档的增删改查操作</li>
 *     <li>单字段条件查询</li>
 *     <li>批量操作支持</li>
 *     <li>正则表达式查询</li>
 *     <li>分页查询</li>
 *     <li>流式处理</li>
 *     <li>空值处理</li>
 * </ul>
 * </p>
 *
 * <p>
 * 主要特性：
 * <ul>
 *     <li>自动处理集合名称映射</li>
 *     <li>提供批量操作的并行处理选项</li>
 *     <li>统一的异常处理</li>
 *     <li>支持MongoDB的主要查询操作符</li>
 *     <li>支持Spring Data的分页和排序功能</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Repository
 * public class UserRepository extends BaseRepository<User> {
 *     public UserRepository(MongoOperations mongoOperations) {
 *         super(mongoOperations);
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @param <ID> ID类型
 * @param <T>  实体类型
 * @author pangju666
 * @see QueryUtils
 * @since 1.0.0
 */
public abstract class BaseRepository<ID, T> {
	/**
	 * 实体类类型
	 *
	 * @since 1.0.0
	 */
	protected Class<T> entityClass;
	/**
	 * MongoDB操作类
	 *
	 * @since 1.0.0
	 */
	protected MongoOperations mongoOperations;
	/**
	 * 集合名称
	 *
	 * @since 1.0.0
	 */
	protected String collectionName;

	/**
	 * 默认构造函数
	 * <p>该构造函数执行以下操作：</p>
	 * <ol>
	 *     <li>通过反射获取子类的第二个泛型参数类型（实体类型）</li>
	 *     <li>检查实体类是否标注了{@link Document}注解</li>
	 *     <li>从注解中提取集合名称，优先使用value属性，如果为空则使用collection属性</li>
	 *     <li>设置{@link #collectionName}字段，用于后续MongoDB操作</li>
	 * </ol>
	 * <p>注意：此构造函数仅初始化实体类型和集合名称，不设置{@link #mongoOperations}，
	 * 通常需要配合{@link #setMongoOperations(MongoOperations)}方法使用，
	 * 或者使用{@link #BaseRepository(MongoOperations)}构造函数。</p>
	 *
	 * @see Document
	 * @since 1.0.0
	 */
	protected BaseRepository() {
		this.entityClass = getClassGenericType(this.getClass());
		if (Objects.nonNull(this.entityClass)) {
			String collectionName = null;
			Document document = this.entityClass.getAnnotation(Document.class);
			if (Objects.nonNull(document)) {
				collectionName = document.value();
				if (StringUtils.isEmpty(collectionName)) {
					collectionName = document.collection();
				}
			}
			this.collectionName = collectionName;
		}
	}

	/**
	 * 构造函数
	 *
	 * @param mongoOperations MongoDB操作类
	 * @since 1.0.0
	 */
	protected BaseRepository(MongoOperations mongoOperations) {
		this();
		setMongoOperations(mongoOperations);
	}

	/**
	 * 获取MongoDB操作类实例
	 *
	 * @return MongoDB操作类实例
	 * @since 1.0.0
	 */
	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	/**
	 * 设置MongoDB操作类实例
	 * <p>该方法执行以下操作：</p>
	 * <ol>
	 *     <li>验证传入的{@code mongoOperations}参数不为null</li>
	 *     <li>将参数赋值给{@link #mongoOperations}字段</li>
	 *     <li>如果{@link #collectionName}为空，则通过{@code mongoOperations}获取实体类对应的集合名称</li>
	 * </ol>
	 * <p>此方法通常在以下情况下使用：</p>
	 * <ul>
	 *     <li>使用默认构造函数创建仓储实例后，需要设置MongoDB操作类</li>
	 *     <li>需要在运行时更改MongoDB操作类实例</li>
	 * </ul>
	 *
	 * @param mongoOperations MongoDB操作类实例，不能为null
	 * @throws IllegalArgumentException 如果{@code mongoOperations}为null
	 * @see MongoOperations#getCollectionName(Class)
	 * @since 1.0.0
	 */
	public void setMongoOperations(MongoOperations mongoOperations) {
		Assert.notNull(mongoOperations, "mongoOperations 不可为null");

		this.mongoOperations = mongoOperations;
		if (StringUtils.isBlank(this.collectionName)) {
			this.collectionName = this.mongoOperations.getCollectionName(entityClass);
		}
	}

	/**
	 * 获取MongoDB集合名称
	 *
	 * @return 集合名称
	 * @since 1.0.0
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * 获取实体类类型
	 *
	 * @return 实体类的Class对象
	 * @since 1.0.0
	 */
	public Class<T> getEntityClass() {
		return entityClass;
	}

	/**
	 * 检查指定字段是否存在指定值的文档
	 * <p>
	 * <ul>
	 *     <li>当value为null时，检查字段是否存在非null值</li>
	 *     <li>当value不为null时，检查字段是否存在等于value的值</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要检查的字段名
	 * @param value 要检查的字段值
	 * @return 如果存在返回true，否则返回false
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public <V> boolean existByKeyValue(String key, Object value) {
		return mongoOperations.exists(QueryUtils.queryByKeyValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 检查指定字段是否不存在指定值的文档
	 * <p>
	 * <ul>
	 *     <li>当value为null时，检查字段是否不存在非null值</li>
	 *     <li>当value不为null时，检查字段是否不存在等于value的值</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要检查的字段名
	 * @param value 要检查的字段值
	 * @return 如果不存在返回true，否则返回false
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public boolean notExistByKeyValue(String key, Object value) {
		return mongoOperations.exists(QueryUtils.queryByKeyNotValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 根据字符串ID检查文档是否存在
	 *
	 * @param id 文档ID
	 * @return 如果存在返回true，否则返回false
	 * @throws IllegalArgumentException 当id为空时抛出
	 * @since 1.0.0
	 */
	public boolean existsById(ID id) {
		return mongoOperations.exists(QueryUtils.queryById(id), this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件检查文档是否存在
	 *
	 * @param query MongoDB查询条件
	 * @return 如果存在返回true，否则返回false
	 * @throws IllegalArgumentException 当query为null时抛出
	 * @since 1.0.0
	 */
	public boolean exist(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.exists(query, this.entityClass, this.collectionName);
	}

	/**
	 * 根据字段名和值查询单个文档
	 * <p>
	 * <ul>
	 *     <li>当value为null时，查询字段值为null的文档</li>
	 *     <li>当value不为null时，查询字段值等于value的文档</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param value 要查询的字段值
	 * @return 匹配的文档，如果没有找到则返回null
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public <V> T getByKeyValue(String key, Object value) {
		return mongoOperations.findOne(QueryUtils.queryByKeyValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 根据字符串ID查询单个文档
	 * <p>
	 * 使用MongoDB的_id字段进行精确匹配查询
	 * </p>
	 *
	 * @param id 文档ID
	 * @return 匹配的文档，如果没有找到则返回null
	 * @throws IllegalArgumentException 当id为空时抛出
	 * @since 1.0.0
	 */
	public T getById(ID id) {
		Assert.notNull(id, "id 不可为null");

		return mongoOperations.findById(id, this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件获取单个文档
	 * <p>
	 * 使用自定义查询条件进行匹配，返回第一个匹配的文档
	 * </p>
	 *
	 * @param query MongoDB查询条件
	 * @return 匹配的文档，如果没有找到则返回null
	 * @throws IllegalArgumentException 当query为null时抛出
	 * @since 1.0.0
	 */
	public T get(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.findOne(query, this.entityClass, this.collectionName);
	}

	/**
	 * 获取集合中所有文档的数量
	 * <p>
	 * 不带任何查询条件，返回整个集合的文档总数
	 * </p>
	 *
	 * @return 集合中的文档总数
	 * @since 1.0.0
	 */
	public long count() {
		return mongoOperations.count(QueryUtils.emptyQuery(), this.collectionName);
	}

	/**
	 * 根据查询条件获取匹配的文档数量
	 * <p>
	 * 使用指定的查询条件过滤文档，返回符合条件的文档数量
	 * </p>
	 *
	 * @param query MongoDB查询条件
	 * @return 匹配查询条件的文档数量
	 * @throws IllegalArgumentException 当query为null时抛出
	 * @since 1.0.0
	 */
	public long count(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.count(query, this.collectionName);
	}

	/**
	 * 获取指定字段的所有不重复值
	 * <p>
	 * 使用MongoDB的distinct命令查询指定字段的所有不同值。
	 * 此方法不带任何查询条件，将返回集合中该字段的所有唯一值。
	 * </p>
	 *
	 * @param key        要查询的字段名
	 * @param valueClass 字段值的类型Class对象
	 * @param <V>        返回值的泛型类型
	 * @return 字段的所有不重复值列表
	 * @throws IllegalArgumentException 当key为空或valueClass为null时抛出
	 * @since 1.0.0
	 */
	public <V> List<V> listDistinctKeyValues(String key, Class<V> valueClass) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(valueClass, "valueClass 不可为null");

		return mongoOperations.findDistinct(QueryUtils.emptyQuery(), key, this.collectionName, this.entityClass,
			valueClass);
	}

	/**
	 * 根据查询条件获取指定字段的不重复值
	 * <p>
	 * 使用MongoDB的distinct命令在指定查询条件下查询字段的不同值。
	 * 此方法允许通过查询条件过滤文档，只返回匹配文档中该字段的唯一值。
	 * </p>
	 *
	 * @param query      MongoDB查询条件
	 * @param key        要查询的字段名
	 * @param valueClass 字段值的类型Class对象
	 * @param <V>        返回值的泛型类型
	 * @return 符合查询条件的文档中，字段的所有不重复值列表
	 * @throws IllegalArgumentException 当query为null、key为空或valueClass为null时抛出
	 * @since 1.0.0
	 */
	public <V> List<V> listDistinctKeyValues(Query query, String key, Class<V> valueClass) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(valueClass, "valueClass 不可为null");
		Assert.notNull(query, "query 不可为不可为null");

		return mongoOperations.findDistinct(query, key, this.collectionName, this.entityClass, valueClass);
	}

	/**
	 * 根据ID集合批量查询文档
	 * <p>
	 * <ul>
	 *     <li>过滤空ID和空白字符串ID</li>
	 *     <li>如果没有有效ID，返回空列表</li>
	 *     <li>使用MongoDB的$in操作符进行批量查询</li>
	 * </ul>
	 * </p>
	 *
	 * @param ids 要查询的ID集合
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @since 1.0.0
	 */
	public List<T> listByIds(Collection<ID> ids) {
		List<ID> validIds = CollectionUtils.isEmpty(ids) ? Collections.emptyList() : ids
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validIds.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(QueryUtils.queryByIds(validIds), this.entityClass, this.collectionName);
	}

	/**
	 * 查询集合中的所有文档
	 *
	 * @return 集合中的所有文档列表
	 * @since 1.0.0
	 */
	public List<T> list() {
		return mongoOperations.findAll(this.entityClass, this.collectionName);
	}

	/**
	 * 根据排序条件查询所有文档
	 *
	 * @param sort 排序条件
	 * @return 排序后的文档列表
	 * @throws IllegalArgumentException 当sort为null时抛出
	 * @since 1.0.0
	 */
	public List<T> list(Sort sort) {
		Assert.notNull(sort, "sort 不可为null");

		return mongoOperations.find(new Query().with(sort), this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件获取文档列表
	 *
	 * @param query MongoDB查询条件
	 * @return 匹配查询条件的文档列表
	 * @throws IllegalArgumentException 当query为null时抛出
	 * @since 1.0.0
	 */
	public List<T> list(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.find(query, this.entityClass, this.collectionName);
	}

	/**
	 * 根据字段名和值查询文档列表
	 * <p>
	 * <ul>
	 *     <li>当value为null时，查询字段值为null的所有文档</li>
	 *     <li>当value不为null时，查询字段值等于value的所有文档</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param value 要查询的字段值
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByKeyValue(String key, Object value) {
		return mongoOperations.find(QueryUtils.queryByKeyValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 根据字段名和值集合批量查询文档
	 * <p>
	 * <ul>
	 *     <li>过滤集合中的null值</li>
	 *     <li>如果过滤后的值集合为空，返回空列表</li>
	 *     <li>使用MongoDB的$in操作符进行批量查询</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    要查询的字段名
	 * @param values 要查询的字段值集合
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByKeyValues(String key, Collection<?> values) {
		List<?> validValues = CollectionUtils.isEmpty(values) ? Collections.emptyList() : values
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(QueryUtils.queryByKeyValues(key, validValues), this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件、字段名和值集合批量查询文档
	 * <p>
	 * <ul>
	 *     <li>在现有查询条件基础上添加字段值匹配条件</li>
	 *     <li>过滤集合中的null值</li>
	 *     <li>如果过滤后的值集合为空，返回空列表</li>
	 *     <li>使用MongoDB的$in操作符进行批量查询</li>
	 * </ul>
	 * </p>
	 *
	 * @param query  MongoDB查询条件
	 * @param key    要查询的字段名
	 * @param values 要查询的字段值集合
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByKeyValues(Query query, String key, Collection<?> values) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		List<?> validValues = CollectionUtils.isEmpty(values) ? Collections.emptyList() : values
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).in(validValues)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段值为null的文档列表
	 * <p>
	 * 使用MongoDB的$or操作符组合以下条件：
	 * <ul>
	 *     <li>字段值为null</li>
	 *     <li>字段不存在</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByNullValue(String key) {
		return mongoOperations.find(QueryUtils.queryByKeyNullValue(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段值不为null的文档列表
	 * <p>
	 * 使用MongoDB的$or操作符组合以下条件：
	 * <ul>
	 *     <li>字段值不为null</li>
	 *     <li>字段存在且有值</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByNotNullValue(String key) {
		return mongoOperations.find(QueryUtils.queryByKeyNotNullValue(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段不匹配正则表达式的文档列表
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档列表，如果正则表达式为空则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByNotRegex(String key, String regex) {
		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(QueryUtils.queryByNotRegex(key, regex), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段不匹配正则表达式模式的文档列表
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档列表，如果pattern为null则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByNotRegex(String key, Pattern pattern) {
		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(QueryUtils.queryByNotRegex(key, pattern), this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段不匹配正则表达式的条件
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param query 现有的MongoDB查询条件
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档列表，如果正则表达式为空则返回空列表
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByNotRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).not().regex(regex)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段不匹配正则表达式模式的条件
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param query   现有的MongoDB查询条件
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档列表，如果pattern为null则返回空列表
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByNotRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).not().regex(pattern)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段匹配正则表达式的文档列表
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档列表，如果正则表达式为空则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByRegex(String key, String regex) {
		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(QueryUtils.queryByRegex(key, regex), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段匹配正则表达式模式的文档列表
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档列表，如果pattern为null则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByRegex(String key, Pattern pattern) {
		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(QueryUtils.queryByRegex(key, pattern), this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段匹配正则表达式的条件
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param query 现有的MongoDB查询条件
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档列表，如果正则表达式为空则返回空列表
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).regex(regex)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段匹配正则表达式模式的条件
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param query   现有的MongoDB查询条件
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档列表，如果pattern为null则返回空列表
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).regex(pattern)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 根据ID集合批量查询文档并返回流
	 * <p>
	 * <ul>
	 *     <li>过滤空ID和空白字符串ID</li>
	 *     <li>如果没有有效ID，返回空流</li>
	 *     <li>使用MongoDB的$in操作符进行批量查询</li>
	 * </ul>
	 * </p>
	 *
	 * @param ids 要查询的ID集合
	 * @return 匹配的文档流，如果没有匹配则返回空流
	 * @since 1.0.0
	 */
	public Stream<T> streamByIds(Collection<ID> ids) {
		List<ID> validIds = CollectionUtils.isEmpty(ids) ? Collections.emptyList() : ids
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validIds.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(QueryUtils.queryByIds(validIds), this.entityClass, this.collectionName);
	}

	/**
	 * 根据字段名和值查询文档并返回流
	 * <p>
	 * <ul>
	 *     <li>当value为null时，查询字段值为null的所有文档</li>
	 *     <li>当value不为null时，查询字段值等于value的所有文档</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param value 要查询的字段值
	 * @return 匹配的文档流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByKeyValue(String key, Object value) {
		return mongoOperations.stream(QueryUtils.queryByKeyValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 根据字段名和值集合批量查询文档并返回流
	 * <p>
	 * <ul>
	 *     <li>过滤集合中的null值</li>
	 *     <li>如果过滤后的值集合为空，返回空流</li>
	 *     <li>使用MongoDB的$in操作符进行批量查询</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    要查询的字段名
	 * @param values 要查询的字段值集合
	 * @return 匹配的文档流，如果没有匹配则返回空流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByKeyValues(String key, Collection<?> values) {
		List<?> validValues = CollectionUtils.isEmpty(values) ? Collections.emptyList() : values
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(QueryUtils.queryByKeyValue(key, values), this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件、字段名和值集合批量查询文档并返回流
	 * <p>
	 * <ul>
	 *     <li>在现有查询条件基础上添加字段值匹配条件</li>
	 *     <li>过滤集合中的null值</li>
	 *     <li>如果过滤后的值集合为空，返回空流</li>
	 *     <li>使用MongoDB的$in操作符进行批量查询</li>
	 * </ul>
	 * </p>
	 *
	 * @param query  MongoDB查询条件
	 * @param key    要查询的字段名
	 * @param values 要查询的字段值集合
	 * @return 匹配的文档流，如果没有匹配则返回空流
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByKeyValues(Query query, String key, Collection<?> values) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		List<?> validValues = CollectionUtils.isEmpty(values) ? Collections.emptyList() : values
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).in(validValues)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段不匹配正则表达式的文档并返回流
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档流，如果正则表达式为空则返回空流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByNotRegex(String key, String regex) {
		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(QueryUtils.queryByNotRegex(key, regex), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段不匹配正则表达式模式的文档并返回流
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档流，如果pattern为null则返回空流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByNotRegex(String key, Pattern pattern) {
		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(QueryUtils.queryByNotRegex(key, pattern), this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段不匹配正则表达式的条件并返回流
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param query 现有的MongoDB查询条件
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档流，如果正则表达式为空则返回空流
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByNotRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).not().regex(regex)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段不匹配正则表达式模式的条件并返回流
	 * <p>
	 * 使用MongoDB的$not和$regex操作符组合进行字符串模式匹配
	 * </p>
	 *
	 * @param query   现有的MongoDB查询条件
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档流，如果pattern为null则返回空流
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByNotRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).not().regex(pattern)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段匹配正则表达式的文档并返回流
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档流，如果正则表达式为空则返回空流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByRegex(String key, String regex) {
		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(QueryUtils.queryByNotRegex(key, regex), this.entityClass,
			this.collectionName);
	}

	/**
	 * 查询指定字段匹配正则表达式模式的文档并返回流
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档流，如果pattern为null则返回空流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByRegex(String key, Pattern pattern) {
		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(QueryUtils.queryByNotRegex(key, pattern),
			this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段匹配正则表达式的条件并返回流
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param query 现有的MongoDB查询条件
	 * @param key   要查询的字段名
	 * @param regex 正则表达式字符串
	 * @return 匹配的文档流，如果正则表达式为空则返回空流
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).regex(regex)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 在现有查询条件基础上添加字段匹配正则表达式模式的条件并返回流
	 * <p>
	 * 使用MongoDB的$regex操作符进行字符串模式匹配
	 * </p>
	 *
	 * @param query   现有的MongoDB查询条件
	 * @param key     要查询的字段名
	 * @param pattern Java正则表达式模式
	 * @return 匹配的文档流，如果pattern为null则返回空流
	 * @throws IllegalArgumentException 当query为null或key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).regex(pattern)),
			this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段值为null的文档并返回流
	 * <p>
	 * 使用MongoDB的$or操作符组合以下条件：
	 * <ul>
	 *     <li>字段值为null</li>
	 *     <li>字段不存在</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByNullValue(String key) {
		return mongoOperations.stream(QueryUtils.queryByKeyNullValue(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段值不为null的文档并返回流
	 * <p>
	 * 使用MongoDB的$or操作符组合以下条件：
	 * <ul>
	 *     <li>字段值不为null</li>
	 *     <li>字段存在且有值</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByNotNullValue(String key) {
		return mongoOperations.stream(QueryUtils.queryByKeyNotNullValue(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询集合中的所有文档并返回流
	 * <p>
	 * 不带任何查询条件，返回整个集合的文档流
	 * </p>
	 *
	 * @return 包含所有文档的流
	 * @since 1.0.0
	 */
	public Stream<T> stream() {
		return mongoOperations.stream(QueryUtils.emptyQuery(), this.entityClass, this.collectionName);
	}

	/**
	 * 根据排序条件查询所有文档并返回流
	 * <p>
	 * 对集合中的所有文档进行排序并返回文档流
	 * </p>
	 *
	 * @param sort 排序条件
	 * @return 排序后的文档流
	 * @throws IllegalArgumentException 当sort为null时抛出
	 * @since 1.0.0
	 */
	public Stream<T> stream(Sort sort) {
		Assert.notNull(sort, "sort 不可为null");

		return mongoOperations.stream(new Query().with(sort), this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件获取文档流
	 * <p>
	 * 使用指定的查询条件过滤文档并返回文档流
	 * </p>
	 *
	 * @param query MongoDB查询条件
	 * @return 匹配查询条件的文档流
	 * @throws IllegalArgumentException 当query为null时抛出
	 * @since 1.0.0
	 */
	public Stream<T> stream(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.stream(query, this.entityClass, this.collectionName);
	}

	/**
	 * 分页查询所有文档
	 * <p>
	 * 不带任何查询条件，对整个集合进行分页
	 * </p>
	 *
	 * @param pageable 分页参数
	 * @return 分页结果
	 * @throws IllegalArgumentException 当pageable为null时抛出
	 * @since 1.0.0
	 */
	public Page<T> page(Pageable pageable) {
		Assert.notNull(pageable, "pageable 不可为null");

		long count = count();
		List<T> list = list(new Query().with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	/**
	 * 根据排序条件分页查询所有文档
	 * <p>
	 * 对集合中的所有文档进行排序和分页
	 * </p>
	 *
	 * @param pageable 分页参数
	 * @param sort     排序条件
	 * @return 排序和分页后的结果
	 * @throws IllegalArgumentException 当pageable或sort为null时抛出
	 * @since 1.0.0
	 */
	public Page<T> page(Pageable pageable, Sort sort) {
		Assert.notNull(pageable, "pageable 不可为null");
		Assert.notNull(sort, "sort 不可为null");

		long count = count();
		List<T> list = list(new Query().with(pageable).with(sort));
		return new PageImpl<>(list, pageable, count);
	}

	/**
	 * 根据查询条件分页查询文档
	 * <p>
	 * 使用指定的查询条件过滤文档并进行分页
	 * </p>
	 *
	 * @param pageable 分页参数
	 * @param query    MongoDB查询条件
	 * @return 查询条件过滤和分页后的结果
	 * @throws IllegalArgumentException 当pageable或query为null时抛出
	 * @since 1.0.0
	 */
	public Page<T> page(Pageable pageable, Query query) {
		Assert.notNull(pageable, "pageable 不可为null");
		Assert.notNull(query, "query 不可为null");

		long count = count(query);
		List<T> list = list(query.with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	/**
	 * 根据查询条件和排序条件分页查询文档
	 * <p>
	 * 使用指定的查询条件过滤文档，并进行排序和分页
	 * </p>
	 *
	 * @param pageable 分页参数
	 * @param query    MongoDB查询条件
	 * @param sort     排序条件
	 * @return 查询条件过滤、排序和分页后的结果
	 * @throws IllegalArgumentException 当pageable、query或sort为null时抛出
	 * @since 1.0.0
	 */
	public Page<T> page(Pageable pageable, Query query, Sort sort) {
		Assert.notNull(pageable, "pageable 不可为null");
		Assert.notNull(query, "query 不可为null");
		Assert.notNull(sort, "sort 不可为null");

		long count = count(query);
		List<T> list = list(query.with(pageable).with(sort));
		return new PageImpl<>(list, pageable, count);
	}

	/**
	 * 插入单个文档
	 * <p>
	 * 将实体对象插入到MongoDB集合中
	 * </p>
	 *
	 * @param entity 要插入的实体对象
	 * @return 插入后的实体对象（包含MongoDB自动生成的ID）
	 * @throws IllegalArgumentException 当entity为null时抛出
	 * @since 1.0.0
	 */
	public T insert(T entity) {
		Assert.notNull(entity, "entity 不可为null");

		return mongoOperations.insert(entity, this.collectionName);
	}

	/**
	 * 批量插入文档
	 * <p>
	 * <ul>
	 *     <li>过滤集合中的null元素</li>
	 *     <li>如果过滤后的集合为空，返回空列表</li>
	 *     <li>使用MongoDB的批量插入操作提高性能</li>
	 * </ul>
	 * </p>
	 *
	 * @param entities 要插入的实体对象集合
	 * @return 插入后的实体对象集合
	 * @since 1.0.0
	 */
	public Collection<T> insertBatch(Collection<T> entities) {
		List<T> validaEntities = CollectionUtils.isEmpty(entities) ? Collections.emptyList() : entities
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validaEntities.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.insert(validaEntities, this.collectionName);
	}

	/**
	 * 保存单个文档
	 * <p>
	 * 使用MongoDB的save操作保存文档：
	 * <ul>
	 *     <li>如果文档不存在则插入新文档</li>
	 *     <li>如果文档已存在则更新现有文档</li>
	 * </ul>
	 * </p>
	 *
	 * @param entity 要保存的实体对象
	 * @throws IllegalArgumentException 当entity为null时抛出
	 * @since 1.0.0
	 */
	public void save(T entity) {
		Assert.notNull(entity, "entity 不可为null");

		mongoOperations.save(entity, this.collectionName);
	}

	/**
	 * 批量保存文档（默认使用并行处理）
	 * <p>
	 * 调用重载方法{@link #saveBatch(Collection, boolean)}，默认启用并行处理
	 * </p>
	 *
	 * @param entities 要保存的实体对象集合
	 * @return 保存后的实体对象集合
	 * @since 1.0.0
	 */
	public Collection<T> saveBatch(Collection<T> entities) {
		return saveBatch(entities, true);
	}

	/**
	 * 批量保存文档（可选并行处理）
	 * <p>
	 * <ul>
	 *     <li>过滤集合中的null元素</li>
	 *     <li>根据parallel参数选择使用并行流或普通流处理</li>
	 *     <li>对每个文档执行save操作：</li>
	 *     <ul>
	 *         <li>如果文档不存在则插入新文档</li>
	 *         <li>如果文档已存在则更新现有文档</li>
	 *     </ul>
	 * </ul>
	 * </p>
	 *
	 * @param entities 要保存的实体对象集合
	 * @param parallel 是否启用并行处理
	 * @return 保存后的实体对象集合
	 * @since 1.0.0
	 */
	public Collection<T> saveBatch(Collection<T> entities, boolean parallel) {
		if (parallel) {
			return CollectionUtils.isEmpty(entities) ? Collections.emptyList() : entities
				.parallelStream()
				.filter(Objects::nonNull)
				.map(validaEntity -> mongoOperations.save(validaEntity, this.collectionName))
				.collect(Collectors.toList());
		} else {
			return CollectionUtils.isEmpty(entities) ? Collections.emptyList() : entities
				.stream()
				.filter(Objects::nonNull)
				.map(validaEntity -> mongoOperations.save(validaEntity, this.collectionName))
				.collect(Collectors.toList());
		}
	}

	/**
	 * 根据ID更新文档的指定字段值
	 * <p>
	 * 使用MongoDB的$set操作符更新单个字段
	 * </p>
	 *
	 * @param key   要更新的字段名
	 * @param value 新的字段值
	 * @param id    文档ID
	 * @return 如果更新成功返回true，否则返回false
	 * @throws IllegalArgumentException 当key或id为空时抛出
	 * @since 1.0.0
	 */
	public boolean updateKeyValueById(String key, Object value, ID id) {
		Assert.hasText(key, "key 不可为空");

		UpdateResult result = mongoOperations.updateFirst(QueryUtils.queryById(id), new Update().set(key, value),
			this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getModifiedCount() == 1;
	}

	/**
	 * 批量替换字段的旧值为新值
	 * <p>
	 * <ul>
	 *     <li>当oldValue为null时，查找字段值为null的文档</li>
	 *     <li>当oldValue不为null时，查找字段值等于oldValue的文档</li>
	 *     <li>使用MongoDB的$set操作符更新匹配的所有文档</li>
	 * </ul>
	 * </p>
	 *
	 * @param key      要更新的字段名
	 * @param newValue 新的字段值
	 * @param oldValue 原字段值
	 * @param <V>      字段值的类型
	 * @return 成功更新的文档数量
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public <V> long replaceKeyValue(String key, V newValue, V oldValue) {
		Assert.hasText(key, "key 不可为空");

		UpdateResult result = mongoOperations.updateMulti(QueryUtils.queryByKeyValue(key, oldValue),
			new Update().set(key, newValue), this.entityClass, this.collectionName);
		return result.wasAcknowledged() ? result.getModifiedCount() : 0;
	}

	/**
	 * 根据ID删除单个文档
	 * <p>
	 * 使用MongoDB的remove操作删除指定ID的文档
	 * </p>
	 *
	 * @param id 要删除的文档ID
	 * @return 如果删除成功返回true，否则返回false
	 * @throws IllegalArgumentException 当id为空时抛出
	 * @since 1.0.0
	 */
	public boolean removeById(ID id) {
		Assert.notNull(id, "id 不可为null");

		DeleteResult result = mongoOperations.remove(QueryUtils.queryById(id), this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == 1;
	}

	/**
	 * 根据ID集合批量删除文档
	 * <p>
	 * <ul>
	 *     <li>过滤集合中的空字符串和空白字符串ID</li>
	 *     <li>如果过滤后的ID集合为空，返回0</li>
	 *     <li>使用MongoDB的$in操作符进行批量删除</li>
	 * </ul>
	 * </p>
	 *
	 * @param ids 要删除的文档ID集合
	 * @return 成功删除的文档数量
	 * @since 1.0.0
	 */
	public long removeByIds(Collection<ID> ids) {
		List<ID> validIds = CollectionUtils.isEmpty(ids) ? Collections.emptyList() : ids
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validIds.isEmpty()) {
			return 0;
		}
		DeleteResult result = mongoOperations.remove(QueryUtils.queryByIds(validIds), this.entityClass, this.collectionName);
		return result.wasAcknowledged() ? result.getDeletedCount() : 0;
	}

	/**
	 * 根据查询条件删除文档
	 * <p>
	 * 使用MongoDB的remove操作删除匹配查询条件的所有文档
	 * </p>
	 *
	 * @param query MongoDB查询条件
	 * @return 成功删除的文档数量
	 * @throws IllegalArgumentException 当query为null时抛出
	 * @since 1.0.0
	 */
	public long remove(Query query) {
		Assert.notNull(query, "query 不可为null");

		DeleteResult result = mongoOperations.remove(query, this.entityClass, this.collectionName);
		return result.wasAcknowledged() ? result.getDeletedCount() : 0;
	}

	@SuppressWarnings("unchecked")
	private Class<T> getClassGenericType(Class<?> clazz) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return null;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (1 >= params.length) {
			return null;
		}
		if (!(params[1] instanceof Class)) {
			return null;
		}
		return (Class<T>) params[1];
	}
}
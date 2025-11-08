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
import io.github.pangju666.framework.data.mongodb.model.document.BaseDocument;
import io.github.pangju666.framework.data.mongodb.utils.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MongoDB 基础仓储抽象类
 * <p>
 * 封装 Spring Data MongoDB 的常用文档操作与查询能力，提供：
 * <ul>
 *     <li>存在性/单条获取/计数/列表/分页/排序/流式等通用操作</li>
 *     <li>按单字段进行等值、不等值、空值/非空值、正则匹配的查询</li>
 *     <li>按 ID 或值集合进行批量查询与批量更新/删除</li>
 *     <li>插入、保存（存在则更新）、替换字段值等修改操作</li>
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
 * <p>
 * 参数与约束总则：
 * <ul>
 *     <li><b>key/id</b>：遵循非空且非空白约束（多数方法使用 {@code Assert.hasText} 校验）。若方法未在仓储层显式校验，调用方需自行保证有效性。</li>
 *     <li><b>集合参数</b>：批量方法会过滤集合中的 {@code null} 与空白字符串元素；过滤后为空时返回空结果或 {@code false}。</li>
 *     <li><b>Optional 返回</b>：未匹配到文档时统一返回 {@code Optional.empty()}。</li>
 *     <li><b>null 值语义</b>：匹配“值为 {@code null}”与“字段缺失”并不等价；如需匹配缺失字段应使用 {@code $exists:false}。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 性能与使用提示：
 * <ul>
 *     <li><b>索引前提</b>：建议为常用字段（如 {@code _id}、查询字段 {@code key}）建立合适索引，以保障查询与更新效率。</li>
 *     <li><b>正则查询</b>：正则匹配通常不索引友好；建议限制前缀锚点并使用 {@link java.util.regex.Pattern} 控制 flags，避免慢查询与潜在 ReDoS 风险。</li>
 *     <li><b>分页计数</b>：分页会进行 {@code count} 与数据查询；复杂或超大集合场景下可考虑跳过计数或使用估算策略。</li>
 *     <li><b>批量操作</b>：超大批次建议改用批量管道（例如 {@code BulkOperations}）；当前实现会逐条/多条更新，调用方需控制批次大小。</li>
 *     <li><b>流式查询</b>：{@code stream*} 方法基于游标；请使用 try-with-resources 或在消费结束时关闭流以释放资源。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 成功判定语义：
 * <ul>
 *     <li><b>单条删除</b>（如 {@code removeById}）：确认且恰好影响 1 条视为成功。</li>
 *     <li><b>批量删除/更新</b>（如 {@code removeByIds}、{@code updateByIds}）：确认且影响条数大于 0 视为成功。</li>
 *     <li><b>按 ID 局部更新</b>（{@code updateById(Update, String)}）：确认且实际修改 1 条（如值未变化则视为未修改）。</li>
 *     <li><b>整文替换</b>（{@code updateById(T)}）：会覆盖未在对象中的字段，仅在可构造完整文档时使用。</li>
 * </ul>
 * </p>
 *
 * @param <T> 实体类型
 * @author pangju666
 * @see QueryUtils
 * @since 1.0.0
 */
public abstract class BaseRepository<T extends BaseDocument> {
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
	 * 检查是否存在指定字段匹配给定值的文档
	 * <p>
	 * 语义与 {@link QueryUtils#queryByKeyValue(String, Object)} 一致：
	 * <ul>
	 *     <li>当 {@code value} 为 {@code null}：存在“字段值为 {@code null} 或字段不存在”的文档。</li>
	 *     <li>当 {@code value} 非 {@code null}：存在“字段值等于 {@code value}”的文档。</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要检查的字段名
	 * @param value 要检查的字段值
	 * @return 如果存在返回true，否则返回false
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public <V> boolean existByValue(String key, @Nullable Object value) {
		return mongoOperations.exists(QueryUtils.queryByKeyValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 检查是否存在指定字段不匹配给定值的文档
	 * <p>
	 * 语义与 {@link QueryUtils#queryByKeyNotValue(String, Object)} 一致：
	 * <ul>
	 *     <li>当 {@code value} 为 {@code null}：存在“字段存在且值不为 {@code null}”的文档。</li>
	 *     <li>当 {@code value} 非 {@code null}：存在“字段值不等于 {@code value}”的文档。</li>
	 * </ul>
	 * </p>
	 *
	 * @param key   要检查的字段名
	 * @param value 要检查的字段值
	 * @return 如果不存在返回true，否则返回false
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public boolean notExistByValue(String key, @Nullable Object value) {
		return mongoOperations.exists(QueryUtils.queryByKeyNotValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 根据字符串ID检查文档是否存在
	 *
	 * @param id 文档ID
	 * @return 如果存在返回true，否则返回false
	 * @throws IllegalArgumentException 当 id 为空或仅空白时抛出
	 * @since 1.0.0
	 */
	public boolean existsById(String id) {
		Assert.hasText(id, "id 不可为空");

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
	public T getByKeyValue(String key, @Nullable Object value) {
		return mongoOperations.findOne(QueryUtils.queryByKeyValue(key, value), this.entityClass, this.collectionName);
	}

	/**
	 * 根据键值查询单个文档（Optional 封装）
	 * <p>
	 * 使用指定字段进行精确匹配，返回首个匹配结果的 Optional。
	 * 当 value 为 null 时，遵循空值语义：匹配字段不存在或值为 null。
	 * </p>
	 *
	 * @param key   字段名，不能为空或仅空白
	 * @param value 字段值，可为 null（按空值语义匹配）
	 * @return 首个匹配文档的 Optional；未找到时为 {@code Optional.empty()}
	 * @throws IllegalArgumentException 当 key 为空或仅空白时抛出
	 * @since 1.0.0
	 */
	public Optional<T> getOptByKeyValue(String key, @Nullable Object value) {
		return Optional.ofNullable(mongoOperations.findOne(QueryUtils.queryByKeyValue(key, value), this.entityClass,
			this.collectionName));
	}

	/**
	 * 根据字符串 ID 查询单个文档
	 * <p>
	 * 使用 MongoDB 的 <code>_id</code> 字段进行精确匹配。
	 * </p>
	 *
	 * @param id 文档 ID，不能为空或仅空白
	 * @return 匹配的文档；未找到时返回 {@code null}
	 * @throws IllegalArgumentException 当 id 为空或仅空白时抛出
	 * @since 1.0.0
	 */
	public T getById(String id) {
		Assert.hasText(id, "id 不可为空");

		return mongoOperations.findById(id, this.entityClass, this.collectionName);
	}

	/**
	 * 根据字符串 ID 查询单个文档（Optional 封装）
	 * <p>
	 * 使用 MongoDB 的 <code>_id</code> 字段进行精确匹配。
	 * </p>
	 *
	 * @param id 文档 ID，不能为空或仅空白
	 * @return 匹配文档的 Optional；未找到时为 {@code Optional.empty()}
	 * @throws IllegalArgumentException 当 id 为空或仅空白时抛出
	 * @since 1.0.0
	 */
	public Optional<T> getOptById(String id) {
		Assert.hasText(id, "id 不可为空");

		return Optional.ofNullable(mongoOperations.findById(id, this.entityClass, this.collectionName));
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
	public T getOne(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.findOne(query, this.entityClass, this.collectionName);
	}

	/**
	 * 根据查询条件获取单个文档（Optional 封装）
	 * <p>
	 * 返回满足条件的首个文档；当不存在匹配时返回 {@code Optional.empty()}。
	 * </p>
	 *
	 * @param query 查询条件，不能为空
	 * @return 首个匹配文档的 Optional；未找到时为 {@code Optional.empty()}
	 * @throws IllegalArgumentException 当 query 为 null 时抛出
	 * @since 1.0.0
	 */
	public Optional<T> getOptOne(Query query) {
		Assert.notNull(query, "query 不可为null");

		return Optional.ofNullable(mongoOperations.findOne(query, this.entityClass, this.collectionName));
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
	 * <p>
	 * 说明：distinct仅统计“字段存在”的值；字段缺失的文档不参与统计。若需排除{@code null}值，请结合查询条件使用 {@code $exists:true} 与 {@code $ne:null}。
	 * </p>
	 *
	 * @param key        要查询的字段名
	 * @param valueClass 字段值的类型Class对象
	 * @param <V>        返回值的泛型类型
	 * @return 字段的所有不重复值列表
	 * @throws IllegalArgumentException 当key为空或valueClass为null时抛出
	 * @since 1.0.0
	 */
	public <V> List<V> listUniqueKeyValues(String key, Class<V> valueClass) {
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
	 * <p>
	 * 说明：distinct仅统计“字段存在”的值；对于缺失字段的文档不会产生值。如需排除{@code null}，请在查询中加入 {@code $exists:true} 与 {@code $ne:null}。
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
	public <V> List<V> listUniqueKeyValues(Query query, String key, Class<V> valueClass) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(valueClass, "valueClass 不可为null");
		Assert.notNull(query, "query 不可为null");

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
	 * 过滤集合中的 {@code null} 与空白字符串 ID；当有效 ID 为空时返回空列表。
	 *
	 * @param ids 要查询的ID集合
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @since 1.0.0
	 */
	public List<T> listByIds(Collection<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<String> validIds = ids.stream()
			.filter(StringUtils::isNotBlank)
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
	public List<T> listByKeyValue(String key, @Nullable Object value) {
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
		Assert.hasText(key, "key 不可为空");

		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		List<?> validValues = values.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(key).in(validValues)), this.entityClass,
			this.collectionName);
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
	 * <p>
	 * 注意：如需仅匹配“值为{@code null}”而排除“字段缺失”的情况，请在查询中显式添加 {@code $exists:true} 条件以限定字段存在。
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByKeyNull(String key) {
		return mongoOperations.find(QueryUtils.queryByKeyNull(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段值不为 null 的文档列表
	 * <p>
	 * 使用 MongoDB 的 $and 操作符组合以下条件：
	 * <ul>
	 *     <li>字段存在</li>
	 *     <li>字段值不为 {@code null}</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 说明：该查询会排除“字段缺失”的文档，仅匹配“字段存在且值非{@code null}”。
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档列表，如果没有匹配则返回空列表
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public List<T> listByKeyNotNull(String key) {
		return mongoOperations.find(QueryUtils.queryByKeyNotNull(key), this.entityClass, this.collectionName);
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
	public List<T> listByKeyNotRegex(String key, String regex) {
		return mongoOperations.find(QueryUtils.queryByKeyNotRegex(key, regex), this.entityClass, this.collectionName);
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
	public List<T> listByKeyNotRegex(String key, Pattern pattern) {
		return mongoOperations.find(QueryUtils.queryByKeyNotRegex(key, pattern), this.entityClass, this.collectionName);
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
	public List<T> listByKeyRegex(String key, String regex) {
		return mongoOperations.find(QueryUtils.queryByKeyRegex(key, regex), this.entityClass, this.collectionName);
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
	public List<T> listByKeyRegex(String key, Pattern pattern) {
		return mongoOperations.find(QueryUtils.queryByKeyRegex(key, pattern), this.entityClass, this.collectionName);
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
	 * 过滤集合中的 {@code null} 与空白字符串 ID；当有效 ID 为空时返回空流。
	 *
	 * @param ids 要查询的ID集合
	 * @return 匹配的文档流，如果没有匹配则返回空流
	 * @since 1.0.0
	 */
	public Stream<T> streamByIds(Collection<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Stream.empty();
		}
		List<String> validIds = ids.stream()
			.filter(StringUtils::isNotBlank)
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
	public Stream<T> streamByKeyValue(String key, @Nullable Object value) {
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
		Assert.hasText(key, "key 不可为空");

		if (CollectionUtils.isEmpty(values)) {
			return Stream.empty();
		}
		List<?> validValues = values.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).in(validValues)),
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
	public Stream<T> streamByKeyNotRegex(String key, String regex) {
		return mongoOperations.stream(QueryUtils.queryByKeyNotRegex(key, regex), this.entityClass, this.collectionName);
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
	public Stream<T> streamByKeyNotRegex(String key, Pattern pattern) {
		return mongoOperations.stream(QueryUtils.queryByKeyNotRegex(key, pattern), this.entityClass, this.collectionName);
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
	public Stream<T> streamByKeyRegex(String key, String regex) {
		return mongoOperations.stream(QueryUtils.queryByKeyRegex(key, regex), this.entityClass, this.collectionName);
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
	public Stream<T> streamByKeyRegex(String key, Pattern pattern) {
		return mongoOperations.stream(QueryUtils.queryByKeyRegex(key, pattern), this.entityClass, this.collectionName);
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
	public Stream<T> streamByKeyNull(String key) {
		return mongoOperations.stream(QueryUtils.queryByKeyNull(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询指定字段值不为null的文档并返回流
	 * <p>
	 * 使用MongoDB的 $and 操作符组合以下条件：
	 * <ul>
	 *     <li>字段存在</li>
	 *     <li>字段值不为 {@code null}</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 要查询的字段名
	 * @return 匹配的文档流
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @since 1.0.0
	 */
	public Stream<T> streamByKeyNotNull(String key) {
		return mongoOperations.stream(QueryUtils.queryByKeyNotNull(key), this.entityClass, this.collectionName);
	}

	/**
	 * 查询集合中的所有文档并返回流
	 * <p>
	 * 不带任何查询条件，返回整个集合的文档流。
	 * 流基于游标，请在消费结束或异常时关闭以释放资源，建议使用 try-with-resources。
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
	 * 对集合中的所有文档进行排序并返回文档流。
	 * 流基于游标，请在消费结束或异常时关闭以释放资源，建议使用 try-with-resources。
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
	 * 使用指定的查询条件过滤文档并返回文档流。
	 * 流基于游标，请在消费结束或异常时关闭以释放资源，建议使用 try-with-resources。
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
	 * <p>
	 * 计数说明：总记录数通过独立的 {@link #count()} 操作获取；当集合数据量较大或并发较高时，计数可能成为性能瓶颈，建议确保相关过滤/排序字段具备合适索引以提升整体分页效率。
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
	 * 根据查询条件分页查询文档
	 * <p>
	 * 使用指定的查询条件过滤文档并进行分页
	 * </p>
	 * <p>
	 * 计数说明：总记录数通过 {@link #count(Query)} 在相同查询条件下获取；当过滤条件复杂或涉及大量数据时，计数会带来额外的性能开销。为提升性能，建议为查询条件中涉及的键建立索引，并尽量避免未索引的范围查询。
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

		long count = count(query);
		List<T> list = list(query.with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	/**
	 * 插入单个文档
	 * <p>
	 * 将实体对象插入到MongoDB集合中
	 * </p>
	 *
	 * @return 插入后的实体对象（包含MongoDB自动生成的ID）
	 * @throws IllegalArgumentException 当entity为null时抛出
	 * @since 1.0.0
	 */
	public T insert(T document) {
		Assert.notNull(document, "document 不可为null");

		return mongoOperations.insert(document, this.collectionName);
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
	 * @return 插入后的实体对象集合
	 * @since 1.0.0
	 */
	public Collection<T> insertBatch(Collection<T> documents) {
		if (CollectionUtils.isEmpty(documents)) {
			return Collections.emptyList();
		}
		List<T> validaDocuments = documents.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validaDocuments.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.insert(validaDocuments, this.collectionName);
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
	 * @throws IllegalArgumentException 当entity为null时抛出
	 * @since 1.0.0
	 */
	public T saveOrUpdate(T document) {
		Assert.notNull(document, "document 不可为null");

		return mongoOperations.save(document, this.collectionName);
	}

	/**
	 * 批量保存或更新文档（upsert）
	 * <p>
	 * 对集合中每个非空文档执行保存：存在 <code>_id</code> 时进行替换更新；
	 * 无 <code>_id</code> 或不存在匹配时插入新文档。传入集合为 null 或为空时，返回空列表。
	 * </p>
	 *
	 * @param documents 待保存或更新的文档集合，可为 null；集合中的 null 元素会被忽略
	 * @return 保存后的文档集合（与输入中非空元素一一对应）
	 * @since 1.0.0
	 */
	public Collection<T> saveOrUpdateBatch(Collection<T> documents) {
		if (CollectionUtils.isEmpty(documents)) {
			return Collections.emptyList();
		}
		return documents.stream()
			.filter(Objects::nonNull)
			.map(document -> mongoOperations.save(document, this.collectionName))
			.collect(Collectors.toList());
	}

	/**
	 * 根据文档自身携带的 ID 进行整文替换更新
	 * <p>
	 * 使用 {@link MongoOperations#findAndReplace(Query, Object, String)} 按 ID 查找并替换整条文档：
	 * <ul>
	 *     <li>要求传入实体的 {@code id} 非空；更新过程中会暂时清空 {@code id} 字段避免冲突。</li>
	 *     <li>如果匹配文档存在则替换并返回替换后的文档；否则返回 {@code null}。</li>
	 * </ul>
	 * </p>
	 *
	 * @param document 含 ID 的待更新文档，不能为 {@code null}
	 * @return 替换后的文档，如果未匹配到则返回 {@code null}
	 * @throws IllegalArgumentException 当 {@code document} 为 {@code null} 或 {@code document.id} 为空时抛出
	 * @since 1.0.0
	 */
	public T updateById(T document) {
		Assert.notNull(document, "document 不可为null");
		Assert.hasText(document.getId(), "文档id 不可为空");

		String id = document.getId();
		document.setId(null);
		return mongoOperations.findAndReplace(QueryUtils.queryById(id), document, this.collectionName);
	}

	/**
	 * 根据 ID 更新文档的字段
	 * <p>
	 * 使用 MongoDB 的 `$set` 操作符更新指定字段。
	 * </p>
	 *
	 * @param update 更新操作对象，不能为 {@code null}
	 * @param id 文档 ID，不能为空或仅空白
	 * @return 更新是否成功（确认且仅修改 1 条）
	 * @throws IllegalArgumentException 当 {@code update} 为 {@code null} 或 {@code id} 为空/仅空白时抛出
	 * @since 1.0.0
	 */
	public boolean updateById(Update update, String id) {
		Assert.notNull(update, "update 不可为null");
		Assert.hasText(id, "id 不可为空");

		UpdateResult result = mongoOperations.updateFirst(QueryUtils.queryById(id), update, this.entityClass,
			this.collectionName);
		return result.wasAcknowledged() && result.getMatchedCount() == 1;
	}

	/**
	 * 根据 ID 集合批量更新文档的字段
	 * <p>
	 * 过滤 {@code ids} 集合中的 {@code null} 与空白字符串；当有效 ID 为空时返回 {@code false}。
	 * 使用 MongoDB 的批量更新以 `$set` 生效。
	 * </p>
	 *
	 * @param update 更新操作对象，不能为 {@code null}
	 * @param ids 文档 ID 集合，可为空或包含 {@code null}/空白元素
	 * @return 更新是否被确认且至少修改 1 条
	 * @throws IllegalArgumentException 当 {@code update} 为 {@code null} 时抛出
	 * @since 1.0.0
	 */
	public boolean updateByIds(Update update, Collection<String> ids) {
		Assert.notNull(update, "update 不可为null");

		if (CollectionUtils.isEmpty(ids)) {
			return false;
		}
		List<String> validIds = ids.stream()
			.filter(StringUtils::isNotBlank)
			.toList();
		if (validIds.isEmpty()) {
			return false;
		}

		UpdateResult result = mongoOperations.updateMulti(QueryUtils.queryByIds(validIds), update, this.entityClass,
			this.collectionName);
		return result.wasAcknowledged() && result.getModifiedCount() > 0;
	}

	/**
	 * 替换指定字段的值
	 * <p>
	 * 将字段 {@code key} 等于 {@code oldValue} 的文档更新为 {@code newValue}，使用 MongoDB 的 `$set`。
	 * 支持 {@code oldValue} 为 {@code null}（匹配字段值为 {@code null} 的文档），{@code newValue} 也可为 {@code null}。
	 * </p>
	 *
	 * @param key 字段名，需由调用方保证非空且非空白
	 * @param newValue 新值，可为 {@code null}
	 * @param oldValue 旧值，可为 {@code null}
	 * @return 更新是否被确认且至少修改 1 条
	 * @since 1.0.0
	 */
	public <V> boolean replaceKeyValue(String key, @Nullable V newValue, @Nullable V oldValue) {
		Assert.hasText(key, "key 不可为空");

		UpdateResult result = mongoOperations.updateMulti(Query.query(Criteria.where(key).is(oldValue)),
			new Update().set(key, newValue), this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getModifiedCount() > 0;
	}

	/**
	 * 根据ID删除单个文档
	 * <p>
	 * 使用MongoDB的remove操作删除指定ID的文档
	 * </p>
	 *
	 * @param id 要删除的文档ID
	 * @return 如果删除成功返回true，否则返回false
	 * @throws IllegalArgumentException 当 id 为空或仅空白时抛出
	 * @since 1.0.0
	 */
	public boolean removeById(String id) {
		Assert.hasText(id, "id 不可为空");

		DeleteResult result = mongoOperations.remove(QueryUtils.queryById(id), this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == 1;
	}

	/**
	 * 根据 ID 集合批量删除文档
	 * <p>
	 * 过滤集合中的 {@code null} 与空白字符串 ID；当有效 ID 为空时返回 {@code false}。
	 * 返回删除操作是否被确认。
	 * </p>
	 *
	 * @param ids 要删除的文档 ID 集合，可为空或包含 {@code null}/空白元素
	 * @return 删除操作是否被确认
	 * @since 1.0.0
	 */
	public boolean removeByIds(Collection<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return false;
		}
		List<String> validIds = ids.stream()
			.filter(StringUtils::isNotBlank)
			.toList();
		if (validIds.isEmpty()) {
			return false;
		}

		DeleteResult result = mongoOperations.remove(QueryUtils.queryByIds(validIds), this.entityClass,
			this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() > 0;
	}

	/**
	 * 解析子类声明的泛型实体类型
	 * <p>
	 * 通过读取当前类的泛型超类，提取第一个类型参数并返回其 {@link Class}。
	 * 若无法解析（未使用参数化类型或类型参数非 {@link Class}），返回 {@code null}。
	 * </p>
	 *
	 * @param clazz 当前仓储子类的 {@link Class}
	 * @return 解析得到的实体类型，或 {@code null}
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getClassGenericType(Class<?> clazz) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return null;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (!(params[0] instanceof Class)) {
			return null;
		}
		return (Class<T>) params[0];
	}
}
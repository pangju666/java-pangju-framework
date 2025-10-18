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

package io.github.pangju666.framework.data.mybatisplus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 基础仓库类，扩展了MyBatis-Plus的CrudRepository功能
 * <p>
 * 该类提供了丰富的数据库操作方法，包括但不限于：
 * <ul>
 *     <li>JSON字段查询操作</li>
 *     <li>基于列值的查询操作</li>
 *     <li>批量操作</li>
 *     <li>模糊查询操作</li>
 *     <li>存在性检查</li>
 * </ul>
 * 所有方法都经过空值检查和参数验证，提供了更安全的数据库操作。
 * </p>
 *
 * @param <M> Mapper类型，必须继承自BaseMapper
 * @param <T> 实体类型
 * @author pangju666
 * @since 1.0.0
 */
public abstract class BaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> {
	/**
	 * 默认列表批处理大小，用于分批查询时的批次大小
	 *
	 * @since 1.0.0
	 */
	public static final int DEFAULT_LIST_BATCH_SIZE = 500;
	/**
	 * JSON对象值查询格式，使用MySQL的JSON_CONTAINS函数
	 * <ul>
	 * <li>格式：JSON_CONTAINS(%s->'$.%s', '%s')</li>
	 * <li>参数1：列名</li>
	 * <li>参数2：JSON对象键</li>
	 * <li>参数3：JSON对象值</li>
	 * </ul>
	 *
	 * @since 1.0.0
	 */
	protected static final String JSON_VALUE_EQ_FORMAT = "JSON_CONTAINS(%s->'$.%s', '%s')";
	/**
	 * JSON数组包含查询格式，使用MySQL的JSON_CONTAINS函数
	 * <ul>
	 * <li>格式：JSON_CONTAINS(%s, '%s')</li>
	 * <li>参数1：列名</li>
	 * <li>参数2：JSON数组值</li>
	 * </ul>
	 *
	 * @since 1.0.0
	 */
	protected static final String JSON_ARRAY_CONTAIN_FORMAT = "JSON_CONTAINS(%s, '%s')";

	/**
	 * 根据JSON对象字段值查询实体列表
	 * <p>
	 * 该方法用于查询JSON对象字段中特定键的值等于指定值的实体。
	 * </p>
	 *
	 * @param columnName      列名，JSON类型的列
	 * @param jsonObjectKey   JSON对象中的键
	 * @param jsonObjectValue 期望的JSON对象值
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果columnName、jsonObjectKey为空
	 * @apiNote 此方法仅在<strong>MySQL</strong>数据库环境下测试过
	 * @since 1.0.0
	 */
	public List<T> listByJsonObjectValue(String columnName, String jsonObjectKey, Object jsonObjectValue) {
		return listByJsonObjectValue(lambdaQuery(), columnName, jsonObjectKey, jsonObjectValue);
	}

	/**
	 * 根据JSON对象字段值查询实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法用于在自定义查询条件的基础上，查询JSON对象字段中特定键的值等于指定值的实体。
	 * 注意：此方法仅支持MySQL数据库。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param columnName        列名，JSON类型的列
	 * @param jsonObjectKey     JSON对象中的键
	 * @param jsonObjectValue   期望的JSON对象值
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper为null，或columnName、jsonObjectKey为空
	 * @apiNote 此方法仅在<strong>MySQL</strong>数据库环境下测试过
	 * @since 1.0.0
	 */
	public List<T> listByJsonObjectValue(LambdaQueryChainWrapper<T> queryChainWrapper, String columnName,
										 String jsonObjectKey, Object jsonObjectValue) {
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Assert.hasText(columnName, "columnName 不可为空");
		Assert.hasText(jsonObjectKey, "jsonObjectKey 不可为空");

		return queryChainWrapper.apply(String.format(JSON_VALUE_EQ_FORMAT, columnName, jsonObjectKey,
				getJsonValue(jsonObjectValue)))
			.list();
	}

	/**
	 * 查询指定列为空JSON对象的实体列表
	 * <p>
	 * 该方法用于查询指定列的值为空JSON对象（"{}"）的实体。
	 * </p>
	 *
	 * @param column 实体字段，通常是JSON类型的字段
	 * @param <V>    字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByEmptyJsonObject(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");

		return lambdaQuery()
			.like(column, Constants.EMPTY_JSON_OBJECT_STR)
			.list();
	}

	/**
	 * 查询指定列为空JSON对象的实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法用于在自定义查询条件的基础上，查询指定列的值为空JSON对象（"{}"）的实体。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段，通常是JSON类型的字段
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByEmptyJsonObject(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Assert.notNull(column, "column 不可为null");

		return queryChainWrapper
			.like(column, Constants.EMPTY_JSON_OBJECT_STR)
			.list();
	}

	/**
	 * 根据JSON数组值查询实体列表
	 * <p>
	 * 该方法用于查询JSON数组字段包含指定值的实体。
	 * </p>
	 *
	 * @param columnName     列名，JSON类型的列
	 * @param jsonArrayValue 期望包含的JSON数组值
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果columnName为空
	 * @apiNote 此方法仅在<strong>MySQL</strong>数据库环境下测试过
	 * @since 1.0.0
	 */
	public List<T> listByJsonArrayValue(String columnName, Object jsonArrayValue) {
		return listByJsonArrayValue(lambdaQuery(), columnName, jsonArrayValue);
	}

	/**
	 * 根据JSON数组值查询实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法用于在自定义查询条件的基础上，查询JSON数组字段包含指定值的实体。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param columnName        列名，JSON类型的列
	 * @param jsonArrayValue    期望包含的JSON数组值
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper为null或columnName为空
	 * @apiNote 此方法仅在<strong>MySQL</strong>数据库环境下测试过
	 * @since 1.0.0
	 */
	public List<T> listByJsonArrayValue(LambdaQueryChainWrapper<T> queryChainWrapper, String columnName, Object jsonArrayValue) {
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Assert.hasText(columnName, "columnName 不可为空");

		return queryChainWrapper
			.apply(String.format(JSON_ARRAY_CONTAIN_FORMAT, columnName, getJsonValue(jsonArrayValue)))
			.list();
	}

	/**
	 * 查询指定列为空JSON数组的实体列表
	 * <p>
	 * 该方法用于查询指定列的值为空JSON数组（"[]"）的实体。
	 * </p>
	 *
	 * @param column 实体字段，通常是JSON类型的字段
	 * @param <V>    字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByEmptyJsonArray(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");

		return lambdaQuery()
			.like(column, Constants.EMPTY_JSON_ARRAY_STR)
			.list();
	}

	/**
	 * 查询指定列为空JSON数组的实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法用于在自定义查询条件的基础上，查询指定列的值为空JSON数组（"[]"）的实体。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段，通常是JSON类型的字段
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByEmptyJsonArray(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Assert.notNull(column, "column 不可为null");

		return queryChainWrapper
			.like(column, Constants.EMPTY_JSON_ARRAY_STR)
			.list();
	}

	/**
	 * 检查指定ID的实体是否存在
	 *
	 * @param id 实体ID
	 * @return 如果实体存在返回true，否则返回false
	 * @since 1.0.0
	 */
	public boolean existsById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	/**
	 * 检查指定ID的实体是否不存在
	 *
	 * @param id 实体ID
	 * @return 如果实体不存在返回true，否则返回false
	 * @since 1.0.0
	 */
	public boolean notExistsById(Serializable id) {
		return Objects.isNull(getById(id));
	}

	/**
	 * 检查指定列值的实体是否存在
	 * <p>
	 * 如果value为null，则检查该列是否有非null值的记录存在。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param value  期望的字段值
	 * @param <V>    字段类型
	 * @return 如果符合条件的实体存在返回true，否则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> boolean existsByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNotNull(column)
				.exists();
		}
		return lambdaQuery()
			.eq(column, value)
			.exists();
	}

	/**
	 * 检查指定列值的实体是否不存在
	 *
	 * @param column 实体字段
	 * @param value  期望的字段值
	 * @param <V>    字段类型
	 * @return 如果符合条件的实体不存在返回true，否则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @see #existsByColumnValue(SFunction, Object)
	 * @since 1.0.0
	 */
	public <V> boolean notExistsByColumnValue(SFunction<T, V> column, V value) {
		return !existsByColumnValue(column, value);
	}

	/**
	 * 根据列值获取单个实体
	 * <p>
	 * 如果value为null，则查询该列为null的记录。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param value  期望的字段值
	 * @param <V>    字段类型
	 * @return 符合条件的实体，如果不存在则返回null
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> T getByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.one();
		}
		return lambdaQuery()
			.eq(column, value)
			.one();
	}

	/**
	 * 获取指定列的所有值列表
	 * <p>
	 * 该方法返回指定列的所有非null值，不去重。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param <V>    字段类型
	 * @return 列值列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> List<V> listColumnValue(SFunction<T, V> column) {
		return listColumnValue(lambdaQuery(), column, false, true);
	}

	/**
	 * 获取指定列的所有值列表（使用自定义查询条件）
	 * <p>
	 * 该方法在自定义查询条件的基础上，返回指定列的所有非null值，不去重。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param <V>               字段类型
	 * @return 列值列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<V> listColumnValue(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		return listColumnValue(queryChainWrapper, column, false, true);
	}

	/**
	 * 获取指定列的唯一值列表
	 * <p>
	 * 该方法返回指定列的所有非null值，并进行去重。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param <V>    字段类型
	 * @return 去重后的列值列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> List<V> listUniqueColumnValue(SFunction<T, V> column) {
		return listColumnValue(lambdaQuery(), column, true, true);
	}

	/**
	 * 获取指定列的唯一值列表（使用自定义查询条件）
	 * <p>
	 * 该方法在自定义查询条件的基础上，返回指定列的所有非null值，并进行去重。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param <V>               字段类型
	 * @return 去重后的列值列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<V> listUniqueColumnValue(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		return listColumnValue(queryChainWrapper, column, true, true);
	}

	/**
	 * 获取指定列的值列表，支持去重和null值过滤
	 *
	 * @param column  实体字段
	 * @param unique  是否去重
	 * @param nonNull 是否过滤null值
	 * @param <V>     字段类型
	 * @return 列值列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> List<V> listColumnValue(SFunction<T, V> column, boolean unique, boolean nonNull) {
		return listColumnValue(lambdaQuery(), column, unique, nonNull);
	}

	/**
	 * 获取指定列的值列表，支持去重和null值过滤（使用自定义查询条件）
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param unique            是否去重
	 * @param nonNull           是否过滤null值
	 * @param <V>               字段类型
	 * @return 列值列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<V> listColumnValue(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column,
									   boolean unique, boolean nonNull) {
		Assert.notNull(column, "column 不可为null");
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		var queryWrapper = queryChainWrapper.select(column);
		if (nonNull) {
			queryWrapper = queryWrapper.isNotNull(column);
		}
		var stream = queryWrapper.list()
			.stream()
			.map(column);
		if (unique) {
			stream = stream.distinct();
		}
		return stream.collect(Collectors.toList());
	}

	/**
	 * 根据ID列表查询实体列表
	 * <p>
	 * 使用默认的批处理大小进行分批查询。该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>使用并行流处理批量查询，提高大数据量下的查询性能</li>
	 * </ul>
	 * </p>
	 *
	 * @param ids ID集合
	 * @return 实体列表，如果ID集合为空或全部为null元素，则返回空列表
	 * @see #listByIds(Collection, int, boolean)
	 * @since 1.0.0
	 */
	@Override
	public List<T> listByIds(Collection<? extends Serializable> ids) {
		return listByIds(ids, DEFAULT_LIST_BATCH_SIZE, true);
	}

	/**
	 * 根据ID列表查询实体列表，支持自定义批处理大小
	 * <p>
	 * 当ID数量超过批处理大小时，会分批查询并合并结果。该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>使用并行流处理批量查询，提高大数据量下的查询性能</li>
	 * </ul>
	 * </p>
	 *
	 * @param ids       ID集合
	 * @param batchSize 批处理大小
	 * @return 实体列表，如果ID集合为空或全部为null元素，则返回空列表
	 * @throws IllegalArgumentException 如果batchSize小于等于0
	 * @see #listByIds(Collection, int, boolean)
	 * @since 1.0.0
	 */
	public List<T> listByIds(Collection<? extends Serializable> ids, int batchSize) {
		return listByIds(ids, batchSize, true);
	}

	/**
	 * 根据ID列表查询实体列表，支持自定义批处理大小和并行处理选项
	 * <p>
	 * 当ID数量超过批处理大小时，会分批查询并合并结果。该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当parallel为true时，使用并行流处理批量查询，提高大数据量下的查询性能</li>
	 * </ul>
	 * </p>
	 *
	 * @param ids       ID集合
	 * @param batchSize 批处理大小
	 * @param parallel  是否使用并行流处理，true表示使用并行流，false表示使用普通流
	 * @return 实体列表，如果ID集合为空或全部为null元素，则返回空列表
	 * @throws IllegalArgumentException 如果batchSize小于等于0
	 * @since 1.0.0
	 */
	public List<T> listByIds(Collection<? extends Serializable> ids, int batchSize, boolean parallel) {
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<? extends Serializable> validIdList = CollectionUtils.emptyIfNull(ids)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validIdList.isEmpty()) {
			return Collections.emptyList();
		}

		if (validIdList.size() <= batchSize) {
			return super.listByIds(validIdList);
		}
		if (parallel) {
			return ListUtils.partition(validIdList, batchSize)
				.parallelStream()
				.map(super::listByIds)
				.flatMap(List::stream)
				.toList();
		} else {
			return ListUtils.partition(validIdList, batchSize)
				.stream()
				.map(super::listByIds)
				.flatMap(List::stream)
				.toList();
		}
	}

	/**
	 * 根据列值查询实体列表
	 * <p>
	 * 如果value为null，则查询该列为null的记录。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param value  期望的字段值
	 * @param <V>    字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.list();
		}
		return lambdaQuery()
			.eq(column, value)
			.list();
	}

	/**
	 * 根据列值集合查询实体列表
	 * <p>
	 * 该方法使用默认的批处理大小并默认开启并行处理模式，查询指定列值在给定集合中的实体。
	 * 当值集合数量超过批处理大小时，会分批查询并合并结果。
	 * </p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当值集合数量超过批处理大小时，会分批查询并合并结果</li>
	 * </ul>
	 * <p>
	 * 等同于SQL中的 "WHERE column IN (value1, value2, ...)" 查询。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param values 期望的字段值集合
	 * @param <V>    字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null
	 * @see #listByColumnValues(LambdaQueryChainWrapper, SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return listByColumnValues(lambdaQuery(), column, values, DEFAULT_LIST_BATCH_SIZE, true);
	}

	/**
	 * 根据列值集合查询实体列表，支持自定义批处理大小
	 * <p>
	 * 该方法使用指定的批处理大小并默认开启并行处理模式，查询指定列值在给定集合中的实体。
	 * 当值集合数量超过批处理大小时，会分批查询并合并结果。
	 * </p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当值集合数量超过批处理大小时，会分批查询并合并结果</li>
	 * </ul>
	 * <p>
	 * 等同于SQL中的 "WHERE column IN (value1, value2, ...)" 查询。
	 * </p>
	 *
	 * @param column    实体字段
	 * @param values    期望的字段值集合
	 * @param batchSize 批处理大小
	 * @param <V>       字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null或batchSize小于等于0
	 * @see #listByColumnValues(LambdaQueryChainWrapper, SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		return listByColumnValues(lambdaQuery(), column, values, batchSize, true);
	}

	/**
	 * 根据列值集合查询实体列表，支持自定义批处理大小和并行处理选项
	 * <p>
	 * 该方法使用指定的批处理大小和并行处理选项，查询指定列值在给定集合中的实体。
	 * 当值集合数量超过批处理大小时，会分批查询并合并结果。
	 * </p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当值集合数量超过批处理大小时，会分批查询并合并结果</li>
	 *     <li>当parallel为true时，使用并行流处理批量查询，提高大数据量下的查询性能</li>
	 * </ul>
	 * <p>
	 * 等同于SQL中的 "WHERE column IN (value1, value2, ...)" 查询。
	 * </p>
	 *
	 * @param column    实体字段
	 * @param values    期望的字段值集合
	 * @param batchSize 批处理大小
	 * @param parallel  是否使用并行流处理，true表示使用并行流，false表示使用普通流
	 * @param <V>       字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null或batchSize小于等于0
	 * @see #listByColumnValues(LambdaQueryChainWrapper, SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize, boolean parallel) {
		return listByColumnValues(lambdaQuery(), column, values, batchSize, parallel);
	}

	/**
	 * 根据列值集合查询实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法在自定义查询条件的基础上，使用默认的批处理大小并默认开启并行处理模式，查询指定列值在给定集合中的实体。
	 * </p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当值集合数量超过批处理大小时，会分批查询并合并结果</li>
	 * </ul>
	 * <p>
	 * 等同于SQL中的 "WHERE column IN (value1, value2, ...)" 查询。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param values            期望的字段值集合
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @see #listByColumnValues(LambdaQueryChainWrapper, SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column,
										  Collection<V> values) {
		return listByColumnValues(queryChainWrapper, column, values, DEFAULT_LIST_BATCH_SIZE, true);
	}

	/**
	 * 根据列值集合查询实体列表（使用自定义查询条件和批处理大小）
	 * <p>
	 * 该方法在自定义查询条件的基础上，使用指定的批处理大小并默认开启并行处理模式，查询指定列值在给定集合中的实体。
	 * </p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当值集合数量超过批处理大小时，会分批查询并合并结果</li>
	 * </ul>
	 * <p>
	 * 等同于SQL中的 "WHERE column IN (value1, value2, ...)" 查询。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param values            期望的字段值集合
	 * @param batchSize         批处理大小
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null，或batchSize小于等于0
	 * @see #listByColumnValues(LambdaQueryChainWrapper, SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column,
										  Collection<V> values, int batchSize) {
		return listByColumnValues(queryChainWrapper, column, values, batchSize, true);
	}

	/**
	 * 根据列值集合查询实体列表，支持自定义查询条件、批处理大小和并行处理选项
	 * <p>
	 * 该方法在自定义查询条件的基础上，使用指定的批处理大小和并行处理选项，查询指定列值在给定集合中的实体。
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回空列表</li>
	 *     <li>当值集合数量超过批处理大小时，会分批查询并合并结果</li>
	 *     <li>当parallel为true时，使用并行流处理批量查询，提高大数据量下的查询性能</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column IN (value1, value2, ...)" 查询。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param values            期望的字段值集合
	 * @param batchSize         批处理大小
	 * @param parallel          是否使用并行流处理，true表示使用并行流，false表示使用普通流
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表，如果值集合为空或全部为null元素，则返回空列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null，或batchSize小于等于0
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column,
										  Collection<V> values, int batchSize, boolean parallel) {
		Assert.notNull(column, "column 不可为null");
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<V> validList = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validList.isEmpty()) {
			return Collections.emptyList();
		}

		if (validList.size() <= batchSize) {
			return queryChainWrapper.in(column, validList).list();
		}
		if (parallel) {
			return ListUtils.partition(validList, batchSize)
				.parallelStream()
				.map(part -> queryChainWrapper.in(column, part).list())
				.flatMap(List::stream)
				.toList();
		} else {
			return ListUtils.partition(validList, batchSize)
				.stream()
				.map(part -> queryChainWrapper.in(column, part).list())
				.flatMap(List::stream)
				.toList();
		}
	}

	/**
	 * 查询指定列不为null的实体列表
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值不为null的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column IS NOT NULL" 查询。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param <V>    字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null
	 * @see #listByNotNullColumn(LambdaQueryChainWrapper, SFunction)
	 * @since 1.0.0
	 */
	public <V> List<T> listByNotNullColumn(SFunction<T, V> column) {
		return listByNotNullColumn(lambdaQuery(), column);
	}

	/**
	 * 查询指定列不为null的实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法在自定义查询条件的基础上，查询指定列值不为null的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE ... AND column IS NOT NULL" 查询。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByNotNullColumn(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNotNull(column).list();
	}

	/**
	 * 查询指定列为null的实体列表
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值为null的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column IS NULL" 查询。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param <V>    字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果column为null
	 * @see #listByNullColumn(LambdaQueryChainWrapper, SFunction)
	 * @since 1.0.0
	 */
	public <V> List<T> listByNullColumn(SFunction<T, V> column) {
		return listByNullColumn(lambdaQuery(), column);
	}

	/**
	 * 查询指定列为null的实体列表（使用自定义查询条件）
	 * <p>
	 * 该方法在自定义查询条件的基础上，查询指定列值为null的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE ... AND column IS NULL" 查询。
	 * </p>
	 *
	 * @param queryChainWrapper 查询条件包装器
	 * @param column            实体字段
	 * @param <V>               字段类型
	 * @return 符合条件的实体列表
	 * @throws IllegalArgumentException 如果queryChainWrapper或column为null
	 * @since 1.0.0
	 */
	public <V> List<T> listByNullColumn(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");
		Assert.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNull(column).list();
	}

	/**
	 * 根据列值进行模糊查询
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值包含给定值的实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column LIKE '%value%'" 查询。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  查询值
	 * @return 符合条件的实体列表，如果value为空则返回空列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public List<T> listByLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	/**
	 * 根据列值进行左模糊查询
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值以给定值结尾的实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column LIKE '%value'" 查询。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  查询值
	 * @return 符合条件的实体列表，如果value为空则返回空列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public List<T> listByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeLeft(column, value)
			.list();
	}

	/**
	 * 根据列值进行右模糊查询
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值以给定值开头的实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column LIKE 'value%'" 查询。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  查询值
	 * @return 符合条件的实体列表，如果value为空则返回空列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public List<T> listByLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeRight(column, value)
			.list();
	}

	/**
	 * 根据列值进行模糊排除查询
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值不包含给定值的实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column NOT LIKE '%value%'" 查询。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  查询值
	 * @return 符合条件的实体列表，如果value为空则返回空列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public List<T> listByNotLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	/**
	 * 根据列值进行左模糊排除查询
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值不以给定值结尾的实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column NOT LIKE '%value'" 查询。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  查询值
	 * @return 符合条件的实体列表，如果value为空则返回空列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public List<T> listByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeLeft(column, value)
			.list();
	}

	/**
	 * 根据列值进行右模糊排除查询
	 * <p>
	 * 该方法使用默认的查询条件，查询指定列值不以给定值开头的实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "WHERE column NOT LIKE 'value%'" 查询。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  查询值
	 * @return 符合条件的实体列表，如果value为空则返回空列表
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public List<T> listByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeRight(column, value)
			.list();
	}

	/**
	 * 批量保存实体
	 * <p>
	 * 使用默认的批处理大小进行批量保存操作。
	 * </p>
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param entityList 待保存的实体集合
	 * @return 保存成功返回true，如果实体集合为空或全部为null元素则返回false
	 * @see #saveBatch(Collection, int)
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean saveBatch(Collection<T> entityList) {
		return super.saveBatch(entityList, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 批量保存实体，支持自定义批处理大小
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param entityList 待保存的实体集合
	 * @param batchSize  批处理大小
	 * @return 保存成功返回true，如果实体集合为空或全部为null元素则返回false
	 * @throws IllegalArgumentException 如果batchSize小于等于0
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<T> validEntityList = CollectionUtils.emptyIfNull(entityList)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validEntityList.isEmpty()) {
			return false;
		}
		return super.saveBatch(validEntityList, batchSize);
	}

	/**
	 * 批量更新实体
	 * <p>
	 * 使用默认的批处理大小进行批量更新操作。
	 * </p>
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param entityList 待更新的实体集合
	 * @return 更新成功返回true，如果实体集合为空或全部为null元素则返回false
	 * @see #updateBatchById(Collection, int)
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateBatchById(Collection<T> entityList) {
		return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 批量更新实体，支持自定义批处理大小
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param entityList 待更新的实体集合
	 * @param batchSize  批处理大小
	 * @return 更新成功返回true，如果实体集合为空或全部为null元素则返回false
	 * @throws IllegalArgumentException 如果batchSize小于等于0
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<T> validEntityList = CollectionUtils.emptyIfNull(entityList)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validEntityList.isEmpty()) {
			return false;
		}
		return super.updateBatchById(validEntityList, batchSize);
	}

	/**
	 * 批量保存或更新实体
	 * <p>
	 * 使用默认的批处理大小进行批量保存或更新操作。
	 * 如果实体存在则更新，不存在则保存。
	 * </p>
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param entityList 待保存或更新的实体集合
	 * @return 操作成功返回true，如果实体集合为空或全部为null元素则返回false
	 * @see #saveOrUpdateBatch(Collection, int)
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean saveOrUpdateBatch(Collection<T> entityList) {
		return saveOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 批量保存或更新实体，支持自定义批处理大小
	 * <p>
	 * 如果实体存在则更新，不存在则保存。
	 * </p>
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param entityList 待保存或更新的实体集合
	 * @param batchSize  批处理大小
	 * @return 操作成功返回true，如果实体集合为空或全部为null元素则返回false
	 * @throws IllegalArgumentException 如果batchSize小于等于0
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<T> validEntityList = CollectionUtils.emptyIfNull(entityList)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validEntityList.isEmpty()) {
			return false;
		}
		return super.saveOrUpdateBatch(validEntityList, batchSize);
	}

	/**
	 * 替换指定列的值
	 * <p>
	 * 该方法将指定列中等于oldValue的值替换为newValue。<br/>
	 * 如果oldValue为null，则将该列中为null的值替换为newValue。
	 * </p>
	 * <p>
	 * 等同于SQL中的：
	 * <ul>
	 *     <li>当oldValue为null时：UPDATE table SET column = newValue WHERE column IS NULL</li>
	 *     <li>当oldValue不为null时：UPDATE table SET column = newValue WHERE column = oldValue</li>
	 * </ul>
	 * </p>
	 *
	 * @param column   实体字段
	 * @param newValue 新值
	 * @param oldValue 旧值
	 * @param <V>      字段类型
	 * @return 更新成功返回true，否则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> boolean replaceColumnValue(SFunction<T, V> column, V newValue, V oldValue) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(oldValue)) {
			return lambdaUpdate()
				.set(column, newValue)
				.isNull(column)
				.update();
		} else {
			return lambdaUpdate()
				.set(column, newValue)
				.eq(column, oldValue)
				.update();
		}
	}

	/**
	 * 根据ID集合删除实体
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param list ID集合
	 * @return 删除成功返回true，如果ID集合为空或全部为null元素则返回false
	 * @since 1.0.0
	 */
	@Override
	public boolean removeByIds(Collection<?> list) {
		List<?> validList = CollectionUtils.emptyIfNull(list)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validList.isEmpty()) {
			return false;
		}
		return super.removeByIds(validList);
	}

	/**
	 * 根据ID集合删除实体，支持字段填充功能
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 * </ul>
	 * </p>
	 *
	 * @param list    ID集合
	 * @param useFill 是否启用字段填充
	 * @return 删除成功返回true，如果ID集合为空或全部为null元素则返回false
	 * @since 1.0.0
	 */
	@Override
	public boolean removeByIds(Collection<?> list, boolean useFill) {
		List<?> validList = CollectionUtils.emptyIfNull(list)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validList.isEmpty()) {
			return false;
		}
		return super.removeByIds(validList, useFill);
	}

	/**
	 * 根据列值删除实体
	 * <p>
	 * 该方法删除指定列值等于给定值的所有实体。<br/>
	 * 如果value为null，则删除该列值为null的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的：
	 * <ul>
	 *     <li>当value为null时：DELETE FROM table WHERE column IS NULL</li>
	 *     <li>当value不为null时：DELETE FROM table WHERE column = value</li>
	 * </ul>
	 * </p>
	 *
	 * @param column 实体字段
	 * @param value  期望的字段值
	 * @param <V>    字段类型
	 * @return 删除成功返回true，否则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public <V> boolean removeByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaUpdate()
				.isNull(column)
				.remove();
		}
		return lambdaUpdate()
			.eq(column, value)
			.remove();
	}

	/**
	 * 根据列值集合删除实体
	 * <p>
	 * 使用默认的批处理大小并默认开启并行处理模式进行批量删除操作。
	 * </p>
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 *     <li>当值集合数量超过批处理大小时，会分批删除</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column IN (value1, value2, ...)" 语句。
	 * </p>
	 *
	 * @param column 实体字段
	 * @param values 期望的字段值集合
	 * @param <V>    字段类型
	 * @return 删除成功返回true，如果值集合为空或全部为null元素则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @see #removeByColumnValues(SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return removeByColumnValues(column, values, DEFAULT_BATCH_SIZE, true);
	}

	/**
	 * 根据列值集合删除实体，支持自定义批处理大小
	 * <p>
	 * 使用指定的批处理大小并默认开启并行处理模式进行批量删除操作。
	 * </p>
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 *     <li>当值集合数量超过批处理大小时，会分批删除</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column IN (value1, value2, ...)" 语句。
	 * </p>
	 *
	 * @param column    实体字段
	 * @param values    期望的字段值集合
	 * @param batchSize 批处理大小
	 * @param <V>       字段类型
	 * @return 删除成功返回true，如果值集合为空或全部为null元素则返回false
	 * @throws IllegalArgumentException 如果column为null或batchSize小于等于0
	 * @see #removeByColumnValues(SFunction, Collection, int, boolean)
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		return removeByColumnValues(column, values, batchSize, true);
	}

	/**
	 * 根据列值集合删除实体，支持自定义批处理大小和并行处理选项
	 * <p>
	 * 该方法会自动处理以下情况：
	 * <ul>
	 *     <li>如果传入的集合为null，将被视为空集合处理</li>
	 *     <li>集合中的null元素会被自动过滤掉</li>
	 *     <li>如果过滤后的集合为空，则返回false</li>
	 *     <li>当值集合数量超过批处理大小时，会分批删除</li>
	 *     <li>当parallel为true时，使用并行流处理批量删除，提高大数据量下的删除性能</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column IN (value1, value2, ...)" 语句。
	 * </p>
	 *
	 * @param column    实体字段
	 * @param values    期望的字段值集合
	 * @param batchSize 批处理大小
	 * @param parallel  是否使用并行流处理，true表示使用并行流，false表示使用普通流
	 * @param <V>       字段类型
	 * @return 删除成功返回true，如果值集合为空或全部为null元素则返回false
	 * @throws IllegalArgumentException 如果column为null或batchSize小于等于0
	 * @since 1.0.0
	 */
	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize, boolean parallel) {
		Assert.notNull(column, "column 不可为null");
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<V> validList = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validList.isEmpty()) {
			return false;
		}

		if (validList.size() <= batchSize) {
			return lambdaUpdate()
				.in(column, validList)
				.remove();
		}
		if (parallel) {
			return ListUtils.partition(validList, batchSize)
				.parallelStream()
				.allMatch(part -> lambdaUpdate()
					.in(column, part)
					.remove());
		} else {
			return ListUtils.partition(validList, batchSize)
				.stream()
				.allMatch(part -> lambdaUpdate()
					.in(column, part)
					.remove());
		}
	}

	/**
	 * 根据列值进行模糊删除
	 * <p>
	 * 该方法删除指定列值包含给定值的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column LIKE '%value%'" 语句。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  删除条件值
	 * @return 删除成功返回true，如果value为空则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public boolean removeByLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.like(column, value)
			.remove();
	}

	/**
	 * 根据列值进行模糊排除删除
	 * <p>
	 * 该方法删除指定列值不包含给定值的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column NOT LIKE '%value%'" 语句。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  删除条件值
	 * @return 删除成功返回true，如果value为空则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public boolean removeByNotLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLike(column, value)
			.remove();
	}

	/**
	 * 根据列值进行左模糊删除
	 * <p>
	 * 该方法删除指定列值以给定值结尾的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column LIKE '%value'" 语句。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  删除条件值
	 * @return 删除成功返回true，如果value为空则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public boolean removeByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeLeft(column, value)
			.remove();
	}

	/**
	 * 根据列值进行左模糊排除删除
	 * <p>
	 * 该方法删除指定列值不以给定值结尾的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column NOT LIKE '%value'" 语句。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  删除条件值
	 * @return 删除成功返回true，如果value为空则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public boolean removeByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeLeft(column, value)
			.remove();
	}

	/**
	 * 根据列值进行右模糊删除
	 * <p>
	 * 该方法删除指定列值以给定值开头的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column LIKE 'value%'" 语句。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  删除条件值
	 * @return 删除成功返回true，如果value为空则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public boolean removeByLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeRight(column, value)
			.remove();
	}

	/**
	 * 根据列值进行右模糊排除删除
	 * <p>
	 * 该方法删除指定列值不以给定值开头的所有实体。
	 * </p>
	 * <p>
	 * 等同于SQL中的 "DELETE FROM table WHERE column NOT LIKE 'value%'" 语句。
	 * </p>
	 *
	 * @param column 实体字段（字符串类型）
	 * @param value  删除条件值
	 * @return 删除成功返回true，如果value为空则返回false
	 * @throws IllegalArgumentException 如果column为null
	 * @since 1.0.0
	 */
	public boolean removeByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeRight(column, value)
			.remove();
	}

	/**
	 * 将对象转换为JSON字符串
	 * <p>
	 * 该方法用于处理JSON相关查询时的值转换。
	 * 如果传入的值为null，则返回字符串"null"。
	 * 否则，将对象转换为JSON字符串格式。
	 * </p>
	 *
	 * @param value 需要转换的对象
	 * @return 转换后的JSON字符串，如果value为null则返回"null"
	 * @apiNote 此方法仅可在<strong>MySQL</strong>数据库环境下使用
	 * @since 1.0.0
	 */
	protected String getJsonValue(Object value) {
		if (Objects.isNull(value)) {
			return "null";
		}
		return JsonUtils.toString(value);
	}

	/**
	 * 将字符串集合转换为JSON数组字符串
	 * <p>
	 * 该方法使用默认的空白字符串过滤条件（使用{@link StringUtils#isBlank}判断），将符合条件的字符串元素转换为标准JSON数组格式。
	 * </p>
	 *
	 * @param values 需要转换的字符串集合
	 * @return 转换后的JSON数组字符串
	 * @apiNote 此方法仅可在<strong>MySQL</strong>数据库环境下使用
	 * @see #getJsonArrayString(Collection, Predicate)
	 * @since 1.0.0
	 */
	protected String getJsonArrayString(Collection<String> values) {
		return getJsonArrayString(values, StringUtils::isBlank);
	}

	/**
	 * 将字符串集合转换为JSON数组字符串，支持自定义过滤条件
	 * <p>
	 * 该方法根据指定的断言条件过滤集合中的元素，将符合条件的元素转换为标准JSON数组格式。
	 * 生成的JSON字符串格式为：["值1","值2","值3"]。
	 * </p>
	 * <p>
	 * 例如，使用以下代码可以过滤掉空白字符串：
	 * <pre>
	 * List&lt;String&gt; values = Arrays.asList("项目1", "", "项目2", "  ", "项目3");
	 * String json = getJsonArrayString(values, StringUtils::isBlank);
	 * // 结果: ["项目1","项目2","项目3"]
	 * </pre>
	 * </p>
	 *
	 * @param values    需要转换的字符串集合
	 * @param predicate 用于过滤元素的断言，返回true的元素将被包含在结果中
	 * @return 转换后的JSON数组字符串
	 * @throws IllegalArgumentException 如果values为空集合
	 * @throws NullPointerException     如果predicate为null
	 * @apiNote 此方法仅可在<strong>MySQL</strong>数据库环境下使用
	 * @since 1.0.0
	 */
	protected String getJsonArrayString(Collection<String> values, Predicate<String> predicate) {
		Validate.notEmpty(values);
		Objects.requireNonNull(predicate);

		StringBuilder jsonArrayStringBuilder = new StringBuilder("[");
		for (String value : values) {
			if (predicate.test(value)) {
				jsonArrayStringBuilder.append("\"").append(value).append("\",");
			}
		}
		jsonArrayStringBuilder.deleteCharAt(jsonArrayStringBuilder.length() - 1);
		jsonArrayStringBuilder.append("]");
		return jsonArrayStringBuilder.toString();
	}
}
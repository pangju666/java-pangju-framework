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
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.data.mybatisplus.uitls.EntityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * MyBatis-Plus 通用仓储基类。
 *
 * <p><b>目标与范围：</b>面向实体 {@code T}，在 {@link CrudRepository} 与 {@link IService} 基础上扩展通用查询与更新能力。</p>
 *
 * <p><b>提供能力：</b></p>
 * <ul>
 *   <li>JSON 列查询：键存在、键值匹配、数组包含、数组交集、空对象/空数组检测。</li>
 *   <li>列值操作：存在性判断、按列取单个/多个、列值去重、IS NULL/IS NOT NULL。</li>
 *   <li>模糊匹配：LIKE/NOT LIKE 及 LEFT/RIGHT 变体，对空串进行安全处理。</li>
 *   <li>批量查询：in 查询支持分批，避免单条 SQL 过长与参数超限。</li>
 *   <li>维护操作：列值替换与删除（对 {@code null} 具有特殊语义）。</li>
 *   <li>辅助：{@code SFunction} → 列名解析、值 → JSON/SQL 字面量转换。</li>
 * </ul>
 *
 * <p><b>版本要求（仅 JSON 相关查询函数）：</b></p>
 * <ul>
 *   <li>MySQL 5.7.8+：基础 JSON 能力（{@code JSON_CONTAINS_PATH}、{@code JSON_CONTAINS}），以及空对象/空数组检测。
 *       关联方法：{@link #listByColumnJsonKey(SFunction, String)}、{@link #listByColumnJsonArrayValue(SFunction, Object)}、
 *       {@link #listByColumnEmptyJsonObject(SFunction)}、{@link #listByColumnEmptyJsonArray(SFunction)}；及其 {@code String column} 重载。</li>
 *   <li>MySQL 5.7.13+：{@code column->>'$.key'} 文本比较。
 *       关联方法：{@link #listByColumnJsonKeyValue(SFunction, String, Object)}；及其 {@code String column} 重载。</li>
 *   <li>MySQL 8.0.17+：{@code JSON_OVERLAPS}（数组交集）。
 *       关联方法：{@link #listByColumnJsonArrayValues(SFunction, Collection)}；及其 {@code String column} 重载。</li>
 * </ul>
 *
 * <p><b>使用约定与行为：</b></p>
 * <ul>
 *   <li>入参统一通过 {@link Assert} 校验，不满足时抛出 {@link IllegalArgumentException}。</li>
 *   <li>空集合通常返回空结果；LIKE 系列对空字符串返回空列表/false（避免全表扫描）。</li>
 *   <li>{@code SFunction} 解析依赖 MyBatis-Plus 的 Lambda 缓存与实体元数据，映射缺失将触发内部断言异常。</li>
 *   <li>大批量 in 查询建议合理设置 {@code batchSize}，以适配数据库配置与 SQL 长度限制。</li>
 * </ul>
 *
 * <p><b>性能与注意事项：</b></p>
 * <ul>
 *   <li>JSON 函数通常难以命中常规索引，建议结合虚拟列/函数索引或业务分段方案。</li>
 *   <li>LIKE 前缀模糊（likeLeft）相对更易利用索引，全模糊与后缀模糊开销较大。</li>
 * </ul>
 *
 * <p><b>线程安全：</b>仓储本身不持有共享可变业务状态；列缓存按实体类懒加载一次，常规 Spring 单例场景下可安全复用。</p>
 *
 * @author pangju666
 * @see CrudRepository
 * @see IService
 * @since 1.0.0
 */
public abstract class BaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> implements IService<T> {
	/**
	 * 生成 JSON 键值匹配 SQL 片段的格式串。
	 * <p>示例：column -&gt;&gt; '$.key' = 'value'（文本比较）。</p>
	 *
	 * @since 1.0.0
	 */
	protected static final String JSON_KEY_VALUE_SQL_FORMAT = "%s->>'$.%s' = '%s'";
	/**
	 * 生成 JSON 键存在检查 SQL 片段的格式串，使用 {@code JSON_CONTAINS_PATH(..., 'one', '$.key')}。
	 *
	 * @since 1.0.0
	 */
	protected static final String JSON_KEY_EXIST_SQL_FORMAT = "JSON_CONTAINS_PATH(%s, 'one', '$.%s')";
	/**
	 * 生成 JSON 数组包含值检查 SQL 片段的格式串，使用 {@code JSON_CONTAINS(column, 'jsonValue')}。
	 *
	 * @since 1.0.0
	 */
	protected static final String JSON_ARRAY_VALUE_SQL_FORMAT = "JSON_CONTAINS(%s, '%s')";
	/**
	 * 生成 JSON 数组交集检查 SQL 片段的格式串，使用 {@code JSON_OVERLAPS(column, 'jsonArray')}（MySQL 8.0.17+）。
	 *
	 * @since 1.0.0
	 */
	protected static final String JSON_ARRAY_VALUES_SQL_FORMAT = "JSON_OVERLAPS(%s, '%s')";

	private Map<String, ColumnCache> columnMap = null;
	private boolean initColumnMap = false;

	/**
	 * 查询指定 JSON 列为“空对象 {}”或列值为 {@code null} 的记录。
	 *
	 * <p>依赖 MySQL 5.7.8+ 对 JSON 的支持，使用文本匹配 {@code {""}}（常量 {@code Constants.EMPTY_JSON_OBJECT_STR}）。</p>
	 *
	 * @param column JSON 列的 Lambda 引用
	 * @param <V>    列值类型（实体字段类型）
	 * @return 列为 null 或空对象的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnEmptyJsonObject(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");

		return lambdaQuery()
			.isNull(column)
			.or()
			.like(column, Constants.EMPTY_JSON_OBJECT_STR)
			.list();
	}

	/**
	 * 查询指定 JSON 列为“空数组 []”或列值为 {@code null} 的记录。
	 *
	 * <p>依赖 MySQL 5.7.8+ 对 JSON 的支持，使用文本匹配 {@code []}（常量 {@code Constants.EMPTY_JSON_ARRAY_STR}）。</p>
	 *
	 * @param column JSON 列的 Lambda 引用
	 * @param <V>    列值类型（实体字段类型）
	 * @return 列为 null 或空数组的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnEmptyJsonArray(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");

		return lambdaQuery()
			.isNull(column)
			.or()
			.like(column, Constants.EMPTY_JSON_ARRAY_STR)
			.list();
	}

	/**
	 * 查询指定 JSON 列中存在给定键（路径）的记录。
	 *
	 * <p>依赖 MySQL 5.7.8+ 的 {@code JSON_CONTAINS_PATH(column, 'one', '$.key')}。</p>
	 *
	 * @param column JSON 列的 Lambda 引用
	 * @param key    要检查的键（JSONPath 中的直接键名）
	 * @return 包含该键的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null 或 {@code key} 为空白
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonKey(SFunction<T, ?> column, String key) {
		Assert.notNull(column, "column 不可为null");
		Assert.hasText(key, "key 不可为空");
		return listByColumnJsonKey(columnToString(column), key);
	}

	/**
	 * 查询指定 JSON 列中某键的值与给定值相等的记录。
	 *
	 * <p>依赖 MySQL 5.7.13+，采用 {@code column->>'$.key' = 'value'} 的文本比较形式。</p>
	 *
	 * @param column JSON 列的 Lambda 引用
	 * @param key    目标键
	 * @param value  目标值
	 * @return 键值匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null 或 {@code key} 为空白
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonKeyValue(SFunction<T, ?> column, String key, Object value) {
		Assert.notNull(column, "column 不可为null");
		Assert.hasText(key, "key 不可为空");
		return listByColumnJsonKeyValue(columnToString(column), key, value);
	}

	/**
	 * 查询指定 JSON 数组列包含某个值的记录。
	 *
	 * <p>依赖 MySQL 5.7.8+ 的 {@code JSON_CONTAINS(column, 'jsonValue')}。</p>
	 *
	 * @param column JSON 数组列的 Lambda 引用
	 * @param value  目标值
	 * @return 包含该值的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonArrayValue(SFunction<T, ?> column, Object value) {
		Assert.notNull(column, "column 不可为null");
		return listByColumnJsonArrayValue(columnToString(column), value);
	}

	/**
	 * 查询指定 JSON 数组列存在给定值集合中任意值的记录。
	 *
	 * <p>依赖 MySQL 8.0.17+ 的 {@code JSON_OVERLAPS(column, 'jsonArray')}。</p>
	 *
	 * @param column JSON 数组列的 Lambda 引用
	 * @param values 值集合（为空时返回空列表）
	 * @return 与集合存在交集的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonArrayValues(SFunction<T, ?> column, Collection<?> values) {
		Assert.notNull(column, "column 不可为null");
		return listByColumnJsonArrayValues(columnToString(column), values);
	}

	/**
	 * 检查指定列的 JSON 路径（键）是否存在。
	 *
	 * @param column 列名（物理列名）
	 * @param key    目标键
	 * @return 包含该键的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 或 {@code key} 为空白
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonKey(String column, String key) {
		Assert.hasText(column, "column 不可为空");
		Assert.hasText(key, "key 不可为空");

		return lambdaQuery()
			.apply(JSON_KEY_EXIST_SQL_FORMAT.formatted(column, key))
			.list();
	}

	/**
	 * 检查指定列的某个 JSON 键值与给定值相等。
	 *
	 * @param column 列名（物理列名）
	 * @param key    目标键
	 * @param value  目标值
	 * @return 键值匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 或 {@code key} 为空白
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonKeyValue(String column, String key, Object value) {
		Assert.hasText(column, "column 不可为空");
		Assert.hasText(key, "key 不可为空");

		String sqlValue;
		if (Objects.isNull(value)) {
			sqlValue = "null";
		} else if (value instanceof Number number) {
			sqlValue = number.toString();
		} else if (value instanceof String string) {
			sqlValue = string;
		} else if (value instanceof Boolean bool) {
			sqlValue = bool.toString();
		} else {
			sqlValue = JsonUtils.toString(value);
		}
		return lambdaQuery()
			.apply(JSON_KEY_VALUE_SQL_FORMAT.formatted(column, key, sqlValue))
			.list();
	}

	/**
	 * 检查指定列（JSON 数组）是否包含某个值。
	 *
	 * @param column 列名（物理列名）
	 * @param value  目标值
	 * @return 包含该值的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为空白
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonArrayValue(String column, Object value) {
		Assert.hasText(column, "column 不可为空");

		String sqlValue;
		if (Objects.isNull(value)) {
			sqlValue = "null";
		} else if (value instanceof Number number) {
			sqlValue = number.toString();
		} else if (value instanceof String string) {
			sqlValue = "\"" + string + "\"";
		} else if (value instanceof Boolean bool) {
			sqlValue = bool.toString();
		} else {
			sqlValue = JsonUtils.toString(value);
		}
		return lambdaQuery()
			.apply(JSON_ARRAY_VALUE_SQL_FORMAT.formatted(column, sqlValue))
			.list();
	}

	/**
	 * 检查指定列（JSON 数组）与给定值集合是否存在交集。
	 *
	 * @param column 列名（物理列名）
	 * @param values 值集合（为空时返回空列表）
	 * @return 与集合存在交集的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为空白
	 * @since 1.0.0
	 */
	public List<T> listByColumnJsonArrayValues(String column, Collection<?> values) {
		Assert.hasText(column, "column 不可为空");

		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}

		return lambdaQuery()
			.apply(JSON_ARRAY_VALUES_SQL_FORMAT.formatted(column, JsonUtils.toString(values)))
			.list();
	}

	/**
	 * 根据主键判断记录是否存在。
	 *
	 * @param id 主键值
	 * @return 存在返回 {@code true}，否则 {@code false}
	 * @since 1.0.0
	 */
	public boolean existsById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	/**
	 * 根据主键判断记录是否不存在。
	 *
	 * @param id 主键值
	 * @return 不存在返回 {@code true}，否则 {@code false}
	 * @since 1.0.0
	 */
	public boolean notExistsById(Serializable id) {
		return Objects.isNull(getById(id));
	}

	/**
	 * 判断某列的值是否存在。
	 *
	 * <p>当 {@code value} 为 {@code null} 时，判断该列为 {@code IS NULL} 的记录是否存在。</p>
	 *
	 * @param column 列的 Lambda 引用
	 * @param value  目标值，可为 {@code null}
	 * @param <V>    列值类型
	 * @return 存在返回 {@code true}
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> boolean existsByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.exists();
		}
		return lambdaQuery()
			.eq(column, value)
			.exists();
	}

	/**
	 * 判断某列的值是否不存在。
	 *
	 * <p>当 {@code value} 为 {@code null} 时，判断该列为 {@code IS NOT NULL} 的记录是否存在。</p>
	 *
	 * @param column 列的 Lambda 引用
	 * @param value  目标值，可为 {@code null}
	 * @param <V>    列值类型
	 * @return 不存在返回 {@code true}
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> boolean notExistsByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNotNull(column)
				.exists();
		}
		return !lambdaQuery()
			.eq(column, value)
			.exists();
	}

	/**
	 * 列选择查询并返回该列的值列表（允许重复）。
	 *
	 * @param column 列的 Lambda 引用
	 * @param <V>    列值类型
	 * @return 列值列表，若无匹配返回空列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> List<V> listColumnValue(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");
		return EntityUtils.getFieldValueList(lambdaQuery().select(column).list(), column);
	}

	/**
	 * 列选择查询并返回该列的去重值列表。
	 *
	 * @param column 列的 Lambda 引用
	 * @param <V>    列值类型
	 * @return 去重后的列值列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> List<V> listUniqueColumnValue(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");
		return EntityUtils.getUniqueFieldValueList(lambdaQuery().select(column).list(), column);
	}

	/**
	 * 根据列值获取单个实体。
	 *
	 * <p>当 {@code value} 为 {@code null} 时按 {@code IS NULL} 查询。</p>
	 *
	 * @param column 列的 Lambda 引用
	 * @param value  目标值，可为 {@code null}
	 * @param <V>    列值类型
	 * @return 匹配的实体，可能为 {@code null}
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> T getByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery().isNull(column).one();
		}
		return lambdaQuery().eq(column, value).one();
	}

	/**
	 * 根据列值获取单个实体的 {@link Optional} 包装。
	 *
	 * <p>当 {@code value} 为 {@code null} 时按 {@code IS NULL} 查询。</p>
	 *
	 * @param column 列的 Lambda 引用
	 * @param value  目标值，可为 {@code null}
	 * @param <V>    列值类型
	 * @return 匹配的实体的 Optional 包装，未匹配返回 {@code Optional.empty()}
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> Optional<T> getOptByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return Optional.ofNullable(lambdaQuery().isNull(column).one());
		}
		return Optional.ofNullable(lambdaQuery().eq(column, value).one());
	}

	/**
	 * 根据列值获取多个实体。
	 *
	 * <p>当 {@code value} 为 {@code null} 时按 {@code IS NULL} 查询。</p>
	 *
	 * @param column 列的 Lambda 引用
	 * @param value  目标值，可为 {@code null}
	 * @param <V>    列值类型
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
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
	 * 根据主键集合查询，使用默认批大小（{@code DEFAULT_BATCH_SIZE}）。
	 *
	 * @param ids 主键集合
	 * @return 匹配的实体列表
	 * @see #listByIds(Collection, int)
	 * @since 1.0.0
	 */
	@Override
	public List<T> listByIds(Collection<? extends Serializable> ids) {
		return listByIds(ids, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 根据主键集合查询，支持分批执行以降低单条 SQL 的 in 列表长度。
	 *
	 * @param ids       主键集合
	 * @param batchSize 批大小，必须大于 0；当集合大小不超过批大小时直接使用 MyBatis-Plus 的批量查询
	 * @param <V>       主键类型（需为 {@link Serializable}）
	 * @return 匹配的实体列表；当入参集合为空时返回空列表
	 * @throws IllegalArgumentException 当 {@code batchSize} 小于等于 0
	 * @since 1.0.0
	 */
	public <V extends Serializable> List<T> listByIds(Collection<V> ids, int batchSize) {
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}

		if (ids.size() <= batchSize) {
			return super.listByIds(ids);
		}
		return ListUtils.partition(ids instanceof List<V> list ? list : List.copyOf(ids), batchSize)
			.stream()
			.map(super::listByIds)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	/**
	 * 根据列值集合查询，使用默认批大小（{@code DEFAULT_BATCH_SIZE}）。
	 *
	 * @param column 列的 Lambda 引用
	 * @param values 列值集合
	 * @param <V>    列值类型
	 * @return 匹配的实体列表；当集合为空时返回空列表
	 * @see #listByColumnValues(SFunction, Collection, int)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return listByColumnValues(column, values, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 根据列值集合查询，支持分批执行以降低单条 SQL 的 in 列表长度。
	 *
	 * @param column    列的 Lambda 引用
	 * @param values    列值集合
	 * @param batchSize 批大小，必须大于 0
	 * @param <V>       列值类型
	 * @return 匹配的实体列表；当集合为空时返回空列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null 或 {@code batchSize} 小于等于 0
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		Assert.notNull(column, "column 不可为null");
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}

		if (values.size() <= batchSize) {
			return lambdaQuery().in(column, values).list();
		}
		return ListUtils.partition(values instanceof List<V> list ? list : List.copyOf(values), batchSize)
			.stream()
			.map(part -> lambdaQuery().in(column, part).list())
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	/**
	 * 根据列值集合查询（自定义查询来源），使用默认批大小（{@code DEFAULT_BATCH_SIZE}）。
	 *
	 * @param column   列的 Lambda 引用
	 * @param values   列值集合
	 * @param supplier 查询包装提供者（用于附加查询条件或自定义数据源）
	 * @param <V>      列值类型
	 * @return 匹配的实体列表；当集合为空时返回空列表
	 * @see #listByColumnValues(SFunction, Collection, int, Supplier)
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values,
										  Supplier<LambdaQueryChainWrapper<T>> supplier) {
		return listByColumnValues(column, values, DEFAULT_BATCH_SIZE, supplier);
	}

	/**
	 * 根据列值集合查询（自定义查询来源），支持分批执行以降低单条 SQL 的 in 列表长度。
	 *
	 * @param column    列的 Lambda 引用
	 * @param values    列值集合
	 * @param batchSize 批大小，必须大于 0
	 * @param supplier  查询包装提供者（每批次调用一次，用于构造查询链）
	 * @param <V>       列值类型
	 * @return 匹配的实体列表；当集合为空时返回空列表
	 * @throws IllegalArgumentException 当 {@code column} 或 {@code supplier} 为 null，或 {@code batchSize} 小于等于 0
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize,
										  Supplier<LambdaQueryChainWrapper<T>> supplier) {
		Assert.notNull(column, "column 不可为null");
		Assert.notNull(supplier, "supplier 不可为null");
		Assert.isTrue(batchSize > 0, "batchSize 必须大于0");

		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}

		if (values.size() <= batchSize) {
			return supplier.get().in(column, values).list();
		}
		return ListUtils.partition(values instanceof List<V> list ? list : List.copyOf(values), batchSize)
			.stream()
			.map(part -> supplier.get().in(column, part).list())
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	/**
	 * 查询指定列不为 {@code null} 的记录。
	 *
	 * @param column 列的 Lambda 引用
	 * @param <V>    列值类型
	 * @return 列不为 null 的实体列表
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnNotNull(SFunction<T, V> column) {
		return lambdaQuery().isNotNull(column).list();
	}

	/**
	 * 查询指定列为 {@code null} 的记录。
	 *
	 * @param column 列的 Lambda 引用
	 * @param <V>    列值类型
	 * @return 列为 null 的实体列表
	 * @since 1.0.0
	 */
	public <V> List<T> listByColumnNull(SFunction<T, V> column) {
		return lambdaQuery().isNull(column).list();
	}

	/**
	 * 使用 LIKE 模式匹配列值。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时返回空列表
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnLikeColumn(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	/**
	 * 使用 LIKE LEFT 模式匹配（右侧模糊）。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时返回空列表
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnLikeLeft(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeLeft(column, value)
			.list();
	}

	/**
	 * 使用 LIKE RIGHT 模式匹配（左侧模糊）。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时返回空列表
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnLikeRight(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeRight(column, value)
			.list();
	}

	/**
	 * 使用 NOT LIKE 模式匹配列值。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时返回空列表
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnNotLike(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	/**
	 * 使用 NOT LIKE LEFT 模式匹配（左侧模糊）。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时返回空列表
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnNotLikeLeft(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeLeft(column, value)
			.list();
	}

	/**
	 * 使用 NOT LIKE RIGHT 模式匹配（右侧模糊）。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时返回空列表
	 * @return 匹配的实体列表
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public List<T> listByColumnNotLikeRight(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeRight(column, value)
			.list();
	}

	/**
	 * 替换指定列的值。
	 *
	 * <p>当 {@code oldValue} 为 {@code null} 时，更新列为 {@code IS NULL} 的记录；否则更新列值等于 {@code oldValue} 的记录。</p>
	 *
	 * @param column   列的 Lambda 引用
	 * @param newValue 新值
	 * @param oldValue 旧值，可为 {@code null}
	 * @param <V>      列值类型
	 * @return 是否更新成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
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
	 * 根据列值删除记录。
	 *
	 * <p>当 {@code value} 为 {@code null} 时删除列为 {@code IS NULL} 的记录；否则删除列值等于 {@code value} 的记录。</p>
	 *
	 * @param column 列的 Lambda 引用
	 * @param value  目标值，可为 {@code null}
	 * @param <V>    列值类型
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
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
	 * 根据列值集合删除记录。
	 *
	 * @param column 列的 Lambda 引用
	 * @param values 列值集合；为空集合时直接返回 {@code false}
	 * @param <V>    列值类型
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values) {
		Assert.notNull(column, "column 不可为null");

		if (CollectionUtils.isEmpty(values)) {
			return false;
		}

		return lambdaUpdate()
			.in(column, values)
			.remove();
	}

	/**
	 * 使用 LIKE 模式根据列值删除记录。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时直接返回 {@code false}
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public boolean removeByColumnLike(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.like(column, value)
			.remove();
	}

	/**
	 * 使用 NOT LIKE 模式根据列值删除记录。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时直接返回 {@code false}
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public boolean removeByColumnNotLike(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLike(column, value)
			.remove();
	}

	/**
	 * 使用 LIKE LEFT 模式（左侧模糊）根据列值删除记录。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时直接返回 {@code false}
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public boolean removeByColumnLikeLeft(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeLeft(column, value)
			.remove();
	}

	/**
	 * 使用 NOT LIKE LEFT 模式（左侧模糊）根据列值删除记录。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时直接返回 {@code false}
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public boolean removeByColumnNotLikeLeft(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeLeft(column, value)
			.remove();
	}

	/**
	 * 使用 LIKE RIGHT 模式（右侧模糊）根据列值删除记录。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时直接返回 {@code false}
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public boolean removeByColumnLikeRight(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeRight(column, value)
			.remove();
	}

	/**
	 * 使用 NOT LIKE RIGHT 模式（右侧模糊）根据列值删除记录。
	 *
	 * @param column 字符串列的 Lambda 引用
	 * @param value  匹配值；为空字符串或 {@code null} 时直接返回 {@code false}
	 * @return 是否删除成功
	 * @throws IllegalArgumentException 当 {@code column} 为 null
	 * @since 1.0.0
	 */
	public boolean removeByColumnNotLikeRight(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeRight(column, value)
			.remove();
	}

	/**
	 * 将列的 Lambda 引用解析为数据库物理列名。
	 *
	 * <p>解析流程：</p>
	 * - 使用 MyBatis-Plus 的 {@link LambdaUtils#extract(SFunction)} 提取方法引用元信息。
	 * - 将方法名解析为属性名，并从实体类的列缓存中获取 {@link ColumnCache}。
	 * - 首次调用时会初始化并缓存当前实体类的列映射。
	 *
	 * <p>从Mybatis Plus 源码拷贝过来的。</p>
	 *
	 * @param column 列的 Lambda 引用（如 {@code Entity::getField}）
	 * @return 对应的数据库物理列名（如 {@code field_name}）
	 * @throws IllegalArgumentException 当无法解析列缓存或属性映射不存在时（内部断言抛出）
	 * @see com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper
	 * @since 1.0.0
	 */
	protected String columnToString(SFunction<T, ?> column) {
		LambdaMeta meta = LambdaUtils.extract(column);
		String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
		Class<?> instantiatedClass = meta.getInstantiatedClass();

		if (!initColumnMap) {
			Class<T> entityClass = getEntityClass();
			if (entityClass != null) {
				instantiatedClass = entityClass;
			}
			columnMap = LambdaUtils.getColumnMap(instantiatedClass);
			com.baomidou.mybatisplus.core.toolkit.Assert.notNull(columnMap,
				"can not find lambda cache for this entity [%s]", entityClass.getName());
			initColumnMap = true;
		}

		ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
		com.baomidou.mybatisplus.core.toolkit.Assert.notNull(columnCache,
			"can not find lambda cache for this property [%s] of entity [%s]", fieldName, instantiatedClass.getName());
		return columnCache.getColumn();
	}
}
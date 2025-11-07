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

public abstract class BaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> implements IService<T> {
	protected static final String JSON_KEY_VALUE_SQL_FORMAT = "%s->>'$.%s' = '%s'";
	protected static final String JSON_KEY_EXIST_SQL_FORMAT = "JSON_CONTAINS_PATH(%s, 'one', '$.%s')";
	protected static final String JSON_ARRAY_VALUE_SQL_FORMAT = "JSON_CONTAINS(%s, '%s')";
	protected static final String JSON_ARRAY_VALUES_SQL_FORMAT = "JSON_OVERLAPS(%s, '%s')";

	private Map<String, ColumnCache> columnMap = null;
	private boolean initColumnMap = false;

	// 支持 MySQL 5.7.8+
	public <V> List<T> listByEmptyJsonObject(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");

		return lambdaQuery()
			.isNull(column)
			.or()
			.like(column, Constants.EMPTY_JSON_OBJECT_STR)
			.list();
	}

	// 支持 MySQL 5.7.8+
	public <V> List<T> listByEmptyJsonArray(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");

		return lambdaQuery()
			.isNull(column)
			.or()
			.like(column, Constants.EMPTY_JSON_ARRAY_STR)
			.list();
	}

	// 支持 MySQL 5.7.8+
	public List<T> listByJsonColumnKey(SFunction<T, ?> column, String key) {
		Assert.notNull(column, "column 不可为null");
		Assert.hasText(key, "key 不可为空");
		return listByJsonColumnKey(columnToString(column), key);
	}

	// 支持 MySQL 5.7.13+
	public List<T> listByJsonColumnKeyValue(SFunction<T, ?> column, String key, Object value) {
		Assert.notNull(column, "column 不可为null");
		Assert.hasText(key, "key 不可为空");
		return listByJsonColumnKeyValue(columnToString(column), key, value);
	}

	// 支持 MySQL 5.7.8+
	public List<T> listByJsonArrayColumnValue(SFunction<T, ?> column, Object value) {
		Assert.notNull(column, "column 不可为null");
		return listByJsonArrayColumnValue(columnToString(column), value);
	}

	// 支持 MySQL 8.0.17+
	public List<T> listByJsonArrayColumnValues(SFunction<T, ?> column, Collection<?> values) {
		Assert.notNull(column, "column 不可为null");
		return listByJsonArrayColumnValues(columnToString(column), values);
	}

	// 支持 MySQL 5.7.8+
	public List<T> listByJsonColumnKey(String column, String key) {
		Assert.hasText(column, "column 不可为空");
		Assert.hasText(key, "key 不可为空");

		return lambdaQuery()
			.apply(JSON_KEY_EXIST_SQL_FORMAT.formatted(column, key))
			.list();
	}

	// 支持 MySQL 5.7.13+
	public List<T> listByJsonColumnKeyValue(String column, String key, Object value) {
		Assert.hasText(column, "column 不可为空");
		Assert.hasText(key, "key 不可为空");

		return lambdaQuery()
			.apply(JSON_KEY_VALUE_SQL_FORMAT.formatted(column, key, getJsonValue(value)))
			.list();
	}

	// 支持 MySQL 5.7.8+
	public List<T> listByJsonArrayColumnValue(String column, Object value) {
		Assert.hasText(column, "column 不可为空");

		return lambdaQuery()
			.apply(JSON_ARRAY_VALUE_SQL_FORMAT.formatted(column, getJsonValue(value)))
			.list();
	}

	// 支持 MySQL 8.0.17+
	public List<T> listByJsonArrayColumnValues(String column, Collection<?> values) {
		Assert.hasText(column, "column 不可为空");

		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}

		return lambdaQuery()
			.apply(JSON_ARRAY_VALUES_SQL_FORMAT.formatted(column, JsonUtils.toJson(values)))
			.list();
	}

	public boolean existsById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	public boolean notExistsById(Serializable id) {
		return Objects.isNull(getById(id));
	}

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

	public <V> List<V> listColumnValue(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");
		return EntityUtils.getFieldValueList(lambdaQuery().select(column).list(), column);
	}

	public <V> List<V> listUniqueColumnValue(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为null");
		return EntityUtils.getUniqueFieldValueList(lambdaQuery().select(column).list(), column);
	}

	public <V> T getByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery().isNull(column).one();
		}
		return lambdaQuery().eq(column, value).one();
	}

	public <V> Optional<T> getOptByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return Optional.ofNullable(lambdaQuery().isNull(column).one());
		}
		return Optional.ofNullable(lambdaQuery().eq(column, value).one());
	}

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

	@Override
	public List<T> listByIds(Collection<? extends Serializable> ids) {
		return listByIds(ids, DEFAULT_BATCH_SIZE);
	}

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

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return listByColumnValues(column, values, DEFAULT_BATCH_SIZE);
	}

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

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values,
										  Supplier<LambdaQueryChainWrapper<T>> supplier) {
		return listByColumnValues(column, values, DEFAULT_BATCH_SIZE, supplier);
	}

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

	public <V> List<T> listByNotNullColumn(SFunction<T, V> column) {
		return lambdaQuery().isNotNull(column).list();
	}

	public <V> List<T> listByNullColumn(SFunction<T, V> column) {
		return lambdaQuery().isNull(column).list();
	}

	public List<T> listByLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public List<T> listByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeLeft(column, value)
			.list();
	}

	public List<T> listByLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeRight(column, value)
			.list();
	}

	public List<T> listByNotLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	public List<T> listByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeLeft(column, value)
			.list();
	}

	public List<T> listByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeRight(column, value)
			.list();
	}

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

	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values) {
		Assert.notNull(column, "column 不可为null");

		if (CollectionUtils.isEmpty(values)) {
			return false;
		}

		return lambdaUpdate()
			.in(column, values)
			.remove();
	}

	public boolean removeByLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.like(column, value)
			.remove();
	}

	public boolean removeByNotLikeColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLike(column, value)
			.remove();
	}

	public boolean removeByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeLeft(column, value)
			.remove();
	}

	public boolean removeByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeLeft(column, value)
			.remove();
	}

	public boolean removeByLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeRight(column, value)
			.remove();
	}

	public boolean removeByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		Assert.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeRight(column, value)
			.remove();
	}

	protected <V> String getJsonValue(V value) {
		if (Objects.isNull(value)) {
			return "null";
		} else if (value instanceof Number number) {
			return number.toString();
		} else if (value instanceof String string) {
			return string;
		} else if (value instanceof Boolean bool) {
			return bool.toString();
		} else {
			return JsonUtils.toString(value);
		}
	}

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
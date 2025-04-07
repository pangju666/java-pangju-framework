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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> {
	public static final int DEFAULT_LIST_BATCH_SIZE = 500;

	public List<T> listByJsonObjectValue(String columnName, String jsonObjectKey, Object jsonObjectValue) {
		return listByJsonObjectValue(lambdaQuery(), columnName, jsonObjectKey, jsonObjectValue);
	}

	public List<T> listByJsonObjectValue(LambdaQueryChainWrapper<T> queryChainWrapper, String columnName,
										 String jsonObjectKey, Object jsonObjectValue) {
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Validate.notBlank(columnName, "columnName 不可为空");
		Validate.notBlank(jsonObjectKey, "jsonObjectKey 不可为空");

		return queryChainWrapper
			.apply("{0}->>'$.{1}' = {2}", columnName, jsonObjectKey, getJsonValue(jsonObjectValue))
			.list();
	}

	public <V> List<T> listByEmptyJsonObject(SFunction<T, V> column) {
		Validate.notNull(column, "column 不可为null");

		return lambdaQuery()
			.like(column, Constants.EMPTY_JSON_OBJECT_STR)
			.list();
	}

	public <V> List<T> listByEmptyJsonObject(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Validate.notNull(column, "column 不可为null");

		return queryChainWrapper
			.like(column, Constants.EMPTY_JSON_OBJECT_STR)
			.list();
	}

	public List<T> listByJsonArrayValue(String columnName, Object jsonArrayValue) {
		return listByJsonArrayValue(lambdaQuery(), columnName, jsonArrayValue);
	}

	public List<T> listByJsonArrayValue(LambdaQueryChainWrapper<T> queryChainWrapper, String columnName, Object jsonArrayValue) {
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Validate.notBlank(columnName, "columnName 不可为空");

		return queryChainWrapper
			.apply("{0} member of ({1})", getJsonValue(jsonArrayValue), columnName)
			.list();
	}

	public <V> List<T> listByEmptyJsonArray(SFunction<T, V> column) {
		Validate.notNull(column, "column 不可为null");

		return lambdaQuery()
			.like(column, Constants.EMPTY_JSON_ARRAY_STR)
			.list();
	}

	public <V> List<T> listByEmptyJsonArray(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Validate.notNull(column, "column 不可为null");

		return queryChainWrapper
			.like(column, Constants.EMPTY_JSON_ARRAY_STR)
			.list();
	}

	public boolean existById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	public boolean notExistById(Serializable id) {
		return Objects.isNull(getById(id));
	}

	public <V> boolean existByColumnValue(SFunction<T, V> column, V value) {
		Validate.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.exists();
		}
		return lambdaQuery()
			.eq(column, value)
			.exists();
	}

	public <V> boolean notExistByColumnValue(SFunction<T, V> column, V value) {
		Validate.notNull(column, "column 不可为空");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNotNull(column)
				.exists();
		}
		return !lambdaQuery()
			.eq(column, value)
			.exists();
	}

	public <V> T getByColumnValue(SFunction<T, V> column, V value) {
		Validate.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.one();
		}
		return lambdaQuery()
			.eq(column, value)
			.one();
	}

	public <V> List<V> listColumnValue(SFunction<T, V> column) {
		return listColumnValue(lambdaQuery(), column, false, true);
	}

	public <V> List<V> listColumnValue(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		return listColumnValue(queryChainWrapper, column, false, true);
	}

	public <V> List<V> listUniqueColumnValue(SFunction<T, V> column) {
		return listColumnValue(lambdaQuery(), column, true, true);
	}

	public <V> List<V> listUniqueColumnValue(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		return listColumnValue(queryChainWrapper, column, false, true);
	}

	public <V> List<V> listColumnValue(SFunction<T, V> column, boolean unique, boolean nonNull) {
		return listColumnValue(lambdaQuery(), column, unique, nonNull);
	}

	public <V> List<V> listColumnValue(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column,
									   boolean unique, boolean nonNull) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

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

	@Override
	public List<T> listByIds(Collection<? extends Serializable> ids) {
		return listByIds(ids, DEFAULT_LIST_BATCH_SIZE);
	}

	public List<T> listByIds(Collection<? extends Serializable> ids, int batchSize) {
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

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
		return ListUtils.partition(validIdList, batchSize)
			.stream()
			.map(super::listByIds)
			.flatMap(List::stream)
			.toList();
	}

	public <V> List<T> listByColumnValue(SFunction<T, V> column, V value) {
		Validate.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.list();
		}
		return lambdaQuery()
			.eq(column, value)
			.list();
	}

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return listByColumnValues(lambdaQuery(), column, values, DEFAULT_LIST_BATCH_SIZE);
	}

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		return listByColumnValues(lambdaQuery(), column, values, batchSize);
	}

	public <V> List<T> listByColumnValues(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column,
										  Collection<V> values, int batchSize) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

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
		return ListUtils.partition(validList, batchSize)
			.stream()
			.map(part -> queryChainWrapper.in(column, part).list())
			.flatMap(List::stream)
			.toList();
	}

	public <V> List<T> listByNotNullColumn(SFunction<T, V> column) {
		return listByNotNullColumn(lambdaQuery(), column);
	}

	public <V> List<T> listByNotNullColumn(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNotNull(column).list();
	}

	public <V> List<T> listByNullColumn(SFunction<T, V> column) {
		return listByNullColumn(lambdaQuery(), column);
	}

	public <V> List<T> listByNullColumn(LambdaQueryChainWrapper<T> queryChainWrapper, SFunction<T, V> column) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNull(column).list();
	}

	public List<T> listByLikeColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public List<T> listByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeLeft(column, value)
			.list();
	}

	public List<T> listByLikeRightColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeRight(column, value)
			.list();
	}

	public List<T> listByNotLikeColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	public List<T> listByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeLeft(column, value)
			.list();
	}

	public List<T> listByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeRight(column, value)
			.list();
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean saveBatch(Collection<T> entityList) {
		return super.saveBatch(entityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<T> validEntityList = CollectionUtils.emptyIfNull(entityList)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validEntityList.isEmpty()) {
			return false;
		}
		return super.saveBatch(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateBatchById(Collection<T> entityList) {
		return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<T> validEntityList = CollectionUtils.emptyIfNull(entityList)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validEntityList.isEmpty()) {
			return false;
		}
		return super.updateBatchById(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean saveOrUpdateBatch(Collection<T> entityList) {
		return saveOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<T> validEntityList = CollectionUtils.emptyIfNull(entityList)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validEntityList.isEmpty()) {
			return false;
		}
		return super.saveOrUpdateBatch(validEntityList, batchSize);
	}

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

	public <V> boolean removeByColumnValue(SFunction<T, V> column, V value) {
		Validate.notNull(column, "column 不可为null");

		if (Objects.isNull(value)) {
			return false;
		}
		return lambdaUpdate()
			.eq(column, value)
			.remove();
	}

	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return removeByColumnValues(column, values, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		Validate.notNull(column, "column 不可为null");
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

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
		return ListUtils.partition(validList, batchSize)
			.stream()
			.allMatch(part -> lambdaUpdate()
				.in(column, part)
				.remove());
	}

	public boolean removeByLikeColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.like(column, value)
			.remove();
	}

	public boolean removeByNotLikeColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLike(column, value)
			.remove();
	}

	public boolean removeByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeLeft(column, value)
			.remove();
	}

	public boolean removeByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeLeft(column, value)
			.remove();
	}

	public boolean removeByLikeRightColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeRight(column, value)
			.remove();
	}

	public boolean removeByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLikeRight(column, value)
			.remove();
	}

	protected Object getJsonValue(Object value) {
		if (Objects.isNull(value)) {
			return "null";
		} else if (value instanceof String) {
			return "'" + value + "'";
		} else if (value instanceof Number || value instanceof Boolean) {
			return value;
		}
		return JsonUtils.toString(value);
	}
}
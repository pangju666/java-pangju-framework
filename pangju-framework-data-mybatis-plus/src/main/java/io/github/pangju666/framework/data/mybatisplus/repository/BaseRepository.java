package io.github.pangju666.framework.data.mybatisplus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import io.github.pangju666.commons.lang.utils.StreamUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> {
	protected static final int DEFAULT_LIST_BATCH_SIZE = 500;

	public boolean existById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	public boolean notExistById(Serializable id) {
		return Objects.isNull(getById(id));
	}

	public <V> boolean existByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.exists();
		}
		return lambdaQuery()
			.eq(column, value)
			.exists();
	}

	public <V> boolean notExistByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNotNull(column)
				.exists();
		}
		return !lambdaQuery()
			.eq(column, value)
			.exists();
	}

	public <V> T getByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
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
		return listColumnValue(column, false, true);
	}

	public <V> List<V> listUniqueColumnValue(SFunction<T, V> column) {
		return listColumnValue(column, true, true);
	}

	public <V> List<V> listColumnValue(SFunction<T, V> column, boolean unique, boolean nonNull) {
		Assert.notNull(column, "column 不可为空");
		var queryWrapper = lambdaQuery().select(column);
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
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<? extends Serializable> validIdList = StreamUtils.toNonNullList(ids);
		if (validIdList.size() <= batchSize) {
			return super.listByIds(validIdList);
		}
		return ListUtils.partition(new ArrayList<>(validIdList), batchSize)
			.stream()
			.map(super::listByIds)
			.flatMap(List::stream)
			.toList();
	}

	public <V> List<T> listByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
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
		return listByColumnValues(column, values, DEFAULT_LIST_BATCH_SIZE);
	}

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		Assert.notNull(column, "column 不可为空");
		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		List<V> validList = StreamUtils.toNonNullList(values);
		if (validList.size() <= batchSize) {
			return lambdaQuery()
				.in(column, validList)
				.list();
		}
		return ListUtils.partition(new ArrayList<>(validList), batchSize)
			.stream()
			.map(part -> lambdaQuery()
				.in(column, part)
				.list())
			.flatMap(List::stream)
			.toList();
	}

	public <V> List<T> listByNotNullColumn(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为空");
		return lambdaQuery()
			.isNotNull(column)
			.list();
	}

	public <V> List<T> listByNullColumn(SFunction<T, V> column) {
		Assert.notNull(column, "column 不可为空");
		return lambdaQuery()
			.isNull(column)
			.list();
	}

	public <V> List<T> listByLikeColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public <V> List<T> listByLikeLeftColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeLeft(column, value)
			.list();
	}

	public <V> List<T> listByLikeRightColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.likeRight(column, value)
			.list();
	}

	public <V> List<T> listByNotLikeColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	public <V> List<T> listByNotLikeLeftColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLikeLeft(column, value)
			.list();
	}

	public <V> List<T> listByNotLikeRightColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
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
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		if (CollectionUtils.isEmpty(validEntityList)) {
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
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		if (CollectionUtils.isEmpty(validEntityList)) {
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
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		if (CollectionUtils.isEmpty(validEntityList)) {
			return false;
		}
		return super.saveOrUpdateBatch(validEntityList, batchSize);
	}

	@Override
	public boolean removeByIds(Collection<?> list) {
		List<?> validList = StreamUtils.toNonNullList(list);
		if (CollectionUtils.isEmpty(validList)) {
			return false;
		}
		return super.removeByIds(validList);
	}

	@Override
	public boolean removeByIds(Collection<?> list, boolean useFill) {
		List<?> validList = StreamUtils.toNonNullList(list);
		if (CollectionUtils.isEmpty(validList)) {
			return false;
		}
		return super.removeByIds(validList, useFill);
	}

	public <V> boolean removeByColumnValue(SFunction<T, V> column, V value) {
		Assert.notNull(column, "column 不可为空");
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
		Assert.notNull(column, "column 不可为空");
		List<V> validList = StreamUtils.toNonNullList(values);
		if (CollectionUtils.isEmpty(validList)) {
			return false;
		}
		if (validList.size() <= batchSize) {
			return lambdaUpdate()
				.in(column, validList)
				.remove();
		}
		return ListUtils.partition(new ArrayList<>(validList), batchSize)
			.stream()
			.allMatch(part -> lambdaUpdate()
				.in(column, part)
				.remove());
	}

	public <V> boolean removeByLikeColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.like(column, value)
			.remove();
	}

	public <V> boolean removeByNotLikeColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.notLike(column, value)
			.remove();
	}

	public <V> boolean removeByLikeLeftColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeLeft(column, value)
			.remove();
	}

	public <V> boolean removeByLikeRightColumnValue(SFunction<T, V> column, String value) {
		Assert.notNull(column, "column 不可为空");
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return lambdaUpdate()
			.likeRight(column, value)
			.remove();
	}
}
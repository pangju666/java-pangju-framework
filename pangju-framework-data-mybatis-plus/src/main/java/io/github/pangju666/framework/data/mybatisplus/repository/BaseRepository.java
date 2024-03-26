package io.github.pangju666.framework.data.mybatisplus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.pangju666.commons.lang.utils.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseRepository<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
	protected static final int DEFAULT_ID_LIST_SIZE = 500;

	public boolean existById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	public boolean notExistById(Serializable id) {
		return Objects.isNull(getById(id));
	}

	public boolean existByColumn(SFunction<T, ?> column, Object val) {
		return !notExistByColumn(column, val);
	}

	public boolean notExistByColumn(SFunction<T, ?> column, Object val) {
		return lambdaQuery()
			.eq(column, val)
			.list()
			.isEmpty();
	}

	public List<?> listColumnValue(SFunction<T, ?> column) {
		return listColumnValue(column, false, true);
	}

	public List<?> listUniqueColumnValue(SFunction<T, ?> column) {
		return listColumnValue(column, true, true);
	}

	public List<?> listColumnValue(SFunction<T, ?> column, boolean unique, boolean nonNull) {
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
	public List<T> listByIds(Collection<? extends Serializable> idCollection) {
		return listByIds(idCollection, DEFAULT_ID_LIST_SIZE);
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

	public List<T> listByColumn(SFunction<T, ?> column, Collection<? extends Serializable> collection) {
		return listByColumn(column, collection, DEFAULT_ID_LIST_SIZE);
	}

	public List<T> listByColumn(SFunction<T, ?> column, Collection<? extends Serializable> collection, int batchSize) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		List<? extends Serializable> validList = StreamUtils.toNonNullList(collection);
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

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveBatch(validEntityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveBatch(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.updateBatchById(validEntityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.updateBatchById(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveOrUpdateBatch(validEntityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveOrUpdateBatch(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeByIds(Collection<?> list) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		List<?> validList = StreamUtils.toNonNullList(list);
		return super.removeByIds(validList);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeBatchByIds(Collection<?> list) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		List<?> validList = StreamUtils.toNonNullList(list);
		return super.removeBatchByIds(validList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeBatchByIds(Collection<?> list, int batchSize) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		List<?> validList = StreamUtils.toNonNullList(list);
		return super.removeBatchByIds(validList, batchSize);
	}
}
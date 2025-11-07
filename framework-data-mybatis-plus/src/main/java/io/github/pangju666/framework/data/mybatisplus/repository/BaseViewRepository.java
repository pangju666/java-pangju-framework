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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public abstract class BaseViewRepository<M extends BaseMapper<T>, T> extends BaseRepository<M, T> {
	@Override
	public final boolean save(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean saveBatch(Collection<T> entityList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean saveOrUpdateBatch(Collection<T> entityList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean updateBatchById(Collection<T> entityList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeBatchByIds(Collection<?> list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeById(Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeById(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByMap(Map<String, Object> columnMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean remove(Wrapper<T> queryWrapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByIds(Collection<?> list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByIds(Collection<?> list, boolean useFill) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean updateById(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean update(Wrapper<T> updateWrapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean update(T entity, Wrapper<T> updateWrapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean saveBatch(Collection<T> entityList, int batchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean saveOrUpdate(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean updateBatchById(Collection<T> entityList, int batchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <V> boolean replaceColumnValue(SFunction<T, V> column, V newValue, V oldValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeById(Serializable id, boolean useFill) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final KtUpdateChainWrapper<T> ktUpdate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final UpdateChainWrapper<T> update() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final LambdaUpdateChainWrapper<T> lambdaUpdate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <V> boolean removeByColumnValue(SFunction<T, V> column, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <V> boolean removeByColumnValues(SFunction<T, V> column, Collection<V> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByLikeColumnValue(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByNotLikeColumnValue(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByNotLikeLeftColumnValue(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByNotLikeRightColumnValue(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByLikeLeftColumnValue(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByLikeRightColumnValue(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}
}
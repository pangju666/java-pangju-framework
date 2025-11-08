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

/**
 * 只读仓储基类，用于绑定数据库视图（view）或只读表的实体。
 *
 * <p><b>设计目标</b></p>
 * <ul>
 *   <li>禁用所有数据写入与删除相关操作，统一抛出 {@code UnsupportedOperationException}。</li>
 *   <li>保留查询相关能力（如单体查询、列表查询、分页、计数、条件构造等），具体实现继承自 {@link BaseRepository}。</li>
 * </ul>
 *
 * <p><b>使用场景</b></p>
 * <ul>
 *   <li>绑定数据库视图或只读库，避免服务层误写。</li>
 *   <li>需要使用 MyBatis-Plus 的通用查询能力，但不允许变更数据。</li>
 * </ul>
 *
 * <p><b>泛型说明</b></p>
 * <ul>
 *   <li>M：{@link com.baomidou.mybatisplus.core.mapper.BaseMapper} 的实现类型。</li>
 *   <li>T：实体类型。</li>
 * </ul>
 *
 * <p><b>行为约定</b></p>
 * <ul>
 *   <li>所有写操作方法均为 {@code final} 并抛出 {@code UnsupportedOperationException}（包括 save、update、remove、批量写、链式更新等）。</li>
 *   <li>若后续需要写入能力，请改用 {@link BaseRepository} 或在业务层使用专用写仓储。</li>
 * </ul>
 *
 * <p><b>线程安全</b></p>
 * <ul>
 *   <li>仓储自身无状态；线程安全取决于底层数据源与事务管理。</li>
 * </ul>
 *
 * @author pangju666
 * @see BaseRepository
 * @since 1.0.0
 */
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
	public final boolean removeByColumnLike(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByColumnNotLike(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByColumnNotLikeLeft(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByColumnNotLikeRight(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByColumnLikeLeft(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeByColumnLikeRight(SFunction<T, String> column, String value) {
		throw new UnsupportedOperationException();
	}
}
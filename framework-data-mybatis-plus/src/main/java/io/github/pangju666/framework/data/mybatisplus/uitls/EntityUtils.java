/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICTNST-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTITS OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.data.mybatisplus.uitls;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mybatis Plus 数据表实体工具类
 * <p>
 * 基于 MyBatis-Plus 的 {@link SFunction} 方法引用安全地提取实体字段，
 * 提供列表、集合、去重列表、映射、分组以及求和/平均等常用聚合操作。
 * 所有方法均对 {@code null} 或空集合进行安全处理，返回空容器或零值；
 * 数值聚合会将 {@code null} 当作 0 参与计算。
 * </p>
 *
 * <p>使用约定：</p>
 * <ul>
 *   <li>字段提取函数 {@code sFunction} 通常为实体 getter 的方法引用，例如 {@code User::getId}</li>
 *   <li>对于数值聚合（求和/平均），{@code null} 数值会按 0 处理参与计算</li>
 * </ul>
 *
 * <p>线程安全：类为无状态工具类，方法均为静态，线程安全。</p>
 */
public class EntityUtils {
	/**
	 * 工具类构造器
	 * <p>受保护的空构造，避免被实例化。</p>
	 */
	protected EntityUtils() {
	}

	/**
	 * 提取字段并生成列表（保留顺序，允许重复）
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  字段提取函数（方法引用），例如 {@code User::getId}
	 * @param <T>        实体类型
	 * @param <V>        字段值类型
	 * @return 非 {@code null} 的列表；当集合为空或为 {@code null} 时返回空列表；过滤掉为 {@code null} 的字段值
	 */
	public static <T, V> List<V> getFieldValueList(final Collection<T> collection, final SFunction<T, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(sFunction)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * 提取字段并生成集合（去重，不保证顺序）
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  字段提取函数（方法引用）
	 * @param <T>        实体类型
	 * @param <V>        字段值类型
	 * @return 非 {@code null} 的集合；当集合为空或为 {@code null} 时返回空集合；过滤掉为 {@code null} 的字段值
	 */
	public static <T, V> Set<V> getFieldValueSet(final Collection<T> collection, final SFunction<T, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(sFunction)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	/**
	 * 提取字段并生成去重列表（保留首次出现的顺序）
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  字段提取函数（方法引用）
	 * @param <T>        实体类型
	 * @param <V>        字段值类型
	 * @return 非 {@code null} 的列表；当集合为空或为 {@code null} 时返回空列表；过滤掉为 {@code null} 的字段值
	 */
	public static <T, V> List<V> getUniqueFieldValueList(final Collection<T> collection, final SFunction<T, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(sFunction)
			.filter(Objects::nonNull)
			.distinct()
			.collect(Collectors.toList());
	}

	/**
	 * 以字段值作为键，将实体映射为 {@code Map}。
	 * <p>
	 * 行为说明：
	 * </p>
	 * <ul>
	 *   <li>会过滤掉字段值为 {@code null} 的实体（不包含空键）。</li>
	 *   <li>当存在重复键时，{@link Collectors#toMap} 将抛出 {@link IllegalStateException}，请确保键唯一或先去重。</li>
	 *   <li>当集合为空或为 {@code null} 时返回空映射。</li>
	 * </ul>
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  字段提取函数（方法引用），不可为 {@code null}
	 * @param <T>        实体类型
	 * @param <V>        键的类型（字段值类型）
	 * @return 以非 {@code null} 字段值为键的映射；当集合为空或为 {@code null} 时返回空映射
	 */
	public static <T, V> Map<V, T> mapByField(final Collection<T> collection, final SFunction<T, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.filter(item -> Objects.nonNull(sFunction.apply(item)))
			.collect(Collectors.toMap(sFunction, item -> item));
	}

	/**
	 * 按字段值对实体进行分组。
	 * <p>
	 * 行为说明：
	 * </p>
	 * <ul>
	 *   <li>会过滤掉字段值为 {@code null} 的实体（不包含空分组）。</li>
	 *   <li>分组内的列表保留原始相对顺序。</li>
	 *   <li>当集合为空或为 {@code null} 时返回空映射。</li>
	 * </ul>
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  字段提取函数（方法引用），不可为 {@code null}
	 * @param <T>        实体类型
	 * @param <V>        分组键类型（字段值类型）
	 * @return 非 {@code null} 的分组映射；当集合为空或为 {@code null} 时返回空映射
	 */
	public static <T, V> Map<V, List<T>> groupByField(final Collection<T> collection, final SFunction<T, V> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyMap();
		}
		return collection.stream()
			.filter(item -> Objects.nonNull(sFunction.apply(item)))
			.collect(Collectors.groupingBy(sFunction, Collectors.mapping(
				item -> item, Collectors.toList())));
	}

	/**
	 * 对数值字段求和（转换为 {@code double}）
	 * <p>
	 * 行为：空集合或 {@code null} 返回 0；提取出的数值为 {@code null} 时按 0 处理参与求和。
	 * </p>
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  数值字段提取函数（方法引用），返回 {@link Number}
	 * @param <T>        实体类型
	 * @return 求和结果，空集合或 {@code null} 返回 0
	 */
	public static <T> double sumFieldValue(final Collection<T> collection, final SFunction<T, ? extends Number> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return 0;
		}
		return collection.stream()
			.mapToDouble(item -> {
				Number value = sFunction.apply(item);
				return Objects.nonNull(value) ? value.doubleValue() : 0;
			})
			.sum();
	}

	/**
	 * 对数值字段求平均（转换为 {@code double}）
	 * <p>
	 * 行为：空集合或 {@code null} 返回 {@code 0d}；提取出的数值为 {@code null} 时按 0 处理参与计算。
	 * </p>
	 *
	 * @param collection 实体集合，可为 {@code null}
	 * @param sFunction  数值字段提取函数（方法引用），返回 {@link Number}
	 * @param <T>        实体类型
	 * @return 平均值，空集合或 {@code null} 返回 {@code 0d}
	 */
	public static <T> double averageFieldValue(final Collection<T> collection, final SFunction<T, ? extends Number> sFunction) {
		if (CollectionUtils.isEmpty(collection)) {
			return 0d;
		}
		return collection.stream()
			.mapToDouble(item -> {
				Number value = sFunction.apply(item);
				return Objects.nonNull(value) ? value.doubleValue() : 0;
			})
			.average()
			.orElse(0d);
	}
}

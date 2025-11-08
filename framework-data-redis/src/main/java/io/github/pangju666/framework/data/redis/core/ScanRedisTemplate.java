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

package io.github.pangju666.framework.data.redis.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 扫描增强版 RedisTemplate。
 *
 * <p>用途：围绕 Redis SCAN 命令提供简洁的扫描 API，
 * 将游标结果聚合为集合或映射。</p>
 *
 * <p>行为特性：</p>
 * <ul>
 *   <li>采用渐进式迭代（{@link Cursor}），方法完成后自动关闭游标。</li>
 *   <li>{@code count} 为服务端返回数量建议；当 {@code count <= 0} 时直接返回空结果。</li>
 *   <li>提供匹配模式由服务器端过滤；ZSet 扫描结果按默认比较排序并返回 {@link SortedSet}。</li>
 * </ul>
 *
 * <p>匹配模式的序列化器要求：</p>
 * <ul>
 *   <li>当设置匹配模式（{@link org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder#match(String)}）时，相关序列化器必须支持 {@link String} 序列化，否则抛出 {@link UnsupportedOperationException}。</li>
 *   <li>键扫描：需 {@link #getKeySerializer()} 支持 {@code String}（见 {@link #scanKeys(ScanOptions)}）。</li>
 *   <li>Set/ZSet 元素扫描：需 {@link #getValueSerializer()} 支持 {@code String}（见 {@link #scanSetValues(Object, ScanOptions)}、{@link #scanZSetValues(Object, ScanOptions)}）。</li>
 *   <li>Hash 值扫描：需 {@link #getHashValueSerializer()} 支持 {@code String}（见 {@link #scanHashValues(Object, ScanOptions)}）。</li>
 * </ul>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author pangju666
 * @since 1.0.0
 * @see RedisTemplate
 */
public class ScanRedisTemplate<K, V> extends RedisTemplate<K, V> {
	/**
	 * 无参构造。
	 *
	 * @since 1.0.0
	 */
	public ScanRedisTemplate() {
		super();
	}

	/**
	 * 复制给定 {@link RedisTemplate} 的配置（序列化器、连接工厂）。
	 *
	 * @param redisTemplate 现有模板实例
	 * @since 1.0.0
	 */
	public ScanRedisTemplate(RedisTemplate<K, V> redisTemplate) {
		this();
		setKeySerializer(redisTemplate.getKeySerializer());
		setValueSerializer(redisTemplate.getValueSerializer());
		setHashKeySerializer(redisTemplate.getHashKeySerializer());
		setHashValueSerializer(redisTemplate.getHashValueSerializer());
		setConnectionFactory(redisTemplate.getConnectionFactory());
	}

	/**
	 * 按类型扫描所有键。
	 *
	 * @param dataType 键的数据类型（如 {@link DataType#STRING}、{@link DataType#SET} 等）
	 * @return 匹配类型的键集合；无匹配时为空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeys(DataType dataType) {
		return scanKeys(scanOptions(null, dataType, null));
	}

	/**
	 * 按类型扫描键（指定每次扫描数量）。
	 *
	 * @param dataType 键的数据类型
	 * @param count    每次迭代建议返回的数量；{@code count <= 0} 时返回空集合
	 * @return 键集合；无匹配或 {@code count <= 0} 时为空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeys(DataType dataType, long count) {
		if (count <= 0) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(null, dataType, count));
	}

	/**
	 * 使用默认扫描选项扫描所有键。
	 *
	 * @return 键集合；无匹配时为空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeys() {
		return scanKeys(ScanOptions.NONE);
	}

	/**
	 * 使用指定扫描选项扫描键。
	 *
	 * <p>当提供匹配模式且当前 key 序列化器无法序列化 {@link String} 类型时，抛出 {@link UnsupportedOperationException}。</p>
	 *
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @return 键集合；无匹配时为空集合
	 * @throws IllegalArgumentException 当 {@code scanOptions} 为 {@code null}
	 * @throws UnsupportedOperationException 当提供模式且 key 序列化器不支持 {@code String} 序列化
	 * @since 1.0.0
	 */
	public Set<K> scanKeys(ScanOptions scanOptions) {
		Assert.notNull(scanOptions, "scanOptions 不可为null");
		if (StringUtils.isNotBlank(scanOptions.getPattern()) && !getKeySerializer().canSerialize(String.class)) {
			throw new UnsupportedOperationException();
		}

		try (Cursor<K> cursor = super.scan(scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 扫描 ZSet 的元素（默认选项）。
	 *
	 * @param key ZSet 的键；不可为 {@code null}
	 * @return 有序的元素集合（按默认比较规则排序）；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为 {@code null}
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<V>> scanZSetValues(K key) {
		return scanZSetValues(key, ScanOptions.NONE);
	}

	/**
	 * 扫描 ZSet 的元素（指定每次扫描数量）。
	 *
	 * @param key   ZSet 的键；不可为 {@code null}
	 * @param count 每次迭代建议返回的数量；{@code count <= 0} 时返回空集合
	 * @return 有序的元素集合；无元素或 {@code count <= 0} 时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为 {@code null}
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<V>> scanZSetValues(K key, long count) {
		if (count <= 0) {
			return Collections.emptySortedSet();
		}
		return scanZSetValues(key, scanOptions(null, null, count));
	}

	/**
	 * 扫描 ZSet 的元素（指定扫描选项）。
	 *
	 * <p>当提供匹配模式且当前 value 序列化器无法序列化 {@link String} 类型时，抛出 {@link UnsupportedOperationException}。</p>
	 *
	 * @param key         ZSet 的键；不可为 {@code null}
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @return 有序的元素集合；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code scanOptions} 为 {@code null}
	 * @throws UnsupportedOperationException 当提供模式且 value 序列化器不支持 {@code String} 序列化
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<V>> scanZSetValues(K key, ScanOptions scanOptions) {
		Assert.notNull(key, "key 不可为null");
		Assert.notNull(scanOptions, "scanOptions 不可为null");
		if (StringUtils.isNotBlank(scanOptions.getPattern()) && !getValueSerializer().canSerialize(String.class)) {
			throw new UnsupportedOperationException();
		}

		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = super.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 扫描 Set 的元素（默认选项）。
	 *
	 * @param key Set 的键；不可为 {@code null}
	 * @return 元素集合；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为 {@code null}
	 * @since 1.0.0
	 */
	public Set<V> scanSetValues(K key) {
		return scanSetValues(key, ScanOptions.NONE);
	}

	/**
	 * 扫描 Set 的元素（指定每次扫描数量）。
	 *
	 * @param key   Set 的键；不可为 {@code null}
	 * @param count 每次迭代建议返回的数量；{@code count <= 0} 时返回空集合
	 * @return 元素集合；无元素或 {@code count <= 0} 时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为 {@code null}
	 * @since 1.0.0
	 */
	public Set<V> scanSetValues(K key, long count) {
		if (count <= 0) {
			return Collections.emptySet();
		}
		return scanSetValues(key, scanOptions(null, null, count));
	}

	/**
	 * 扫描 Set 的元素（指定扫描选项）。
	 *
	 * <p>当提供匹配模式时，按 Redis 服务器端匹配进行过滤。</p>
	 * <p>当提供匹配模式且当前 value 序列化器无法序列化 {@link String} 类型时，抛出 {@link UnsupportedOperationException}。</p>
	 *
	 * @param key         Set 的键；不可为 {@code null}
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @return 元素集合；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code scanOptions} 为 {@code null}
	 * @throws UnsupportedOperationException 当提供模式且 value 序列化器不支持 {@code String} 序列化
	 * @since 1.0.0
	 */
	public Set<V> scanSetValues(K key, ScanOptions scanOptions) {
		Assert.notNull(key, "key 不可为null");
		Assert.notNull(scanOptions, "scanOptions 不可为null");
		if (StringUtils.isNotBlank(scanOptions.getPattern()) && !getValueSerializer().canSerialize(String.class)) {
			throw new UnsupportedOperationException();
		}

		try (Cursor<V> cursor = super.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 扫描 Hash 的键值对（默认选项）。
	 *
	 * @param key Hash 的键；不可为 {@code null}
	 * @param <HK> 哈希键类型
	 * @param <HV> 哈希值类型
	 * @return 键值映射；无元素时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为 {@code null}
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValues(K key) {
		return scanHashValues(key, ScanOptions.NONE);
	}

	/**
	 * 扫描 Hash 的键值对（指定每次扫描数量）。
	 *
	 * @param key   Hash 的键；不可为 {@code null}
	 * @param count 每次迭代建议返回的数量；{@code count <= 0} 时返回空映射
	 * @param <HK> 哈希键类型
	 * @param <HV> 哈希值类型
	 * @return 键值映射；无元素或 {@code count <= 0} 时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为 {@code null}
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValues(K key, long count) {
		if (count <= 0) {
			return Collections.emptyMap();
		}
		return scanHashValues(key, scanOptions(null, null, count));
	}

	/**
	 * 扫描 Hash 的键值对（指定扫描选项）。
	 *
	 * <p>当提供匹配模式且当前 hash value 序列化器无法序列化 {@link String} 类型时，抛出 {@link UnsupportedOperationException}。</p>
	 *
	 * @param key         Hash 的键；不可为 {@code null}
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @param <HK> 哈希键类型
	 * @param <HV> 哈希值类型
	 * @return 键值映射；无元素时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code scanOptions} 为 {@code null}
	 * @throws UnsupportedOperationException 当提供模式且 hash value 序列化器不支持 {@code String} 序列化
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValues(K key, ScanOptions scanOptions) {
		Assert.notNull(key, "key 不可为null");
		Assert.notNull(scanOptions, "scanOptions 不可为null");
		if (StringUtils.isNotBlank(scanOptions.getPattern()) && !getHashValueSerializer().canSerialize(String.class)) {
			throw new UnsupportedOperationException();
		}

		HashOperations<K, HK, HV> hashOperations = super.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 构建扫描选项。
	 *
	 * @param pattern  键匹配模式（例如 {@code user:*}）；为空或空白时不设置匹配
	 * @param dataType 过滤键的数据类型；为 {@code null} 时不设置类型过滤
	 * @param count    每次迭代建议返回的数量；为 {@code null} 时不设置数量建议
	 * @return 构建完成的扫描选项
	 * @since 1.0.0
	 */
	public ScanOptions scanOptions(@Nullable String pattern, @Nullable DataType dataType, @Nullable Long count) {
		ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions();
		if (Objects.nonNull(count)) {
			builder.count(count);
		}
		if (Objects.nonNull(dataType)) {
			builder.type(dataType);
		}
		if (StringUtils.isNotBlank(pattern)) {
			builder.match(pattern);
		}
		return builder.build();
	}
}
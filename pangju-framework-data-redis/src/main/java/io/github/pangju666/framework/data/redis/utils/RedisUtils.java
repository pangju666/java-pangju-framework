/*
 * Copyright 2011-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.pangju666.framework.data.redis.utils;

import io.github.pangju666.framework.data.redis.model.ZSetValue;
import io.github.pangju666.framework.data.redis.pool.RedisConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis工具类，提供基于SCAN命令的渐进式扫描方法
 * <p>
 * 主要功能：
 * <ul>
 *     <li>键操作：支持按后缀、前缀、关键字扫描键</li>
 *     <li>有序集合操作：支持按模式扫描成员及其分数</li>
 *     <li>集合操作：支持按模式扫描成员</li>
 *     <li>哈希表操作：支持按模式扫描字段和值</li>
 * </ul>
 * 特点：
 * <ul>
 *     <li>所有扫描操作都使用SCAN命令族，避免阻塞Redis</li>
 *     <li>支持指定每次扫描返回的数量</li>
 *     <li>支持自定义匹配模式</li>
 *     <li>支持指定数据类型</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RedisUtils {
	protected RedisUtils() {
	}

	/**
	 * 使用{@link RedisConstants#REDIS_PATH_DELIMITER 分隔符}组合多个键
	 *
	 * @param keys 要组合的键数组
	 * @return 组合后的键字符串
	 * @since 1.0.0
	 */
	public static String computeKey(final String... keys) {
		return StringUtils.join(Arrays.asList(keys), RedisConstants.REDIS_PATH_DELIMITER);
	}

	/**
	 * 按后缀扫描键
	 * <p>
	 * 此方法使用SCAN命令进行渐进式扫描，避免使用KEYS命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的键，例如：
	 * <ul>
	 *     <li>suffix="user" 将匹配 "app:user"、"system:user" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param suffix          后缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @return 匹配的键集合，如果suffix为空则返回空集合
	 * @throws IllegalArgumentException 当redisOperations为null时抛出
	 * @see #scanOptionsBySuffix(String)
	 * @since 1.0.0
	 */
	public static <K> Set<K> scanKeysBySuffix(final String suffix, final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");

		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}
		try (Cursor<K> cursor = redisOperations.scan(scanOptionsBySuffix(suffix))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按前缀扫描键
	 * <p>
	 * 此方法使用SCAN命令进行渐进式扫描，避免使用KEYS命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的键，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:1"、"user:profile" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param prefix          前缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @return 匹配的键集合，如果prefix为空则返回空集合
	 * @throws IllegalArgumentException 当redisOperations为null时抛出
	 * @see #scanOptionsByPrefix(String)
	 * @since 1.0.0
	 */
	public static <K> Set<K> scanKeysByPrefix(final String prefix, final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");

		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}
		try (Cursor<K> cursor = redisOperations.scan(scanOptionsByPrefix(prefix))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按关键字扫描键
	 * <p>
	 * 此方法使用SCAN命令进行渐进式扫描，避免使用KEYS命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的键，例如：
	 * <ul>
	 *     <li>keyword="user" 将匹配 "app:user:1"、"system:user:profile" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword         关键字
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @return 匹配的键集合，如果keyword为空则返回空集合
	 * @throws IllegalArgumentException 当redisOperations为null时抛出
	 * @see #scanOptionsByKeyword(String)
	 * @since 1.0.0
	 */
	public static <K> Set<K> scanKeysByKeyword(final String keyword, final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");

		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}
		try (Cursor<K> cursor = redisOperations.scan(scanOptionsByKeyword(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按数据类型扫描键
	 * <p>
	 * 扫描指定数据类型的所有键。此方法使用SCAN命令进行渐进式扫描，
	 * 避免使用KEYS命令可能带来的性能问题。
	 * </p>
	 *
	 * @param dataType        数据类型，支持的类型包括：STRING、LIST、SET、ZSET、HASH、STREAM
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @return 匹配数据类型的键集合，如果dataType为null则返回空集合
	 * @throws IllegalArgumentException 当redisOperations为null时抛出
	 * @since 1.0.0
	 */
	public static <K> Set<K> scanKeysByDataType(final DataType dataType, final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");

		if (Objects.isNull(dataType)) {
			return Collections.emptySet();
		}
		try (Cursor<K> cursor = redisOperations.scan(scanOptions(null, dataType, null))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 扫描所有键
	 * <p>
	 * 扫描Redis中的所有键，不进行任何过滤。此方法使用SCAN命令进行渐进式扫描，
	 * 避免使用KEYS命令可能带来的性能问题。
	 * </p>
	 *
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @return 所有键的集合
	 * @throws IllegalArgumentException 当redisOperations为null时抛出
	 * @since 1.0.0
	 */
	public static <K> Set<K> scanKeys(final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");

		try (Cursor<K> cursor = redisOperations.scan(ScanOptions.NONE)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 使用自定义扫描选项扫描键
	 * <p>
	 * 根据提供的扫描选项扫描Redis中的键。此方法使用SCAN命令进行渐进式扫描，
	 * 可以通过ScanOptions自定义匹配模式、数据类型和返回数量等选项。
	 * </p>
	 *
	 * @param scanOptions     扫描选项，可以指定匹配模式、数据类型和返回数量
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @return 匹配的键集合，如果scanOptions为null则返回空集合
	 * @throws IllegalArgumentException 当redisOperations为null时抛出
	 * @see ScanOptions
	 * @since 1.0.0
	 */
	public static <K> Set<K> scanKeys(final ScanOptions scanOptions, final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");

		if (Objects.isNull(scanOptions)) {
			return Collections.emptySet();
		}

		try (Cursor<K> cursor = redisOperations.scan(scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按后缀扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的成员，例如：
	 * <ul>
	 *     <li>suffix="score" 将匹配 "user:score"、"game:score" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             有序集合的键
	 * @param suffix          后缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员及其分数的有序集合，如果suffix为空则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsBySuffix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, V> SortedSet<ZSetValue<V>> scanZSetValuesBySuffix(final K key, final String suffix,
																		final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySortedSet();
		}
		ScanOptions scanOptions = scanOptionsBySuffix(suffix, DataType.ZSET, null);
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisOperations.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 按前缀扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的成员，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:1"、"user:score" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             有序集合的键
	 * @param prefix          前缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员及其分数的有序集合，如果prefix为空则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsByPrefix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, V> SortedSet<ZSetValue<V>> scanZSetValuesByPrefix(final K key, final String prefix,
																		final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySortedSet();
		}
		ScanOptions scanOptions = scanOptionsByPrefix(prefix, DataType.ZSET, null);
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisOperations.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 按关键字扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的成员，例如：
	 * <ul>
	 *     <li>keyword="score" 将匹配 "high:score"、"user:score:100" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             有序集合的键
	 * @param keyword         关键字
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员及其分数的有序集合，如果keyword为空则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsByKeyword(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, V> SortedSet<ZSetValue<V>> scanZSetValuesByKeyword(final K key, final String keyword,
																		 final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySortedSet();
		}
		ScanOptions scanOptions = scanOptionsByKeyword(keyword, DataType.ZSET, null);
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisOperations.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 扫描有序集合的所有成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有成员</li>
	 *     <li>返回结果按分数降序排序</li>
	 *     <li>支持大数据量的有序集合扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             有序集合的键
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 所有成员及其分数的有序集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @since 1.0.0
	 */
	public static <K, V> SortedSet<ZSetValue<V>> scanZSetValues(final K key, final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisOperations.opsForZSet().scan(key, ScanOptions.NONE)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 使用自定义扫描选项扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定每次扫描返回的数量</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             有序集合的键
	 * @param scanOptions     扫描选项，可以指定匹配模式和返回数量
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员及其分数的有序集合，如果scanOptions为null则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see ScanOptions
	 * @since 1.0.0
	 */
	public static <K, V> SortedSet<ZSetValue<V>> scanZSetValues(final K key, final ScanOptions scanOptions,
																final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (Objects.isNull(scanOptions)) {
			return Collections.emptySortedSet();
		}
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisOperations.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 按后缀扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的成员，例如：
	 * <ul>
	 *     <li>suffix="user" 将匹配 "app:user"、"system:user" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             集合的键
	 * @param suffix          后缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员集合，如果suffix为空则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsBySuffix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> scanSetValuesBySuffix(final K key, final String suffix,
													  final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}
		ScanOptions scanOptions = scanOptionsBySuffix(suffix, DataType.SET, null);
		try (Cursor<V> cursor = redisOperations.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按前缀扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的成员，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:1"、"user:profile" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             集合的键
	 * @param prefix          前缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员集合，如果prefix为空则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsByPrefix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> scanSetValuesByPrefix(final K key, final String prefix,
													  final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}

		ScanOptions scanOptions = scanOptionsByPrefix(prefix, DataType.SET, null);
		try (Cursor<V> cursor = redisOperations.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按关键字扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的成员，例如：
	 * <ul>
	 *     <li>keyword="user" 将匹配 "app:user:1"、"system:user:profile" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             集合的键
	 * @param keyword         关键字
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员集合，如果keyword为空则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsByKeyword(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> scanSetValuesByKeyword(final K key, final String keyword,
													   final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}

		ScanOptions scanOptions = scanOptionsByKeyword(keyword, DataType.SET, null);
		try (Cursor<V> cursor = redisOperations.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 扫描集合的所有成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有成员</li>
	 *     <li>返回结果无序且自动去重</li>
	 *     <li>支持大数据量的集合扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             集合的键
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 集合中的所有成员
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> scanSetValues(final K key, final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		try (Cursor<V> cursor = redisOperations.opsForSet().scan(key, ScanOptions.NONE)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 使用自定义扫描选项扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定每次扫描返回的数量</li>
	 *     <li>返回结果无序且自动去重</li>
	 *     <li>使用try-with-resources自动关闭游标</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             集合的键
	 * @param scanOptions     扫描选项，可以指定匹配模式和返回数量
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <V>             值的类型
	 * @return 匹配的成员集合，如果scanOptions为null则返回空集合
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see ScanOptions
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> scanSetValues(final K key, final ScanOptions scanOptions,
											  final RedisOperations<K, V> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (Objects.isNull(scanOptions)) {
			return Collections.emptySet();
		}

		try (Cursor<V> cursor = redisOperations.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按后缀扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的字段，例如：
	 * <ul>
	 *     <li>suffix="name" 将匹配 "first:name"、"last:name" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             哈希表的键
	 * @param suffix          后缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <HK>            哈希字段的类型
	 * @param <HV>            哈希值的类型
	 * @return 匹配的字段和值的映射，如果suffix为空则返回空映射
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsBySuffix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> scanHashValuesBySuffix(final K key, final String suffix,
																 final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(suffix)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = redisOperations.opsForHash();
		ScanOptions scanOptions = scanOptionsBySuffix(suffix, DataType.HASH, null);
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 按前缀扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的字段，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:id"、"user:name" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             哈希表的键
	 * @param prefix          前缀字符串
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <HK>            哈希字段的类型
	 * @param <HV>            哈希值的类型
	 * @return 匹配的字段和值的映射，如果prefix为空则返回空映射
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsByPrefix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> scanHashValuesByPrefix(final K key, final String prefix,
																 final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(prefix)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = redisOperations.opsForHash();
		ScanOptions scanOptions = scanOptionsByPrefix(prefix, DataType.HASH, null);
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 按关键字扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的字段，例如：
	 * <ul>
	 *     <li>keyword="name" 将匹配 "first:name"、"user:name:full" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             哈希表的键
	 * @param keyword         关键字
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <HK>            哈希字段的类型
	 * @param <HV>            哈希值的类型
	 * @return 匹配的字段和值的映射，如果keyword为空则返回空映射
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see #scanOptionsByKeyword(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> scanHashValuesByKeyword(final K key, final String keyword,
																  final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (StringUtils.isBlank(keyword)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = redisOperations.opsForHash();
		ScanOptions scanOptions = scanOptionsByKeyword(keyword, DataType.HASH, null);
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 扫描哈希表的所有字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有字段</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 *     <li>支持大数据量的哈希表扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             哈希表的键
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <HK>            哈希字段的类型
	 * @param <HV>            哈希值的类型
	 * @return 哈希表中的所有字段和值的映射
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> scanHashValues(final K key, final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		HashOperations<K, HK, HV> hashOperations = redisOperations.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, ScanOptions.NONE)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 使用自定义扫描选项扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定每次扫描返回的数量</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 *     <li>使用try-with-resources自动关闭游标</li>
	 * </ul>
	 * </p>
	 *
	 * @param key             哈希表的键
	 * @param scanOptions     扫描选项，可以指定匹配模式和返回数量
	 * @param redisOperations Redis操作对象
	 * @param <K>             键的类型
	 * @param <HK>            哈希字段的类型
	 * @param <HV>            哈希值的类型
	 * @return 匹配的字段和值的映射，如果scanOptions为null则返回空映射
	 * @throws IllegalArgumentException 当key或redisOperations为null时抛出
	 * @see ScanOptions
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> scanHashValues(final K key, final ScanOptions scanOptions,
														 final RedisOperations<K, ?> redisOperations) {
		Assert.notNull(redisOperations, "redisOperations 不可为null");
		Assert.notNull(key, "key 不可为null");

		if (Objects.isNull(scanOptions)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = redisOperations.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 创建扫描选项
	 * <p>
	 * 构建Redis SCAN命令的扫描选项，支持以下功能：
	 * <ul>
	 *     <li>指定匹配模式（pattern）：支持通配符 *、?</li>
	 *     <li>指定数据类型（type）：STRING、LIST、SET、ZSET、HASH、STREAM</li>
	 *     <li>指定每次扫描返回的数量（count）：建议值为100-1000</li>
	 * </ul>
	 * </p>
	 *
	 * @param pattern  匹配模式，支持Redis通配符
	 * @param dataType 数据类型，用于过滤指定类型的键
	 * @param count    期望每次扫描返回的数量
	 * @return 扫描选项对象
	 * @see ScanOptions.ScanOptionsBuilder
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptions(final String pattern, final DataType dataType, final Long count) {
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

	/**
	 * 创建后缀匹配的扫描选项
	 * <p>
	 * 快捷方法，创建用于后缀匹配的扫描选项。
	 * </p>
	 * <p>
	 * 等同于调用 {@link #scanOptionsBySuffix(String, DataType, Long)} 且 dataType 和 count 为 null。
	 * </p>
	 *
	 * @param suffix 后缀字符串
	 * @return 扫描选项对象，使用 "*suffix" 作为匹配模式
	 * @throws IllegalArgumentException 当suffix为空时抛出
	 * @see #scanOptionsBySuffix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsBySuffix(final String suffix) {
		return scanOptionsBySuffix(suffix, null, null);
	}

	/**
	 * 创建后缀匹配的扫描选项（完整版）
	 * <p>
	 * 创建用于后缀匹配的扫描选项，支持指定数据类型和返回数量。
	 * </p>
	 * <p>
	 * 使用 "*suffix" 作为匹配模式，例如：
	 * <ul>
	 *     <li>suffix="user" 将生成模式 "*user"</li>
	 *     <li>可选指定数据类型进行过滤</li>
	 *     <li>可选指定每次扫描返回的数量</li>
	 * </ul>
	 * </p>
	 *
	 * @param suffix   后缀字符串
	 * @param dataType 数据类型
	 * @param count    期望返回的数量
	 * @return 扫描选项对象
	 * @throws IllegalArgumentException 当suffix为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsBySuffix(final String suffix, final DataType dataType, final Long count) {
		Assert.hasText(suffix, "suffix不可为null");
		return scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + suffix, dataType, count);
	}

	/**
	 * 创建前缀匹配的扫描选项
	 * <p>
	 * 快捷方法，创建用于前缀匹配的扫描选项。</p>
	 * <p>等同于调用 {@link #scanOptionsByPrefix(String, DataType, Long)} 且 dataType 和 count 为 null。
	 * </p>
	 *
	 * @param prefix 前缀字符串
	 * @return 扫描选项对象，使用 "prefix*" 作为匹配模式
	 * @throws IllegalArgumentException 当prefix为空时抛出
	 * @see #scanOptionsByPrefix(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsByPrefix(final String prefix) {
		return scanOptionsByPrefix(prefix, null, null);
	}

	/**
	 * 创建前缀匹配的扫描选项（完整版）
	 * <p>
	 * 创建用于前缀匹配的扫描选项，支持指定数据类型和返回数量。</p>
	 * <p>使用 "prefix*" 作为匹配模式，例如：
	 * <ul>
	 *     <li>prefix="user" 将生成模式 "user*"</li>
	 *     <li>可选指定数据类型进行过滤</li>
	 *     <li>可选指定每次扫描返回的数量</li>
	 * </ul>
	 * </p>
	 *
	 * @param prefix   前缀字符串
	 * @param dataType 数据类型
	 * @param count    期望返回的数量
	 * @return 扫描选项对象
	 * @throws IllegalArgumentException 当prefix为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsByPrefix(final String prefix, final DataType dataType, final Long count) {
		Assert.hasText(prefix, "prefix不可为null");
		return scanOptions(prefix + RedisConstants.CURSOR_PATTERN_SYMBOL, dataType, count);
	}

	/**
	 * 创建关键字匹配的扫描选项
	 * <p>
	 * 快捷方法，创建用于关键字匹配的扫描选项。</p>
	 * <p>等同于调用 {@link #scanOptionsByKeyword(String, DataType, Long)} 且 dataType 和 count 为 null。
	 * </p>
	 *
	 * @param keyword 关键字
	 * @return 扫描选项对象，使用 "*keyword*" 作为匹配模式
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @see #scanOptionsByKeyword(String, DataType, Long)
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsByKeyword(final String keyword) {
		return scanOptionsByKeyword(keyword, null, null);
	}

	/**
	 * 创建关键字匹配的扫描选项（完整版）
	 * <p>
	 * 创建用于关键字匹配的扫描选项，支持指定数据类型和返回数量。</p>
	 * <p>使用 "*keyword*" 作为匹配模式，例如：
	 * <ul>
	 *     <li>keyword="user" 将生成模式 "*user*"</li>
	 *     <li>可选指定数据类型进行过滤</li>
	 *     <li>可选指定每次扫描返回的数量</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword  关键字
	 * @param dataType 数据类型
	 * @param count    期望返回的数量
	 * @return 扫描选项对象
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsByKeyword(final String keyword, final DataType dataType, final Long count) {
		Assert.hasText(keyword, "keyword不可为null");
		return scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + keyword + RedisConstants.CURSOR_PATTERN_SYMBOL,
			dataType, count);
	}
}
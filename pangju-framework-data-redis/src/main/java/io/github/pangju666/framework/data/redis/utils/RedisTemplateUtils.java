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
 * {@link RedisTemplate}工具类
 * <p>
 * 提供了一系列便捷方法来操作Redis数据：
 * <ul>
 *     <li>键操作：模糊查询、精确查询</li>
 *     <li>有序集合操作：按模式查询成员及其分数</li>
 *     <li>集合操作：按模式查询成员</li>
 *     <li>哈希操作：按模式查询字段和值</li>
 * </ul>
 * 所有的模糊查询都支持三种匹配模式：
 * <ul>
 *     <li>左匹配：*keyword</li>
 *     <li>右匹配：keyword*</li>
 *     <li>全匹配：*keyword*</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RedisTemplateUtils {
	protected RedisTemplateUtils() {
	}

	/**
	 * 使用{@link RedisConstants#REDIS_PATH_DELIMITER 分隔符}组合多个键
	 *
	 * @param keys 要组合的键数组
	 * @return 组合后的键字符串
	 */
	public static String computeKey(final String... keys) {
		return StringUtils.join(Arrays.asList(keys), RedisConstants.REDIS_PATH_DELIMITER);
	}

	/**
	 * 查找以指定关键字结尾的键（左模糊匹配）
	 * <p>
	 * 使用模式 "*keyword" 进行匹配，例如：
	 * <ul>
	 *     <li>keyword="test" 将匹配 "prefix:test"、"another:test" 等</li>
	 *     <li>使用SCAN命令进行渐进式扫描，避免阻塞Redis</li>
	 *     <li>结果去重并返回Set集合</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的键集合
	 * @param <K> 键的类型
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K> Set<K> likeLeftKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
		try (Cursor<K> cursor = redisTemplate.scan(likeLeftScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 查找以指定关键字开头的键（右模糊匹配）
	 * <p>
	 * 使用模式 "keyword*" 进行匹配，例如：
	 * <ul>
	 *     <li>keyword="test" 将匹配 "test:suffix"、"test:another" 等</li>
	 *     <li>使用SCAN命令进行渐进式扫描，避免阻塞Redis</li>
	 *     <li>结果去重并返回Set集合</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的键集合
	 * @param <K> 键的类型
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K> Set<K> likeRightKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
		try (Cursor<K> cursor = redisTemplate.scan(likeRightScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 查找包含指定关键字的键（全模糊匹配）
	 * <p>
	 * 使用模式 "*keyword*" 进行匹配，例如：
	 * <ul>
	 *     <li>keyword="test" 将匹配 "prefix:test:suffix"、"another:test:other" 等</li>
	 *     <li>使用SCAN命令进行渐进式扫描，避免阻塞Redis</li>
	 *     <li>结果去重并返回Set集合</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的键集合
	 * @param <K> 键的类型
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K> Set<K> likeKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
		try (Cursor<K> cursor = redisTemplate.scan(likeScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 获取Redis中的所有键
	 * <p>
	 * 使用SCAN命令进行渐进式扫描，而不是KEYS命令，以避免在大数据量时阻塞Redis：
	 * <ul>
	 *     <li>使用空的扫描选项，不进行任何过滤</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 *     <li>结果去重并返回Set集合</li>
	 * </ul>
	 * </p>
	 *
	 * @param redisTemplate Redis操作模板
	 * @return 所有键的集合
	 * @param <K> 键的类型
	 * @since 1.0.0
	 */
	public static <K> Set<K> keys(final RedisTemplate<K, ?> redisTemplate) {
		try (Cursor<K> cursor = redisTemplate.scan(emptyScanOptions())) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 使用自定义扫描选项获取Redis中的键
	 * <p>
	 * 允许通过ScanOptions指定扫描的参数：
	 * <ul>
	 *     <li>可以设置匹配模式（pattern）</li>
	 *     <li>可以设置期望返回的数量（count）</li>
	 *     <li>可以按数据类型过滤（type）</li>
	 * </ul>
	 * </p>
	 *
	 * @param scanOptions 自定义的扫描选项
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的键集合
	 * @param <K> 键的类型
	 * @throws IllegalArgumentException 当scanOptions为null时抛出
	 * @since 1.0.0
	 */
	public static <K> Set<K> keys(final ScanOptions scanOptions, final RedisTemplate<K, ?> redisTemplate) {
		try (Cursor<K> cursor = redisTemplate.scan(scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 查找有序集合中以指定关键字结尾的成员（左模糊匹配）
	 * <p>
	 * 使用ZSCAN命令对指定的有序集合进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "*keyword" 进行匹配</li>
	 *     <li>返回匹配的成员及其分数</li>
	 *     <li>结果被封装为{@link ZSetValue}对象列表</li>
	 *     <li>保持结果的顺序（不同于Set接口）</li>
	 * </ul>
	 * </p>
	 *
	 * @param key           有序集合的键
	 * @param keyword       关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @param <K>           键的类型
	 * @param <V>           值的类型
	 * @return 匹配的成员值和分数列表
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, V> List<ZSetValue<V>> likeLeftZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisTemplate.opsForZSet().scan(key, likeLeftScanOptions(keyword))) {
			return cursor.stream()
				.map(ZSetValue::of)
				.toList();
		}
	}

	/**
	 * 查找有序集合中以指定关键字开头的成员（右模糊匹配）
	 * <p>
	 * 使用ZSCAN命令对指定的有序集合进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "keyword*" 进行匹配</li>
	 *     <li>返回匹配的成员及其分数</li>
	 *     <li>结果被封装为{@link ZSetValue}对象列表</li>
	 *     <li>保持结果的顺序（不同于Set接口）</li>
	 * </ul>
	 * </p>
	 *
	 * @param key           有序集合的键
	 * @param keyword       关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @param <K>           键的类型
	 * @param <V>           值的类型
	 * @return 匹配的成员值和分数列表
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, V> List<ZSetValue<V>> likeRightZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisTemplate.opsForZSet().scan(key, likeRightScanOptions(keyword))) {
			return cursor.stream()
				.map(ZSetValue::of)
				.toList();
		}
	}

	/**
	 * 查找有序集合中包含指定关键字的成员（全模糊匹配）
	 * <p>
	 * 使用ZSCAN命令对指定的有序集合进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "*keyword*" 进行匹配</li>
	 *     <li>返回匹配的成员及其分数</li>
	 *     <li>结果被封装为{@link ZSetValue}对象列表</li>
	 *     <li>保持结果的顺序（不同于Set接口）</li>
	 * </ul>
	 * </p>
	 *
	 * @param key           有序集合的键
	 * @param keyword       关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @param <K>           键的类型
	 * @param <V>           值的类型
	 * @return 匹配的成员值和分数列表
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, V> List<ZSetValue<V>> likeZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisTemplate.opsForZSet().scan(key, likeScanOptions(keyword))) {
			return cursor.stream()
				.map(ZSetValue::of)
				.toList();
		}
	}

	/**
	 * 获取有序集合中的所有成员及其分数
	 * <p>
	 * 使用ZSCAN命令对指定的有序集合进行渐进式扫描：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有成员</li>
	 *     <li>结果被封装为{@link ZSetValue}对象列表</li>
	 *     <li>保持结果的顺序（不同于Set接口）</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 有序集合的键
	 * @param redisTemplate Redis操作模板
	 * @return 所有成员值和分数的列表
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public static <K, V> List<ZSetValue<V>> zSetValues(final K key, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisTemplate.opsForZSet().scan(key, emptyScanOptions())) {
			return cursor.stream()
				.map(ZSetValue::of)
				.toList();
		}
	}

	/**
	 * 使用自定义扫描选项获取有序集合中的成员及其分数
	 * <p>
	 * 允许通过ScanOptions指定扫描的参数：
	 * <ul>
	 *     <li>可以设置匹配模式（pattern）</li>
	 *     <li>可以设置期望返回的数量（count）</li>
	 *     <li>结果被封装为{@link ZSetValue}对象列表</li>
	 *     <li>保持结果的顺序（不同于Set接口）</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 有序集合的键
	 * @param scanOptions 自定义的扫描选项
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的成员值和分数列表
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null或scanOptions为null时抛出
	 * @since 1.0.0
	 */
	public static <K, V> List<ZSetValue<V>> zSetValues(final K key, final ScanOptions scanOptions, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = redisTemplate.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.toList();
		}
	}

	/**
	 * 查找集合中以指定关键字结尾的成员（左模糊匹配）
	 * <p>
	 * 使用SSCAN命令对指定的集合进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "*keyword" 进行匹配</li>
	 *     <li>返回匹配的成员集合</li>
	 *     <li>结果自动去重</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的成员集合
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> likeLeftSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<V> cursor = redisTemplate.opsForSet().scan(key, likeLeftScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 查找集合中以指定关键字开头的成员（右模糊匹配）
	 * <p>
	 * 使用SSCAN命令对指定的集合进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "keyword*" 进行匹配</li>
	 *     <li>返回匹配的成员集合</li>
	 *     <li>结果自动去重</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的成员集合
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> likeRightSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<V> cursor = redisTemplate.opsForSet().scan(key, likeRightScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 查找集合中包含指定关键字的成员（全模糊匹配）
	 * <p>
	 * 使用SSCAN命令对指定的集合进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "*keyword*" 进行匹配</li>
	 *     <li>返回匹配的成员集合</li>
	 *     <li>结果自动去重</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的成员集合
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> likeSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<V> cursor = redisTemplate.opsForSet().scan(key, likeScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 获取集合中的所有成员
	 * <p>
	 * 使用SSCAN命令对指定的集合进行渐进式扫描：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有成员</li>
	 *     <li>结果自动去重</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param redisTemplate Redis操作模板
	 * @return 所有成员的集合
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> setValues(final K key, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<V> cursor = redisTemplate.opsForSet().scan(key, emptyScanOptions())) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 使用自定义扫描选项获取集合中的成员
	 * <p>
	 * 允许通过ScanOptions指定扫描的参数：
	 * <ul>
	 *     <li>可以设置匹配模式（pattern）</li>
	 *     <li>可以设置期望返回的数量（count）</li>
	 *     <li>结果自动去重</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param scanOptions 自定义的扫描选项
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的成员集合
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @throws IllegalArgumentException 当key为null或scanOptions为null时抛出
	 * @since 1.0.0
	 */
	public static <K, V> Set<V> setValues(final K key, final ScanOptions scanOptions, final RedisTemplate<K, V> redisTemplate) {
		try (Cursor<V> cursor = redisTemplate.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 查找哈希表中以指定关键字结尾的字段（左模糊匹配）
	 * <p>
	 * 使用HSCAN命令对指定的哈希表进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "*keyword" 进行匹配</li>
	 *     <li>返回匹配的字段和值的映射</li>
	 *     <li>如果字段重复，保留最后一个值</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的字段和值的映射
	 * @param <K> 键的类型
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> likeLeftHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
		HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, likeLeftScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 查找哈希表中以指定关键字开头的字段（右模糊匹配）
	 * <p>
	 * 使用HSCAN命令对指定的哈希表进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "keyword*" 进行匹配</li>
	 *     <li>返回匹配的字段和值的映射</li>
	 *     <li>如果字段重复，保留最后一个值</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的字段和值的映射
	 * @param <K> 键的类型
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> likeRightHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
		HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, likeRightScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 查找哈希表中包含指定关键字的字段（全模糊匹配）
	 * <p>
	 * 使用HSCAN命令对指定的哈希表进行渐进式扫描：
	 * <ul>
	 *     <li>使用模式 "*keyword*" 进行匹配</li>
	 *     <li>返回匹配的字段和值的映射</li>
	 *     <li>如果字段重复，保留最后一个值</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param keyword 关键字，不能为空
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的字段和值的映射
	 * @param <K> 键的类型
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @throws IllegalArgumentException 当key为null或keyword为空时抛出
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> likeHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
		HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, likeScanOptions(keyword))) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 获取哈希表中的所有字段和值
	 * <p>
	 * 使用HSCAN命令对指定的哈希表进行渐进式扫描：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有字段和值</li>
	 *     <li>如果字段重复，保留最后一个值</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param redisTemplate Redis操作模板
	 * @return 所有字段和值的映射
	 * @param <K> 键的类型
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> hashValues(final K key, final RedisTemplate<K, ?> redisTemplate) {
		HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, emptyScanOptions())) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 使用自定义扫描选项获取哈希表中的字段和值
	 * <p>
	 * 允许通过ScanOptions指定扫描的参数：
	 * <ul>
	 *     <li>可以设置匹配模式（pattern）</li>
	 *     <li>可以设置期望返回的数量（count）</li>
	 *     <li>如果字段重复，保留最后一个值</li>
	 *     <li>自动处理游标的迭代和关闭</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param scanOptions 自定义的扫描选项
	 * @param redisTemplate Redis操作模板
	 * @return 匹配的字段和值的映射
	 * @param <K> 键的类型
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @throws IllegalArgumentException 当key为null或scanOptions为null时抛出
	 * @since 1.0.0
	 */
	public static <K, HK, HV> Map<HK, HV> hashValues(final K key, final ScanOptions scanOptions, final RedisTemplate<K, ?> redisTemplate) {
		HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 创建左模糊匹配的扫描选项
	 * <p>
	 * 生成用于以指定关键字结尾的模式匹配：
	 * <ul>
	 *     <li>生成模式 "*keyword"</li>
	 *     <li>不指定数据类型过滤</li>
	 *     <li>不指定返回数量限制</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @return 扫描选项
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions likeLeftScanOptions(final String keyword) {
		Assert.hasText(keyword, "keyword不可为空");
		return scanOptions("*" + keyword, null, null);
	}

	/**
	 * 创建带数据类型和数量限制的左模糊匹配扫描选项
	 * <p>
	 * 生成用于以指定关键字结尾的模式匹配：
	 * <ul>
	 *     <li>生成模式 "*keyword"</li>
	 *     <li>可以指定数据类型进行过滤</li>
	 *     <li>可以指定每次扫描返回的数量</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @param dataType 数据类型过滤，可以为null
	 * @param count 期望返回的数量，可以为null
	 * @return 扫描选项
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions likeLeftScanOptions(final String keyword, final DataType dataType, final Long count) {
		Assert.hasText(keyword, "keyword不可为空");
		return scanOptions("*" + keyword, dataType, count);
	}

	/**
	 * 创建右模糊匹配的扫描选项
	 * <p>
	 * 生成用于以指定关键字开头的模式匹配：
	 * <ul>
	 *     <li>生成模式 "keyword*"</li>
	 *     <li>不指定数据类型过滤</li>
	 *     <li>不指定返回数量限制</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @return 扫描选项
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions likeRightScanOptions(final String keyword) {
		Assert.hasText(keyword, "keyword不可为空");
		return scanOptions(keyword + "*", null, null);
	}

	/**
	 * 创建带数据类型和数量限制的右模糊匹配扫描选项
	 * <p>
	 * 生成用于以指定关键字开头的模式匹配：
	 * <ul>
	 *     <li>生成模式 "keyword*"</li>
	 *     <li>可以指定数据类型进行过滤</li>
	 *     <li>可以指定每次扫描返回的数量</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @param dataType 数据类型过滤，可以为null
	 * @param count 期望返回的数量，可以为null
	 * @return 扫描选项
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions likeRightScanOptions(final String keyword, final DataType dataType, final Long count) {
		Assert.hasText(keyword, "keyword不可为空");
		return scanOptions(keyword + "*", dataType, count);
	}

	/**
	 * 创建全模糊匹配的扫描选项
	 * <p>
	 * 生成用于包含指定关键字的模式匹配：
	 * <ul>
	 *     <li>生成模式 "*keyword*"</li>
	 *     <li>不指定数据类型过滤</li>
	 *     <li>不指定返回数量限制</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @return 扫描选项
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions likeScanOptions(final String keyword) {
		Assert.hasText(keyword, "keyword不可为空");
		return scanOptions("*" + keyword + "*", null, null);
	}

	/**
	 * 创建带数据类型和数量限制的全模糊匹配扫描选项
	 * <p>
	 * 生成用于包含指定关键字的模式匹配：
	 * <ul>
	 *     <li>生成模式 "*keyword*"</li>
	 *     <li>可以指定数据类型进行过滤</li>
	 *     <li>可以指定每次扫描返回的数量</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字，不能为空
	 * @param dataType 数据类型过滤，可以为null
	 * @param count 期望返回的数量，可以为null
	 * @return 扫描选项
	 * @throws IllegalArgumentException 当keyword为空时抛出
	 * @since 1.0.0
	 */
	public static ScanOptions likeScanOptions(final String keyword, final DataType dataType, final Long count) {
		Assert.hasText(keyword, "keyword不可为空");
		return scanOptions("*" + keyword + "*", dataType, count);
	}

	/**
	 * 创建空的扫描选项
	 * <p>
	 * 生成不带任何过滤条件的扫描选项：
	 * <ul>
	 *     <li>不使用匹配模式</li>
	 *     <li>不指定数据类型过滤</li>
	 *     <li>不指定返回数量限制</li>
	 *     <li>用于获取所有元素的场景</li>
	 * </ul>
	 * </p>
	 *
	 * @return 空的扫描选项
	 * @since 1.0.0
	 */
	public static ScanOptions emptyScanOptions() {
		return scanOptions(null, null, null);
	}

	/**
	 * 创建自定义的扫描选项
	 * <p>
	 * 根据提供的参数构建扫描选项：
	 * <ul>
	 *     <li>可以指定匹配模式（pattern）进行过滤</li>
	 *     <li>可以指定数据类型（type）进行过滤</li>
	 *     <li>可以指定每次扫描返回的数量（count）</li>
	 *     <li>所有参数都是可选的，为null时不会应用相应的过滤条件</li>
	 * </ul>
	 * </p>
	 *
	 * @param pattern 匹配模式，可以为null
	 * @param dataType 数据类型过滤，可以为null
	 * @param count 期望返回的数量，可以为null
	 * @return 自定义的扫描选项
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
}
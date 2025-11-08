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

package io.github.pangju666.framework.data.redis.core;

import io.github.pangju666.framework.data.redis.lang.RedisConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

/**
 * 字符串版扫描模板。
 *
 * <p>在 {@link ScanRedisTemplate} 基础上，预设键、值、哈希键和哈希值的序列化器为
 * {@link RedisSerializer#string()}，确保所有基于模式的扫描路径均与字符串序列化兼容。
 * 提供对 ZSet/Set 元素的按后缀、前缀、关键字的便捷扫描方法。</p>
 *
 * <p>行为特性与约束：</p>
 * <ul>
 *   <li>空或空白的匹配参数直接返回空结果，避免不必要的扫描。</li>
 *   <li>匹配模式由服务器端过滤；采用字符串序列化器可避免在 Set/ZSet 元素扫描时因模式匹配引发的 {@link UnsupportedOperationException}。</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 * @see ScanRedisTemplate
 */
public class StringScanRedisTemplate extends ScanRedisTemplate<String> {
	/**
	 * 无参构造。
	 *
	 * @since 1.0.0
	 */
	public StringScanRedisTemplate() {
		super();
		setValueSerializer(RedisSerializer.string());
		setHashValueSerializer(RedisSerializer.string());
	}

	/**
	 * 使用给定连接工厂构造，并初始化字符串序列化器。
	 *
	 * @param connectionFactory Redis 连接工厂；不可为 {@code null}
	 * @since 1.0.0
	 */
	public StringScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}

	/**
	 * 按后缀扫描 ZSet 的元素。
	 *
	 * <p>匹配模式：{@code *suffix}</p>
	 *
	 * @param key    ZSet 的键；不可为空或空白
	 * @param suffix 元素后缀；为空或空白时返回空集合
	 * @return 有序的元素集合；无匹配或后缀为空白时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<String>> scanZSetBySuffix(String key, String suffix) {
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySortedSet();
		}
		ScanOptions scanOptions = scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + suffix, null, null);
		return scanZSet(key, scanOptions);
	}

	/**
	 * 按前缀扫描 ZSet 的元素。
	 *
	 * <p>匹配模式：{@code prefix*}</p>
	 *
	 * @param key    ZSet 的键；不可为空或空白
	 * @param prefix 元素前缀；为空或空白时返回空集合
	 * @return 有序的元素集合；无匹配或前缀为空白时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<String>> scanZSetByPrefix(String key, String prefix) {
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySortedSet();
		}
		ScanOptions scanOptions = scanOptions(prefix + RedisConstants.CURSOR_PATTERN_SYMBOL, null, null);
		return scanZSet(key, scanOptions);
	}

	/**
	 * 按关键字扫描 ZSet 的元素（包含该关键字）。
	 *
	 * <p>匹配模式：{@code *keyword*}</p>
	 *
	 * @param key     ZSet 的键；不可为空或空白
	 * @param keyword 关键字；为空或空白时返回空集合
	 * @return 有序的元素集合；无匹配或关键字为空白时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<String>> scanZSetByKeyword(String key, String keyword) {
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySortedSet();
		}
		ScanOptions scanOptions = scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + keyword +
			RedisConstants.CURSOR_PATTERN_SYMBOL, null, null);
		return scanZSet(key, scanOptions);
	}

	/**
	 * 按后缀扫描 Set 的元素。
	 *
	 * <p>匹配模式：{@code *suffix}</p>
	 *
	 * @param key    Set 的键；不可为空或空白
	 * @param suffix 元素后缀；为空或空白时返回空集合
	 * @return 元素集合；无匹配或后缀为空白时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Set<String> scanSetBySuffix(String key, String suffix) {
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}
		ScanOptions scanOptions = scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + suffix, null, null);
		return scanSet(key, scanOptions);
	}

	/**
	 * 按前缀扫描 Set 的元素。
	 *
	 * <p>匹配模式：{@code prefix*}</p>
	 *
	 * @param key    Set 的键；不可为空或空白
	 * @param prefix 元素前缀；为空或空白时返回空集合
	 * @return 元素集合；无匹配或前缀为空白时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Set<String> scanSetByPrefix(String key, String prefix) {
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}
		ScanOptions scanOptions = scanOptions(prefix + RedisConstants.CURSOR_PATTERN_SYMBOL, null, null);
		return scanSet(key, scanOptions);
	}

	/**
	 * 按关键字扫描 Set 的元素（包含该关键字）。
	 *
	 * <p>匹配模式：{@code *keyword*}</p>
	 *
	 * @param key     Set 的键；不可为空或空白
	 * @param keyword 关键字；为空或空白时返回空集合
	 * @return 元素集合；无匹配或关键字为空白时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Set<String> scanSetByKeyword(String key, String keyword) {
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}
		ScanOptions scanOptions = scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + keyword +
			RedisConstants.CURSOR_PATTERN_SYMBOL, null, null);
		return scanSet(key, scanOptions);
	}

	/**
	 * 预处理连接为字符串连接，以提升字符串序列化的兼容性与效率。
	 *
	 * @param connection         原始连接；不可为 {@code null}
	 * @param existingConnection 是否为已存在的连接
	 * @return 包装后的字符串连接
	 * @see org.springframework.data.redis.core.StringRedisTemplate
	 * @since 1.0.0
	 */
	protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
		return new DefaultStringRedisConnection(connection);
	}
}

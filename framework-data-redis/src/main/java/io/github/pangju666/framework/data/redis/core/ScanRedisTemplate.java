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

import io.github.pangju666.framework.data.redis.lang.RedisConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 扫描增强版 RedisTemplate。
 *
 * <p>用途：围绕 Redis {@code SCAN} 命令提供简洁的扫描 API，聚合游标结果为集合或映射。</p>
 *
 * <p>行为特性：</p>
 * <ul>
 *   <li>采用渐进式迭代（{@link Cursor}），方法结束后自动关闭游标。</li>
 *   <li>匹配模式由服务器端过滤；ZSet 扫描结果按默认比较排序并返回 {@link SortedSet}。</li>
 * </ul>
 *
 * <p>匹配模式的序列化器要求：</p>
 * <ul>
 *   <li>键扫描：本类固定键序列化器为字符串（不可更改），始终支持模式匹配。</li>
 *   <li>Hash 扫描：本类固定哈希键序列化器为字符串（不可更改），只支持对哈希字段名（hash key/field）进行模式匹配。</li>
 *   <li>Set/ZSet 扫描：当设置匹配模式时，当前值序列化器必须支持 {@link String} 序列化，否则抛出 {@link UnsupportedOperationException}。</li>
 * </ul>
 *
 * @param <V> 值类型
 * @author pangju666
 * @since 1.0.0
 * @see RedisTemplate
 */
public class ScanRedisTemplate<V> extends RedisTemplate<String, V> {
	/**
	 * 无参构造。
	 *
	 * @since 1.0.0
	 */
	public ScanRedisTemplate() {
		super();
		super.setKeySerializer(RedisSerializer.string());
		super.setHashKeySerializer(RedisSerializer.string());
	}

	/**
	 * 复制给定 {@link RedisTemplate} 的配置（序列化器、连接工厂）。
	 *
	 * @param redisTemplate 现有模板实例
	 * @since 1.0.0
	 */
	public ScanRedisTemplate(RedisTemplate<?, V> redisTemplate) {
		this();
		setValueSerializer(redisTemplate.getValueSerializer());
		setHashValueSerializer(redisTemplate.getHashValueSerializer());
		setConnectionFactory(redisTemplate.getConnectionFactory());
	}

	@Override
	public void setKeySerializer(RedisSerializer<?> serializer) {
		// 保持键序列化器为 String，避免被外部修改。
	}

	@Override
	public void setHashKeySerializer(RedisSerializer<?> serializer) {
		// 保持哈希键序列化器为 String，避免被外部修改。
	}

	/**
	 * 按类型扫描所有键。
	 *
	 * <p>当 {@code dataType} 为 {@code null} 时不设置类型过滤</p>
	 * <p>该方法等价于调用：{@code scanKeys(scanOptions(null, dataType, null))}。</p>
	 *
	 * @param dataType 键的数据类型（如 {@link DataType#STRING}、{@link DataType#SET} 等）；为 {@code null} 时不设置类型过滤
	 * @return 匹配类型的键集合；无匹配时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeys(DataType dataType) {
		return scanKeys(scanOptions(null, ObjectUtils.getIfNull(dataType,
			DataType.NONE), null));
	}

	/**
	 * 使用默认扫描选项扫描所有键。
	 *
	 * @return 键集合；无匹配时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeys() {
		return scanKeys(ScanOptions.NONE);
	}

	/**
	 * 使用指定扫描选项扫描键。
	 *
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @return 键集合；无匹配时为空集合
	 * @throws IllegalArgumentException 当 {@code scanOptions} 为 {@code null}
	 * @since 1.0.0
	 */
	public Set<String> scanKeys(ScanOptions scanOptions) {
		Assert.notNull(scanOptions, "scanOptions 不可为null");

		try (Cursor<String> cursor = super.scan(scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按后缀扫描所有键。
	 *
	 * <p>匹配模式：{@code *suffix}</p>
	 *
	 * @param suffix 键后缀；为空或空白时返回空集合
	 * @return 键集合；无匹配或后缀为空白时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeysBySuffix(String suffix) {
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + suffix, null, null));
	}

	/**
	 * 按后缀扫描键并按类型过滤。
	 *
	 * <p>匹配模式：{@code *suffix}</p>
	 * <p>当 {@code dataType} 为 {@code null} 时不设置类型过滤</p>
	 * <p>该方法等价于调用：{@code scanKeys(scanOptions("*" + suffix, dataType, null))}。</p>
	 *
	 * @param suffix   键后缀；为空或空白时返回空集合
	 * @param dataType 键的数据类型过滤；为 {@code null} 时不设置类型过滤
	 * @return 键集合；无匹配或后缀为空白时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeysBySuffix(String suffix, DataType dataType) {
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + suffix,
			ObjectUtils.getIfNull(dataType, DataType.NONE), null));
	}

	/**
	 * 按前缀扫描所有键。
	 *
	 * <p>匹配模式：{@code prefix*}</p>
	 *
	 * @param prefix 键前缀；为空或空白时返回空集合
	 * @return 键集合；无匹配或前缀为空白时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeysByPrefix(String prefix) {
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(prefix + RedisConstants.CURSOR_PATTERN_SYMBOL, null, null));
	}

	/**
	 * 按前缀扫描键并按类型过滤。
	 *
	 * <p>匹配模式：{@code prefix*}</p>
	 * <p>当 {@code dataType} 为 {@code null} 时不设置类型过滤</p>
	 * <p>该方法等价于调用：{@code scanKeys(scanOptions(prefix + "*", dataType, null))}。</p>
	 *
	 * @param prefix   键前缀；为空或空白时返回空集合
	 * @param dataType 键的数据类型过滤；为 {@code null} 时不设置类型过滤
	 * @return 键集合；无匹配或前缀为空白时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeysByPrefix(String prefix, DataType dataType) {
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(prefix + RedisConstants.CURSOR_PATTERN_SYMBOL,
			ObjectUtils.getIfNull(dataType, DataType.NONE), null));
	}

	/**
	 * 按关键字扫描所有键（包含该关键字）。
	 *
	 * <p>匹配模式：{@code *keyword*}</p>
	 *
	 * @param keyword 关键字；为空或空白时返回空集合
	 * @return 键集合；无匹配或关键字为空白时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeysByKeyword(String keyword) {
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + keyword +
			RedisConstants.CURSOR_PATTERN_SYMBOL, null, null));
	}

	/**
	 * 按关键字扫描键并按类型过滤。
	 *
	 * <p>匹配模式：{@code *keyword*}</p>
	 * <p>当 {@code dataType} 为 {@code null} 时不设置类型过滤</p>
	 * <p>该方法等价于调用：{@code scanKeys(scanOptions("*" + keyword + "*", dataType, null))}。</p>
	 *
	 * @param keyword  关键字；为空或空白时返回空集合
	 * @param dataType 键的数据类型过滤；为 {@code null} 时不设置类型过滤
	 * @return 键集合；无匹配或关键字为空白时为空集合
	 * @since 1.0.0
	 */
	public Set<String> scanKeysByKeyword(String keyword, DataType dataType) {
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}
		return scanKeys(scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + keyword +
			RedisConstants.CURSOR_PATTERN_SYMBOL, ObjectUtils.getIfNull(dataType,
			DataType.NONE), null));
	}

	/**
	 * 扫描 ZSet 的元素（默认选项）。
	 *
	 * @param key ZSet 的键；不可为空或空白
	 * @return 有序的元素集合（按默认比较规则排序）；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public SortedSet<ZSetOperations.TypedTuple<V>> scanZSet(String key) {
		return scanZSet(key, ScanOptions.NONE);
	}

	/**
	 * 扫描 ZSet 的元素（指定扫描选项）。
	 *
	 * <p>匹配模式由服务器端过滤。</p>
	 *
	 * @param key         ZSet 的键；不可为空或空白
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @return 有序的元素集合；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code scanOptions} 为 {@code null}
	 * @throws UnsupportedOperationException 当提供模式且 value 序列化器不支持 {@code String} 序列化
	 * @since 1.0.0
     */
	public SortedSet<ZSetOperations.TypedTuple<V>> scanZSet(String key, ScanOptions scanOptions) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(scanOptions, "scanOptions 不可为null");
		if (StringUtils.isNotBlank(scanOptions.getPattern()) && !getValueSerializer().canSerialize(java.lang.String.class)) {
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
	 * @param key Set 的键；不可为空或空白
	 * @return 元素集合；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Set<V> scanSet(String key) {
		return scanSet(key, ScanOptions.NONE);
	}

	/**
	 * 扫描 Set 的元素（指定扫描选项）。
	 *
	 * <p>匹配模式由服务器端过滤。</p>
	 *
	 * @param key         Set 的键；不可为空或空白
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @return 元素集合；无元素时为空集合
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code scanOptions} 为 {@code null}
	 * @throws UnsupportedOperationException 当提供模式且 value 序列化器不支持 {@code String} 序列化
     * @since 1.0.0
     */
	public Set<V> scanSet(String key, ScanOptions scanOptions) {
		Assert.hasText(key, "key 不可为空");
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
	 * @param key Hash 的键；不可为空或空白
	 * @param <HV> 哈希值类型
	 * @return 键值映射；无元素时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public <HV> Map<String, HV> scanHash(String key) {
		return scanHash(key, ScanOptions.NONE);
	}

	/**
	 * 扫描 Hash 的键值对（指定扫描选项）。
	 *
	 * <p>匹配模式由服务器端过滤，且仅作用于哈希字段名（hash key/field）。</p>
	 *
	 * @param key         Hash 的键；不可为空或空白
	 * @param scanOptions 扫描选项；不可为 {@code null}
	 * @param <HV> 哈希值类型
	 * @return 键值映射；无元素时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code scanOptions} 为 {@code null}
     * @since 1.0.0
     */
	public <HV> Map<String, HV> scanHash(String key, ScanOptions scanOptions) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(scanOptions, "scanOptions 不可为null");

		HashOperations<String, String, HV> hashOperations = super.opsForHash();
		try (Cursor<Map.Entry<String, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 按后缀扫描 Hash 的键值对（按哈希字段名过滤）。
	 *
	 * <p>匹配模式：{@code *suffix}，仅作用于哈希字段名。</p>
	 *
	 * @param key    Hash 的键；不可为空或空白
	 * @param suffix 哈希字段后缀；为空或空白时返回空映射
	 * @return 键值映射；无匹配或后缀为空白时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Map<String, String> scanHashBySuffix(String key, String suffix) {
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptyMap();
		}
		ScanOptions scanOptions = scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + suffix, null, null);
		return scanHash(key, scanOptions);
	}

	/**
	 * 按前缀扫描 Hash 的键值对（按哈希字段名过滤）。
	 *
	 * <p>匹配模式：{@code prefix*}，仅作用于哈希字段名。</p>
	 *
	 * @param key    Hash 的键；不可为空或空白
	 * @param prefix 哈希字段前缀；为空或空白时返回空映射
	 * @return 键值映射；无匹配或前缀为空白时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Map<String, String> scanHashByPrefix(String key, String prefix) {
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptyMap();
		}
		ScanOptions scanOptions = scanOptions(prefix + RedisConstants.CURSOR_PATTERN_SYMBOL, null, null);
		return scanHash(key, scanOptions);
	}

	/**
	 * 按关键字扫描 Hash 的键值对（按哈希字段名过滤）。
	 *
	 * <p>匹配模式：{@code *keyword*}，仅作用于哈希字段名。</p>
	 *
	 * @param key     Hash 的键；不可为空或空白
	 * @param keyword 关键字（字段名包含该关键字）；为空或空白时返回空映射
	 * @return 键值映射；无匹配或关键字为空白时为空映射
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白
	 * @since 1.0.0
	 */
	public Map<String, String> scanHashByKeyword(String key, String keyword) {
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptyMap();
		}
		ScanOptions scanOptions = scanOptions(RedisConstants.CURSOR_PATTERN_SYMBOL + keyword +
			RedisConstants.CURSOR_PATTERN_SYMBOL, null, null);
		return scanHash(key, scanOptions);
	}

	/**
	 * 构建扫描选项。
	 *
	 * @param pattern  键匹配模式（例如 {@code user:*}）；为空或空白时不设置匹配
	 * @param dataType 过滤键的数据类型；为 {@code null} 时不设置类型过滤
	 * @param count    每次迭代建议返回的数量；为 {@code null} 时不设置数量建议
	 * @return 构建完成的扫描选项
	 * @since 1.0.0
	 * @apiNote {@code count}是一个性能调优选项，用于建议每次迭代返回的元素数量。但它不是精确控制，而是一个提示
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
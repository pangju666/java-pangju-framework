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

package io.github.pangju666.framework.data.redis.bean;

import io.github.pangju666.framework.data.redis.model.ZSetValue;
import io.github.pangju666.framework.data.redis.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis渐进式扫描操作模板类
 * <p>
 * 该类扩展了{@link RedisTemplate}，专注于提供基于SCAN命令族的高效数据扫描操作。
 * 主要功能：
 * <ul>
 *     <li>键空间扫描：使用SCAN命令</li>
 *     <li>有序集合扫描：使用ZSCAN命令</li>
 *     <li>集合扫描：使用SSCAN命令</li>
 *     <li>哈希表扫描：使用HSCAN命令</li>
 * </ul>
 * </p>
 *
 * <p>
 * 序列化要求：
 * <ul>
 *     <li>键匹配模式扫描：要求键序列化器为{@link StringRedisSerializer}</li>
 *     <li>值匹配模式扫描：要求值序列化器为{@link StringRedisSerializer}</li>
 *     <li>哈希匹配模式扫描：要求哈希键序列化器为{@link StringRedisSerializer}</li>
 * </ul>
 * </p>
 *
 * <p>
 * 扫描特性：
 * <ul>
 *     <li>渐进式扫描：避免阻塞操作</li>
 *     <li>自动资源管理：使用try-with-resources处理游标</li>
 *     <li>类型安全：支持泛型</li>
 *     <li>结果处理：自动去重和排序</li>
 * </ul>
 * </p>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @author pangju666
 * @since 1.0.0
 */
public class ScanRedisTemplate<K, V> extends RedisTemplate<K, V> {
	/**
	 * 是否支持键扫描操作
	 *
	 * @since 1.0.0
	 */
	protected boolean supportKeyScan = false;
	/**
	 * 是否支持值扫描操作
	 *
	 * @since 1.0.0
	 */
	protected boolean supportValueScan = false;
	/**
	 * 是否支持哈希表键扫描操作
	 *
	 * @since 1.0.0
	 */
	protected boolean supportHashKeyScan = false;

	/**
	 * 构造一个新的 <code>ScanRedisTemplate</code> 实例。
	 * <p>{@link #setConnectionFactory(RedisConnectionFactory)} 和 {@link #afterPropertiesSet()} 仍需调用。</p>
	 *
	 * @since 1.0.0
	 */
	public ScanRedisTemplate() {
	}

	/**
	 * 构造一个新的 <code>ScanRedisTemplate</code> 实例以备使用。
	 *
	 * @param connectionFactory 用于创建新连接的连接工厂
	 * @since 1.0.0
	 */
	public ScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}

	/**
	 * 设置键序列化器并更新扫描支持状态
	 * <p>
	 * 当键序列化器为{@link StringRedisSerializer}时启用键扫描支持
	 * </p>
	 *
	 * @param serializer 键序列化器
	 * @since 1.0.0
	 */
	@Override
	public void setKeySerializer(RedisSerializer<?> serializer) {
		super.setKeySerializer(serializer);
		if (serializer instanceof StringRedisSerializer) {
			supportKeyScan = true;
		}
	}

	/**
	 * 设置值序列化器并更新扫描支持状态
	 * <p>
	 * 当值序列化器为{@link StringRedisSerializer}时启用值扫描支持。
	 * 此设置影响：
	 * <ul>
	 *     <li>有序集合（ZSet）成员的匹配模式扫描</li>
	 *     <li>集合（Set）成员的匹配模式扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param serializer 值序列化器
	 * @since 1.0.0
	 */
	@Override
	public void setValueSerializer(RedisSerializer<?> serializer) {
		super.setValueSerializer(serializer);
		if (serializer instanceof StringRedisSerializer) {
			supportValueScan = true;
		}
	}

	/**
	 * 设置哈希键序列化器并更新扫描支持状态
	 * <p>
	 * 当哈希键序列化器为{@link StringRedisSerializer}时启用哈希键扫描支持。
	 * 此设置影响：
	 * <ul>
	 *     <li>哈希表字段的匹配模式扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param hashKeySerializer 哈希键序列化器
	 * @since 1.0.0
	 */
	@Override
	public void setHashKeySerializer(RedisSerializer<?> hashKeySerializer) {
		super.setHashKeySerializer(hashKeySerializer);
		if (hashKeySerializer instanceof StringRedisSerializer) {
			supportHashKeyScan = true;
		}
	}

	/**
	 * 使用现有的RedisTemplate构造一个新的ScanRedisTemplate实例
	 * <p>
	 * 此构造方法会复制源RedisTemplate的以下配置：
	 * <ul>
	 *     <li>键序列化器</li>
	 *     <li>值序列化器</li>
	 *     <li>哈希键序列化器</li>
	 *     <li>哈希值序列化器</li>
	 *     <li>Redis连接工厂</li>
	 * </ul>
	 * </p>
	 *
	 * @param redisTemplate 源RedisTemplate实例
	 * @throws IllegalArgumentException 当redisTemplate为null时抛出
	 * @since 1.0.0
	 */
	public ScanRedisTemplate(RedisTemplate<?, ?> redisTemplate) {
		setKeySerializer(redisTemplate.getKeySerializer());
		setValueSerializer(redisTemplate.getValueSerializer());
		setHashKeySerializer(redisTemplate.getHashKeySerializer());
		setHashValueSerializer(redisTemplate.getHashValueSerializer());
		setConnectionFactory(redisTemplate.getConnectionFactory());
		afterPropertiesSet();
	}

	/**
	 * 按后缀扫描键
	 * <p>
	 * 使用SCAN命令渐进式扫描以指定后缀结尾的键：
	 * <ul>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>要求键序列化器为StringRedisSerializer</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param suffix 后缀字符串
	 * @return 匹配的键集合，如果suffix为空则返回空集合
	 * @throws UnsupportedOperationException 当键序列化器不是StringRedisSerializer时抛出
	 * @since 1.0.0
	 */
	public Set<K> scanKeysBySuffix(final String suffix) {
		if (!supportKeyScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}

		try (Cursor<K> cursor = super.scan(RedisUtils.scanOptionsBySuffix(suffix, null, null))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按前缀扫描键
	 * <p>
	 * 使用SCAN命令渐进式扫描以指定前缀开头的键：
	 * <ul>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>要求键序列化器为StringRedisSerializer</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param prefix 前缀字符串
	 * @return 匹配的键集合，如果prefix为空则返回空集合
	 * @throws UnsupportedOperationException 当键序列化器不是StringRedisSerializer时抛出
	 * @since 1.0.0
	 */
	public Set<K> scanKeysByPrefix(final String prefix) {
		if (!supportKeyScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}

		try (Cursor<K> cursor = super.scan(RedisUtils.scanOptionsByPrefix(prefix, null, null))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按关键字扫描键
	 * <p>
	 * 使用SCAN命令渐进式扫描包含指定关键字的键：
	 * <ul>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>要求键序列化器为StringRedisSerializer</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字
	 * @return 匹配的键集合，如果keyword为空则返回空集合
	 * @throws UnsupportedOperationException 当键序列化器不是StringRedisSerializer时抛出
	 * @since 1.0.0
	 */
	public Set<K> scanKeysByKeyword(final String keyword) {
		if (!supportKeyScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}

		try (Cursor<K> cursor = super.scan(RedisUtils.scanOptionsByKeyword(keyword, null, null))) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按数据类型扫描键
	 * <p>
	 * 使用SCAN命令渐进式扫描指定数据类型的键：
	 * <ul>
	 *     <li>支持STRING、LIST、SET、ZSET、HASH等类型</li>
	 *     <li>不使用匹配模式</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param dataType 数据类型
	 * @return 匹配的键集合，如果dataType为null则返回空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeysByDataType(final DataType dataType) {
		if (Objects.isNull(dataType)) {
			return Collections.emptySet();
		}

		try (Cursor<K> cursor = super.scan(RedisUtils.scanOptions(null, dataType, null))) {
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
	 * @return 所有键的集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeys() {
		try (Cursor<K> cursor = super.scan(ScanOptions.NONE)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 使用自定义扫描选项扫描键
	 * <p>
	 * 使用SCAN命令根据自定义选项进行渐进式扫描：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定数据类型</li>
	 *     <li>支持设置返回数量</li>
	 *     <li>使用匹配模式时要求键序列化器为StringRedisSerializer</li>
	 * </ul>
	 * </p>
	 *
	 * @param scanOptions 扫描选项
	 * @return 匹配的键集合，如果scanOptions为null则返回空集合
	 * @throws UnsupportedOperationException 当使用匹配模式且键序列化器不是StringRedisSerializer时抛出
	 * @since 1.0.0
	 */
	public Set<K> scanKeys(final ScanOptions scanOptions) {
		if (Objects.isNull(scanOptions)) {
			return Collections.emptySet();
		}
		if (StringUtils.isNotBlank(scanOptions.getPattern()) && !supportKeyScan) {
			throw new UnsupportedOperationException();
		}

		try (Cursor<K> cursor = super.scan(scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按后缀扫描有序集合成员
	 * <p>
	 * 使用ZSCAN命令渐进式扫描以指定后缀结尾的成员：
	 * <ul>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>要求值序列化器为StringRedisSerializer</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 有序集合的键
	 * @param suffix 后缀字符串
	 * @return 匹配的成员及其分数的有序集合，如果suffix为空则返回空集合
	 * @throws UnsupportedOperationException 当值序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValuesBySuffix(final K key, final String suffix) {
		if (!supportValueScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySortedSet();
		}

		ScanOptions scanOptions = RedisUtils.scanOptionsBySuffix(suffix, null, null);
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = super.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 按前缀扫描有序集合成员
	 * <p>
	 * 使用ZSCAN命令渐进式扫描以指定前缀开头的成员：
	 * <ul>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>要求值序列化器为StringRedisSerializer</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 有序集合的键
	 * @param prefix 前缀字符串
	 * @return 匹配的成员及其分数的有序集合，如果prefix为空则返回空集合
	 * @throws UnsupportedOperationException 当值序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValuesByPrefix(final K key, final String prefix) {
		if (!supportValueScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySortedSet();
		}

		ScanOptions scanOptions = RedisUtils.scanOptionsByPrefix(prefix, null, null);
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = super.opsForZSet().scan(key, scanOptions)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 按关键字扫描有序集合成员
	 * <p>
	 * 使用ZSCAN命令渐进式扫描包含指定关键字的成员：
	 * <ul>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>要求值序列化器为StringRedisSerializer</li>
	 *     <li>返回结果按分数降序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 有序集合的键
	 * @param keyword 关键字
	 * @return 匹配的成员及其分数的有序集合，如果keyword为空则返回空集合
	 * @throws UnsupportedOperationException 当值序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValuesByKeyword(final K key, final String keyword) {
		if (!supportValueScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySortedSet();
		}

		ScanOptions scanOptions = RedisUtils.scanOptionsByKeyword(keyword, null, null);
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = super.opsForZSet().scan(key, scanOptions)) {
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
	 * @param key 有序集合的键
	 * @return 所有成员及其分数的有序集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValues(final K key) {
		try (Cursor<ZSetOperations.TypedTuple<V>> cursor = super.opsForZSet().scan(key, ScanOptions.NONE)) {
			return cursor.stream()
				.map(ZSetValue::of)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/**
	 * 按后缀扫描集合成员
	 * <p>
	 * 使用SSCAN命令渐进式扫描以指定后缀结尾的成员：
	 * <ul>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>要求值序列化器为StringRedisSerializer</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param suffix 后缀字符串
	 * @return 匹配的成员集合，如果suffix为空则返回空集合
	 * @throws UnsupportedOperationException 当值序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValuesBySuffix(final K key, final String suffix) {
		if (!supportValueScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptySet();
		}

		ScanOptions scanOptions = RedisUtils.scanOptionsBySuffix(suffix, null, null);
		try (Cursor<V> cursor = super.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按前缀扫描集合成员
	 * <p>
	 * 使用SSCAN命令渐进式扫描以指定前缀开头的成员：
	 * <ul>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>要求值序列化器为StringRedisSerializer</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param prefix 前缀字符串
	 * @return 匹配的成员集合，如果prefix为空则返回空集合
	 * @throws UnsupportedOperationException 当值序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValuesByPrefix(final K key, final String prefix) {
		if (!supportValueScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptySet();
		}

		ScanOptions scanOptions = RedisUtils.scanOptionsByPrefix(prefix, DataType.SET, null);
		try (Cursor<V> cursor = super.opsForSet().scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按关键字扫描集合成员
	 * <p>
	 * 使用SSCAN命令渐进式扫描包含指定关键字的成员：
	 * <ul>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>要求值序列化器为StringRedisSerializer</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @param keyword 关键字
	 * @return 匹配的成员集合，如果keyword为空则返回空集合
	 * @throws UnsupportedOperationException 当值序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValuesByKeyword(final K key, final String keyword) {
		if (!supportValueScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptySet();
		}

		ScanOptions scanOptions = RedisUtils.scanOptionsByKeyword(keyword, DataType.SET, null);
		try (Cursor<V> cursor = super.opsForSet().scan(key, scanOptions)) {
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
	 * @param key 集合的键
	 * @return 集合中的所有成员
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValues(final K key) {
		try (Cursor<V> cursor = super.opsForSet().scan(key, ScanOptions.NONE)) {
			return cursor.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * 按后缀扫描哈希表字段
	 * <p>
	 * 使用HSCAN命令渐进式扫描以指定后缀结尾的字段：
	 * <ul>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>要求哈希键序列化器为StringRedisSerializer</li>
	 *     <li>返回匹配字段及其对应值的映射</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param suffix 后缀字符串
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @return 匹配的字段和值的映射，如果suffix为空则返回空映射
	 * @throws UnsupportedOperationException 当哈希键序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValuesBySuffix(final K key, final String suffix) {
		if (!supportHashKeyScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(suffix)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = super.opsForHash();
		ScanOptions scanOptions = RedisUtils.scanOptionsBySuffix(suffix, DataType.HASH, null);
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 按前缀扫描哈希表字段
	 * <p>
	 * 使用HSCAN命令渐进式扫描以指定前缀开头的字段：
	 * <ul>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>要求哈希键序列化器为StringRedisSerializer</li>
	 *     <li>返回匹配字段及其对应值的映射</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param prefix 前缀字符串
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @return 匹配的字段和值的映射，如果prefix为空则返回空映射
	 * @throws UnsupportedOperationException 当哈希键序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValuesByPrefix(final K key, final String prefix) {
		if (!supportHashKeyScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(prefix)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = super.opsForHash();
		ScanOptions scanOptions = RedisUtils.scanOptionsByPrefix(prefix, DataType.HASH, null);
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, scanOptions)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	/**
	 * 按关键字扫描哈希表字段
	 * <p>
	 * 使用HSCAN命令渐进式扫描包含指定关键字的字段：
	 * <ul>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>要求哈希键序列化器为StringRedisSerializer</li>
	 *     <li>返回匹配字段及其对应值的映射</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 哈希表的键
	 * @param keyword 关键字
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @return 匹配的字段和值的映射，如果keyword为空则返回空映射
	 * @throws UnsupportedOperationException 当哈希键序列化器不是StringRedisSerializer时抛出
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValuesByKeyword(final K key, final String keyword) {
		if (!supportHashKeyScan) {
			throw new UnsupportedOperationException();
		}
		if (StringUtils.isBlank(keyword)) {
			return Collections.emptyMap();
		}

		HashOperations<K, HK, HV> hashOperations = super.opsForHash();
		ScanOptions scanOptions = RedisUtils.scanOptionsByKeyword(keyword, DataType.HASH, null);
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
	 * @param key  哈希表的键
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @return 哈希表中的所有字段和值的映射
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValues(final K key) {
		HashOperations<K, HK, HV> hashOperations = super.opsForHash();
		try (Cursor<Map.Entry<HK, HV>> cursor = hashOperations.scan(key, ScanOptions.NONE)) {
			return cursor.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}
}
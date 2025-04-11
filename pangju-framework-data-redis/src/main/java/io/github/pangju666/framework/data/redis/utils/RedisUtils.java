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

import io.github.pangju666.framework.data.redis.pool.RedisConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Redis操作工具类
 * <p>
 * 提供Redis键扫描和键名管理的工具方法，主要功能包括：
 * <ul>
 *     <li>键名组合：使用统一分隔符组合多级键名</li>
 *     <li>扫描选项构建：支持多种模式的键扫描</li>
 *     <li>数据类型过滤：支持按Redis数据类型筛选</li>
 * </ul>
 * </p>
 *
 * <p>
 * 扫描模式支持：
 * <ul>
 *     <li>数据类型匹配：按STRING、LIST、SET等类型过滤</li>
 *     <li>前缀匹配：使用 "prefix*" 模式</li>
 *     <li>后缀匹配：使用 "*suffix" 模式</li>
 *     <li>关键字匹配：使用 "*keyword*" 模式</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 组合多级键名
 * String key = RedisUtils.computeKey("user", "profile", "1");  // 结果: user:profile:1
 *
 * // 按数据类型扫描
 * ScanOptions typeOptions = RedisUtils.scanOptionsByDataType(DataType.STRING, 100L);
 *
 * // 按前缀扫描
 * ScanOptions prefixOptions = RedisUtils.scanOptionsByPrefix("user:");
 *
 * // 按关键字扫描
 * ScanOptions keywordOptions = RedisUtils.scanOptionsByKeyword("profile");
 * }</pre>
 * </p>
 *
 * <p>
 * 性能优化：
 * <ul>
 *     <li>使用SCAN命令替代KEYS命令，避免大量键时的性能问题</li>
 *     <li>支持设置每次扫描返回的数量，控制内存使用</li>
 *     <li>提供数据类型过滤，减少不必要的键扫描</li>
 * </ul>
 * </p>
 *
 * <p>
 * 注意事项：
 * <ul>
 *     <li>工具类中的方法都是静态的，无需实例化</li>
 *     <li>键名分隔符统一使用{@link RedisConstants#REDIS_PATH_DELIMITER}</li>
 *     <li>扫描参数为null时会使用默认值</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @version 1.0.0
 * @see org.springframework.data.redis.core.ScanOptions
 * @see org.springframework.data.redis.connection.DataType
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
		return StringUtils.join(keys, RedisConstants.REDIS_PATH_DELIMITER);
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
	 * 创建按数据类型匹配的扫描选项
	 * <p>
	 * 快捷方法，创建仅按数据类型过滤的扫描选项。
	 * </p>
	 * <p>
	 * 等同于调用 {@link #scanOptionsByDataType(DataType, Long)} 且 count 为 null。
	 * </p>
	 *
	 * @param dataType 数据类型
	 * @return 扫描选项对象，仅包含数据类型过滤
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsByDataType(final DataType dataType) {
		return scanOptions(null, dataType, null);
	}

	/**
	 * 创建按数据类型匹配的扫描选项（完整版）
	 * <p>
	 * 创建按数据类型过滤的扫描选项，支持指定返回数量。
	 * </p>
	 * <p>
	 * 使用场景：
	 * <ul>
	 *     <li>需要获取特定类型（如STRING、HASH等）的所有键</li>
	 *     <li>需要控制每次扫描返回的数量以优化性能</li>
	 * </ul>
	 * </p>
	 *
	 * @param dataType 数据类型
	 * @param count    期望返回的数量
	 * @return 扫描选项对象
	 * @since 1.0.0
	 */
	public static ScanOptions scanOptionsByDataType(final DataType dataType, final Long count) {
		return scanOptions(null, dataType, count);
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
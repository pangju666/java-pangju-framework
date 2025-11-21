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

import io.github.pangju666.framework.data.redis.lang.RedisConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Redis 工具类。
 *
 * <p><b>提供能力：</b></p>
 * <ul>
 *   <li>键拼接：通过路径分隔符将多个片段组合为一个 Redis 键（{@link #computeKey(Object...)}）。</li>
 *   <li>批量删除：支持批量删除键，并在未完全删除时进行重试（{@link #deleteKeys(RedisTemplate, Collection)}、
 *   {@link #deleteKeys(RedisTemplate, Collection, int)}）。</li>
 * </ul>
 *
 * <p><b>线程安全：</b>类本身无状态，所有方法为静态方法，可在并发环境下安全调用。</p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RedisUtils {
	/**
	 * 批量删除默认重试次数。
	 * <p>在首轮删除后，若仍存在未删除的键，则最多进行该次数的额外删除尝试。</p>
	 *
	 * @since 1.0.0
	 */
	protected static final int DEFAULT_DELETE_RETRY_TIMES = 3;

	protected RedisUtils() {
	}

	/**
	 * 计算（拼接）Redis 键。
	 *
	 * <p>将多个键片段使用路径分隔符 {@link RedisConstants#REDIS_PATH_DELIMITER} 进行拼接。</p>
	 *
	 * <p>行为说明：每个片段先通过 {@link String#valueOf(Object)} 转为字符串，随后调用
	 * {@link String#strip()} 去除首尾空白字符（空格、制表、换行等）；不会移除中间的分隔符或进行额外规范化。</p>
	 * <p>兼容性：允许片段为 {@code null}，将被转换为字符串 {@code "null"} 再参与拼接。</p>
	 *
	 * @param keys 键的片段（按给定顺序拼接）；不能为空或长度为 0
	 * @return 拼接后的完整键，不为 {@code null}
	 * @throws IllegalArgumentException 当 {@code keys} 为空或长度为 0
	 * @since 1.0.0
	 */
	public static String computeKey(final Object... keys) {
		Assert.notEmpty(keys, "keys 不可为空");

		return String.join(RedisConstants.REDIS_PATH_DELIMITER, Arrays.stream(keys)
			.map(key -> String.valueOf(key).strip())
			.toList());
	}

	/**
	 * 批量删除键（{@link #DEFAULT_DELETE_RETRY_TIMES 使用默认重试次数}）。
	 *
	 * @param redisTemplate RedisTemplate 实例
	 * @param keys          待删除的键集合；为空集合时直接返回
	 * @since 1.0.0
	 */
	public static <K> void deleteKeys(final RedisTemplate<K, ?> redisTemplate, final Collection<K> keys) {
		deleteKeys(redisTemplate, keys, DEFAULT_DELETE_RETRY_TIMES);
	}

	/**
	 * 批量删除键（支持重试）。
	 *
	 * <p>先执行一次批量删除；若未全部删除成功，则在不超过 {@code retryTimes} 次的条件下进行额外删除尝试，
	 * 直到全部删除或达到重试上限（最多额外重试 {@code retryTimes} 次）。</p>
	 *
	 * @param redisTemplate RedisTemplate 实例
	 * @param keys          待删除的键集合；为空集合时直接返回
	 * @param retryTimes    最大额外尝试次数
	 * @throws IllegalArgumentException 当 redisTemplate 为 {@code null} 或 {@code retryTimes} 小于等于 0 时抛出
	 * @since 1.0.0
	 */
	public static <K> void deleteKeys(final RedisTemplate<K, ?> redisTemplate, final Collection<K> keys, final int retryTimes) {
		Assert.isTrue(retryTimes > 0, "retryTimes 必须大于0");
		Assert.notNull(redisTemplate, "redisTemplate 不可为null");

		if (CollectionUtils.isEmpty(keys)) {
			return;
		}
		long deleteCount = ObjectUtils.getIfNull(redisTemplate.delete(keys), 0L);
		if (deleteCount < keys.size()) {
			long count = keys.size() - deleteCount;
			long times = 0;
			while (times < retryTimes && count > 0) {
				++times;
				count -= ObjectUtils.getIfNull(redisTemplate.delete(keys), 0L);
			}
		}
	}
}
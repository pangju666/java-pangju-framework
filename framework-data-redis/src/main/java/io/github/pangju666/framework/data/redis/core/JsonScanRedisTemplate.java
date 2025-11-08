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

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * JSON 序列化版扫描模板。
 *
 * <p>基于 {@link ScanRedisTemplate} 的便捷封装，默认使用
 * 键、哈希键为 {@link RedisSerializer#string()}，值、哈希值为
 * {@link RedisSerializer#json()} 的序列化方案，适合以 JSON 格式存储对象的场景。</p>
 *
 * <p>说明：</p>
 * <ul>
 *   <li>该类仅负责初始化序列化器，扫描能力与行为特性由父类提供。</li>
 *   <li>值/哈希值序列化器非字符串类型，使用匹配模式进行“值扫描”可能不受支持；当提供匹配模式且序列化器不能序列化 {@link String} 时将抛出 {@link UnsupportedOperationException}。如需模式扫描值，推荐使用 {@link StringScanRedisTemplate} 或确保当前序列化器能正确处理字符串模式并与存储格式一致。</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 * @see ScanRedisTemplate
 */
public class JsonScanRedisTemplate extends ScanRedisTemplate<Object> {
	/**
	 * 无参构造，初始化键、哈希键为字符串序列化器，值、哈希值为 JSON 序列化器。
	 *
	 * @since 1.0.0
	 */
	public JsonScanRedisTemplate() {
		super();
		setValueSerializer(RedisSerializer.json());
		setHashValueSerializer(RedisSerializer.json());
	}

	/**
	 * 使用给定连接工厂构造，并初始化为字符串/JSON 序列化器组合。
	 *
	 * @param connectionFactory Redis 连接工厂
	 * @since 1.0.0
	 */
	public JsonScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}
}

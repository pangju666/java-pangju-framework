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
 * 字符串序列化的Redis模板类
 * <p>
 * 扩展自{@link ScanRedisTemplate}，专门用于处理字符串格式数据的序列化和反序列化。
 * 此实现具有以下特点：
 * <ul>
 *     <li>键使用字符串序列化器，保证键的可读性</li>
 *     <li>值使用字符串序列化器，支持直接存取字符串数据</li>
 *     <li>Hash的键使用字符串序列化器，确保字段名的可读性</li>
 *     <li>Hash的值使用字符串序列化器，支持简单文本数据存储</li>
 * </ul>
 * </p>
 *
 * <p>
 * 优点：
 * <ul>
 *     <li>最简单直接的数据存储方式</li>
 *     <li>无需额外的序列化和反序列化转换</li>
 *     <li>存储的数据完全可读，便于管理和调试</li>
 *     <li>适用于所有支持字符串操作的Redis客户端</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>存储简单的文本数据</li>
 *     <li>需要在不同系统间共享基础类型数据</li>
 *     <li>需要直接通过Redis CLI查看或编辑数据</li>
 *     <li>缓存配置信息或系统参数</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Autowired
 * private StringScanRedisTemplate redisTemplate;
 *
 * // 存储字符串
 * redisTemplate.opsForValue().set("user:name", "张三");
 *
 * // 读取字符串
 * String name = redisTemplate.opsForValue().get("user:name");
 *
 * // 存储Hash字段
 * redisTemplate.opsForHash().put("user:1", "name", "张三");
 * redisTemplate.opsForHash().put("user:1", "age", "25");
 *
 * // 读取Hash字段
 * String userName = redisTemplate.opsForHash().get("user:1", "name");
 * }</pre>
 * </p>
 *
 * <p>代码创意来源于{@link org.springframework.data.redis.core.StringRedisTemplate}</p>
 *
 * @author pangju666
 * @version 1.0.0
 * @see ScanRedisTemplate
 * @see RedisSerializer#string()
 * @since 1.0.0
 */
public class StringScanRedisTemplate extends ScanRedisTemplate<String, String> {
	/**
	 * 构造一个新的 <code>StringScanRedisTemplate</code> 实例。
	 *
	 * @since 1.0.0
	 */
	public StringScanRedisTemplate() {
		setKeySerializer(RedisSerializer.string());
		setValueSerializer(RedisSerializer.string());
		setHashKeySerializer(RedisSerializer.string());
		setHashValueSerializer(RedisSerializer.string());
	}

	/**
	 * 构造一个新的 <code>StringScanRedisTemplate</code> 实例以备使用。
	 *
	 * @param connectionFactory 用于创建新连接的连接工厂
	 * @since 1.0.0
	 */
	public StringScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}
}

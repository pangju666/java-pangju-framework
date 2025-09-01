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

package io.github.pangju666.framework.data.redis.bean;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * JSON序列化的Redis模板类
 * <p>
 * 扩展自{@link ScanRedisTemplate}，专门用于处理JSON格式数据的序列化和反序列化。
 * 此实现具有以下特点：
 * <ul>
 *     <li>键使用字符串序列化器，保证键的可读性</li>
 *     <li>值使用JSON序列化器，支持跨语言数据交换</li>
 *     <li>Hash的键使用字符串序列化器，确保字段名的可读性</li>
 *     <li>Hash的值使用JSON序列化器，支持结构化数据存储</li>
 * </ul>
 * </p>
 *
 * <p>
 * 优点：
 * <ul>
 *     <li>数据格式标准化，支持跨语言访问</li>
 *     <li>存储的数据具有可读性，便于调试</li>
 *     <li>较小的存储空间占用</li>
 *     <li>支持动态类型，适合灵活的数据结构</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>需要跨语言访问Redis数据时</li>
 *     <li>数据结构相对简单，不涉及复杂的类型信息</li>
 *     <li>需要直接查看或编辑存储的数据内容</li>
 *     <li>与其他JSON相关的系统集成</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Autowired
 * private JsonScanRedisTemplate redisTemplate;
 *
 * // 存储对象为JSON
 * User user = new User("张三", 25);
 * redisTemplate.opsForValue().set("user:1", user);
 *
 * // 读取JSON为对象
 * User cachedUser = redisTemplate.opsForValue().get("user:1", User.class);
 *
 * // 存储Map为JSON
 * Map<String, Object> data = new HashMap<>();
 * data.put("name", "张三");
 * data.put("age", 25);
 * redisTemplate.opsForValue().set("user:2", data);
 * }</pre>
 * </p>
 *
 * <p>代码创意来源于{@link org.springframework.data.redis.core.StringRedisTemplate}</p>
 *
 * @author pangju666
 * @version 1.0.0
 * @see ScanRedisTemplate
 * @see RedisSerializer#json()
 * @since 1.0.0
 */
public class JsonScanRedisTemplate extends ScanRedisTemplate<String, Object> {
	/**
	 * 构造一个新的 <code>JsonScanRedisTemplate</code> 实例。
	 * <p>{@link #setConnectionFactory(RedisConnectionFactory)} 和 {@link #afterPropertiesSet()} 仍需调用。</p>
	 *
	 * @since 1.0.0
	 */
	public JsonScanRedisTemplate() {
		setKeySerializer(RedisSerializer.string());
		setValueSerializer(RedisSerializer.json());
		setHashKeySerializer(RedisSerializer.string());
		setHashValueSerializer(RedisSerializer.json());
	}

	/**
	 * 构造一个新的 <code>JsonScanRedisTemplate</code> 实例以备使用。
	 *
	 * @param connectionFactory 用于创建新连接的连接工厂
	 * @since 1.0.0
	 */
	public JsonScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}
}

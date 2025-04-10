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
 * Java对象序列化的Redis模板类
 * <p>
 * 扩展自{@link ScanRedisTemplate}，专门用于处理Java对象的序列化和反序列化。
 * 此实现具有以下特点：
 * <ul>
 *     <li>键使用字符串序列化器，保证键的可读性</li>
 *     <li>值使用Java对象序列化器，支持所有可序列化的Java对象</li>
 *     <li>Hash的键使用字符串序列化器，确保字段名的可读性</li>
 *     <li>Hash的值使用Java对象序列化器，支持复杂对象存储</li>
 * </ul>
 * </p>
 *
 * <p>
 * 优点：
 * <ul>
 *     <li>完整保留Java对象的类型信息</li>
 *     <li>支持复杂对象图的序列化</li>
 *     <li>无需额外配置即可使用</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>需要在Redis中存储完整Java对象时</li>
 *     <li>应用内部数据缓存，不需要跨语言访问</li>
 *     <li>对象包含复杂的继承关系或循环引用</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Autowired
 * private JavaRedisTemplate redisTemplate;
 *
 * // 存储Java对象
 * User user = new User("张三", 25);
 * redisTemplate.opsForValue().set("user:1", user);
 *
 * // 读取Java对象
 * User cachedUser = (User) redisTemplate.opsForValue().get("user:1");
 * }</pre>
 * </p>
 *
 * <p>代码创意来源于{@link org.springframework.data.redis.core.StringRedisTemplate}</p>
 *
 * @author pangju666
 * @see ScanRedisTemplate
 * @see java.io.Serializable
 * @since 1.0.0
 */
public class JavaRedisTemplate extends ScanRedisTemplate<String, Object> {
	/**
	 * 构造一个新的 <code>JavaRedisTemplate</code> 实例。
	 * <p>{@link #setConnectionFactory(RedisConnectionFactory)} 和 {@link #afterPropertiesSet()} 仍需调用。</p>
	 *
	 * @since 1.0.0
	 */
	public JavaRedisTemplate() {
		setKeySerializer(RedisSerializer.string());
		setValueSerializer(RedisSerializer.java());
		setHashKeySerializer(RedisSerializer.string());
		setHashValueSerializer(RedisSerializer.java());
	}

	/**
	 * 构造一个新的 <code>JavaRedisTemplate</code> 实例以备使用。
	 *
	 * @param connectionFactory 用于创建新连接的连接工厂
	 * @since 1.0.0
	 */
	public JavaRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}
}

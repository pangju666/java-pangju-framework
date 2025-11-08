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
 * Java 序列化版扫描模板。
 *
 * <p>在 {@link ScanRedisTemplate} 基础上预设序列化方案：键与哈希字段使用
 * {@link RedisSerializer#string()}，值与哈希值使用 {@link RedisSerializer#java()}。
 * 适用于需要以 JDK 序列化存储对象的场景。</p>
 *
 * @author pangju666
 * @since 1.0.0
 * @see ScanRedisTemplate
 */
public class JavaScanRedisTemplate extends ScanRedisTemplate<Object> {
	/**
	 * 无参构造，初始化键、哈希键为字符串序列化器，值、哈希值为 Java 序列化器。
	 *
	 * @since 1.0.0
	 */
	public JavaScanRedisTemplate() {
		super();
		setValueSerializer(RedisSerializer.java());
		setHashValueSerializer(RedisSerializer.java());
	}

	/**
	 * 使用给定连接工厂构造，并初始化为字符串/Java 序列化器组合。
	 *
	 * @param connectionFactory Redis 连接工厂
	 * @since 1.0.0
	 */
	public JavaScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}
}

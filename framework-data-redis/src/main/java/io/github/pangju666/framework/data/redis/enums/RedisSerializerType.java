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

package io.github.pangju666.framework.data.redis.enums;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis 序列化器类型枚举。
 *
 * <p><b>用途：</b>统一声明在 Redis 读写过程中可选的序列化方案，便于根据场景选择。</p>
 *
 * <p><b>类型与特性：</b></p>
 * <ul>
 *   <li>{@link #STRING}：字符串序列化（通常 UTF-8），推荐用于 key 及人类可读的 value。</li>
 *   <li>{@link #JAVA}：JDK 原生序列化，要求对象实现 {@code Serializable}，跨语言/类变更兼容性较弱。</li>
 *   <li>{@link #JSON}：通用 JSON 序列化，结构可读，便于调试与跨语言交互。</li>
 *   <li>{@link #BYTE_ARRAY}：字节透传，不做转换，适用于自定义编码或二进制数据。</li>
 * </ul>
 *
 * <p><b>使用建议：</b></p>
 * <ul>
 *   <li>Key 建议使用 {@link #STRING}；Value 依据可读性、兼容性与性能在 {@link #JSON}/{@link #JAVA}/{@link #BYTE_ARRAY} 中选择。</li>
 *   <li>获取具体序列化器实现请使用 {@link #getSerializer()}。</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 * @see #getSerializer()
 */
public enum RedisSerializerType {
	STRING,
	JAVA,
	JSON,
	BYTE_ARRAY;

	/**
	 * 获取当前类型对应的 {@link RedisSerializer 序列化器} 实例。
	 *
	 * <p>映射关系：</p>
	 * <ul>
	 *   <li>{@link #STRING} → {@link RedisSerializer#string()}（字符串序列化，通常基于 UTF-8）</li>
	 *   <li>{@link #JAVA} → {@link RedisSerializer#java()}（JDK 原生序列化，依赖对象实现 {@code Serializable}）</li>
	 *   <li>{@link #JSON} → {@link RedisSerializer#json()}（通用 JSON 序列化，兼容多数对象结构）</li>
	 *   <li>{@link #BYTE_ARRAY} → {@link RedisSerializer#byteArray()}（透传字节序列，不进行转换）</li>
	 * </ul>
	 *
	 * @return 与枚举常量匹配的 {@code RedisSerializer} 实例，不为 {@code null}
	 * @since 1.0.0
	 */
	public RedisSerializer<?> getSerializer() {
		return switch (this) {
			case STRING -> RedisSerializer.string();
			case JAVA -> RedisSerializer.java();
			case JSON -> RedisSerializer.json();
			case BYTE_ARRAY -> RedisSerializer.byteArray();
		};
	}
}

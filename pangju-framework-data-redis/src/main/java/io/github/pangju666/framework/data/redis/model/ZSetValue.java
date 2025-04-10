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

package io.github.pangju666.framework.data.redis.model;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.ZSetOperations;

/**
 * Redis有序集合（ZSet）成员值和分数的数据结构
 * <p>
 * 使用record特性提供了一个不可变的数据结构，用于表示Redis有序集合中的元素：
 * <ul>
 *     <li>value: 成员值，可以是任意类型</li>
 *     <li>score: 成员分数，用于排序，可以为null</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>不可变性：一旦创建，value和score都不可修改</li>
 *     <li>可比较性：实现了Comparable接口，支持基于score的排序</li>
 *     <li>类型安全：使用泛型确保类型安全</li>
 *     <li>空值处理：优雅处理null分数的情况</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 创建实例
 * ZSetValue<String> member = new ZSetValue<>("user1", 100.0);
 *
 * // 从TypedTuple创建
 * ZSetOperations.TypedTuple<String> tuple = // ...
 * ZSetValue<String> fromTuple = ZSetValue.of(tuple);
 *
 * // 排序比较
 * ZSetValue<String> member1 = new ZSetValue<>("user1", 100.0);
 * ZSetValue<String> member2 = new ZSetValue<>("user2", 200.0);
 * int result = member1.compareTo(member2); // 结果小于0
 * }</pre>
 * </p>
 *
 * @param <T> 成员值的类型
 * @author pangju666
 * @version 1.0.0
 * @since 1.0.0
 */
public record ZSetValue<T>(T value, Double score) implements Comparable<ZSetValue<T>> {
	/**
	 * 从TypedTuple创建ZSetValue实例
	 * <p>
	 * 将Spring Redis的TypedTuple对象转换为ZSetValue对象，
	 * 保持值和分数的对应关系。
	 * </p>
	 *
	 * @param typedTuple Spring Redis的TypedTuple对象
	 * @param <T>        成员值的类型
	 * @return 新的ZSetValue实例
	 * @throws NullPointerException 如果typedTuple为null
	 * @since 1.0.0
	 */
	public static <T> ZSetValue<T> of(final ZSetOperations.TypedTuple<T> typedTuple) {
		return new ZSetValue<>(typedTuple.getValue(), typedTuple.getScore());
	}


	/**
	 * 比较两个ZSetValue对象
	 * <p>
	 * 基于score进行比较，处理规则如下：
	 * <ul>
	 *     <li>如果两个score都为null，返回0（相等）</li>
	 *     <li>如果当前对象score为null，另一个不为null，返回-1（当前对象更小）</li>
	 *     <li>如果另一个对象score为null，当前对象不为null，返回1（当前对象更大）</li>
	 *     <li>如果两个score都不为null，返回score的自然顺序比较结果</li>
	 * </ul>
	 * </p>
	 *
	 * @param o 要比较的对象
	 * @return 比较结果：负数表示小于，0表示相等，正数表示大于
	 * @since 1.0.0
	 */
	@Override
	public int compareTo(ZSetValue<T> o) {
		if (ObjectUtils.allNull(o.score, this.score)) {
			return 0;
		} else if (o.score == null) {
			return 1;
		} else if (this.score == null) {
			return -1;
		}
		return this.score.compareTo(o.score);
	}
}
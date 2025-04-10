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
 *     <li>value: 成员值</li>
 *     <li>score: 成员分数，用于排序</li>
 * </ul>
 * </p>
 *
 * @param <T> 成员值的类型
 * @author pangju666
 * @since 1.0.0
 */
public record ZSetValue<T>(T value, Double score) implements Comparable<ZSetValue<T>> {
	public static <T> ZSetValue<T> of(final ZSetOperations.TypedTuple<T> typedTuple) {
		return new ZSetValue<>(typedTuple.getValue(), typedTuple.getScore());
	}

	@Override
	public int compareTo(ZSetValue<T> o) {
		if (ObjectUtils.allNull(o.score, this.score)) {
			return 0;
		} else if (o.score == null) {
			return -1;
		} else if (this.score == null) {
			return 1;
		}
		return this.score.compareTo(o.score);
	}
}
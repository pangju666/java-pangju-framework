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

package io.github.pangju666.framework.spring.utils;

import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Bean 工具类，继承自{@link org.springframework.beans.BeanUtils}。
 *
 * <p>在父类的基础上，提供空值友好的属性复制方法，支持在复制完成后执行自定义处理逻辑。</p>
 *
 * @author pangju666
 * @see org.springframework.beans.BeanUtils
 * @since 1.0.0
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {
	protected BeanUtils() {
	}

	/**
	 * 复制源对象属性到目标对象，并在复制后执行自定义操作
	 * <p>
	 * 该方法首先使用Spring的BeanUtils.copyProperties方法复制属性，
	 * 然后通过提供的BiConsumer回调函数执行自定义操作，可用于处理特殊字段或复杂对象。
	 * 如果源对象或目标对象为null，则不执行复制操作和自定义操作。
	 * </p>
	 *
	 * <p>
	 * 示例:
	 * <pre>{@code
	 * User source = new User("张三", 25);
	 * UserDTO target = new UserDTO();
	 * BeanUtils.copyProperties(source, target, (s, t) -> {
	 *     t.setFullName(s.getName());
	 *     t.setDisplayAge(s.getAge() + "岁");
	 * });
	 * }</pre>
	 * </p>
	 *
	 * @param source   源对象（可为 {@code null}）
	 * @param target   目标对象（可为 {@code null}）
	 * @param consumer 复制后的自定义操作（可为 {@code null}）
	 * @param <S>      源对象类型
	 * @param <T>      目标对象类型
	 * @see org.springframework.beans.BeanUtils#copyProperties(Object, Object)
	 * @since 1.0.0
	 */
	public static <S, T> void copyProperties(@Nullable final S source, @Nullable final T target,
											 @Nullable final BiConsumer<S, T> consumer) {
		if (Objects.nonNull(source) && Objects.nonNull(target)) {
			copyProperties(source, target);
			if (Objects.nonNull(consumer)) {
				consumer.accept(source, target);
			}
		}
	}
}
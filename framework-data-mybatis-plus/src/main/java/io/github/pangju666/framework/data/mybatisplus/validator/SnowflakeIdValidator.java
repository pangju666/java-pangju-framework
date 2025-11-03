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

package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * 雪花算法ID校验器
 * <p>
 * 用于校验基于雪花算法生成的ID字段的有效性。
 * 校验规则：
 * <ul>
 *     <li>不允许为null</li>
 *     <li>必须大于等于0</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 * @see io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeId
 */
public class SnowflakeIdValidator implements ConstraintValidator<SnowflakeId, Long> {
	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return Objects.nonNull(value) && value >= 0;
	}
}

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

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.UUID;

/**
 * UUID集合校验器
 * <p>
 * 用于校验UUID格式字符串集合的有效性。
 * 校验规则：
 * <ul>
 *     <li>允许为null</li>
 *     <li>集合中的元素不允许为null或空字符串</li>
 *     <li>集合中的每个元素必须是有效的UUID格式</li>
 *     <li>可通过notEmpty参数控制是否允许空集合</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 * @see io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds
 */
public class UuIdsValidator implements ConstraintValidator<UUIds, Collection<String>> {
	private boolean notEmpty;

	@Override
	public void initialize(UUIds constraintAnnotation) {
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, notEmpty, id -> {
				try {
					UUID.fromString(id);
					return true;
				} catch (IllegalArgumentException e) {
					return false;
				}
			});
	}
}

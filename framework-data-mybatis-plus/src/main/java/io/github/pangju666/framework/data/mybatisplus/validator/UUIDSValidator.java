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

import io.github.pangju666.commons.lang.pool.RegExPool;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIDS;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 无中划线 UUID 集合校验器
 * <p>
 * 基于 MyBatis-Plus 主键生成策略（无中划线 UUID）逐个校验集合元素：
 * 仅接受由小写十六进制字符组成且总长度为 32 的值，不包含中划线。
 * 与标准 RFC 4122 UUID 文本格式（含中划线）不同。
 * </p>
 *
 * <p>行为约定：</p>
 * <ul>
 *   <li>允许集合为 {@code null} 或空集合，视为通过；如需非空请叠加 {@code @NotNull} / {@code @NotEmpty}</li>
 *   <li>非空元素需满足：长度 32、仅小写十六进制、符合 UUID 结构</li>
 * </ul>
 *
 * @see io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIDS
 */
public class UUIDSValidator implements ConstraintValidator<UUIDS, Collection<String>> {
	private static final Pattern PATTERN = RegExUtils.compile(RegExPool.JAVA_UUID_SIMPLE, true, true);

	/**
	 * 校验逻辑
	 * <p>
	 * 集合为 {@code null} 或空集合时通过；否则逐个元素校验：非空白、长度 32、匹配小写十六进制的无中划线 UUID 模式。
	 * </p>
	 */
	@Override
	public boolean isValid(Collection<String> values, ConstraintValidatorContext context) {
		if (Objects.isNull(values) || values.isEmpty()) {
			return true;
		}
		for (String value : values) {
			if (StringUtils.isBlank(value) || value.length() != 32 || !PATTERN.matcher(value).matches()) {
				return false;
			}
		}
		return true;
	}
}

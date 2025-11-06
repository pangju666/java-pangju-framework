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
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 无中划线 UUID 单值校验器
 * <p>
 * 基于 MyBatis-Plus 主键生成策略（无中划线 UUID）进行字符串校验：
 * 仅接受由小写十六进制字符组成且总长度为 32 的值，不包含中划线。
 * 与标准 RFC 4122 UUID 文本格式（含中划线）不同。
 * </p>
 *
 * <p>行为约定：</p>
 * <ul>
 *   <li>允许输入为 {@code null}，视为通过；如需非空请叠加 {@code @NotNull}</li>
 *   <li>非空字符串需满足：长度 32、仅小写十六进制、符合 UUID 结构</li>
 * </ul>
 *
 * @see io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUID
 */
public class UUIDValidator implements ConstraintValidator<UUID, String> {
	private static final Pattern PATTERN = RegExUtils.compile(RegExPool.JAVA_UUID_SIMPLE, true, true);

	/**
	 * 校验逻辑
	 * <p>
	 * {@code null} 视为通过；非空时需满足：非空白、长度 32、匹配小写十六进制的无中划线 UUID 模式。
	 * </p>
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (Objects.isNull(value)) {
			return true;
		}
		return StringUtils.isNotBlank(value) && value.length() == 32 && PATTERN.matcher(value).matches();
	}
}

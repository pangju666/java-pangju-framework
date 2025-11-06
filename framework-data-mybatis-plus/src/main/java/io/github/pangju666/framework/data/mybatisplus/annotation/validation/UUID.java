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

package io.github.pangju666.framework.data.mybatisplus.annotation.validation;

import io.github.pangju666.framework.data.mybatisplus.validator.UUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 无中划线 UUID 校验注解
 * <p>
 * 用于校验基于 MyBatis-Plus 主键生成策略（常见为 32 位无中划线 UUID）的字符串 ID。
 * 与标准 RFC 4122 UUID 字符串（含中划线，如 {@code 8-4-4-4-12}）不同，本注解仅接受
 * 由小写十六进制字符组成且长度为 32 的值，不包含任何中划线或其他分隔符。
 * </p>
 *
 * <p>校验规则：</p>
 * <ul>
 *   <li>允许 {@code null} 值；如需非空请配合 {@code @NotNull}</li>
 *   <li>允许仅匹配小写十六进制（{@code 0-9a-f}）；不接受大写</li>
 *   <li>长度必须为 32；不包含中划线</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * public class UserEntity {
 *     @UUID
 *     private String id;
 * }
 * }</pre>
 *
 * @author pangju666
 * @see io.github.pangju666.framework.data.mybatisplus.validator.UUIDValidator
 * @see jakarta.validation.Constraint
 * @since 1.0.0
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface UUID {
	/**
	 * 消息模板
	 * <p>默认提示为“不是有效的id”，可通过国际化资源进行覆盖。</p>
	 */
	String message() default "不是有效的id";

	/**
	 * 分组定义
	 */
	Class<?>[] groups() default {};

	/**
	 * 负载定义
	 */
	Class<? extends Payload>[] payload() default {};
}
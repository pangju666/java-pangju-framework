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

import io.github.pangju666.framework.data.mybatisplus.validator.UUIDSValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 无中划线 UUID 集合校验注解
 * <p>
 * 用于校验集合中的每个元素是否为基于 MyBatis-Plus 主键生成策略的 32 位无中划线 UUID。
 * 与标准 RFC 4122 UUID 字符串不同，本注解仅接受由小写十六进制字符组成且长度为 32 的值，
 * 不包含中划线或其他分隔符。
 * </p>
 *
 * <p>校验规则：</p>
 * <ul>
 *   <li>允许集合为 {@code null} 或空集合；如需非空请配合 {@code @NotNull} / {@code @NotEmpty}</li>
 *   <li>集合内元素必须匹配小写十六进制且长度 32</li>
 *   <li>不接受大写字符或含中划线的格式</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * public class BatchRequest {
 *     @UUIDS
 *     private List<String> ids;
 * }
 * }</pre>
 *
 * @author pangju666
 * @see io.github.pangju666.framework.data.mybatisplus.validator.UUIDSValidator
 * @see io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUID
 * @since 1.0.0
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = UUIDSValidator.class)
public @interface UUIDS {
	String message() default "不是有效的id";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
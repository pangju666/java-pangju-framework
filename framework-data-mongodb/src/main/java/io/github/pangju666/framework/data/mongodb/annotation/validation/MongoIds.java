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

package io.github.pangju666.framework.data.mongodb.annotation.validation;

import io.github.pangju666.framework.data.mongodb.validator.MongoIdsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * MongoDB ObjectId集合格式校验注解
 * <p>
 * 用于验证字符串集合中的每个元素是否符合MongoDB的ObjectId格式。
 * 可以应用于方法、字段、注解、构造函数、参数和类型使用处。
 * 支持配置是否允许空集合。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = MongoIdsValidator.class)
public @interface MongoIds {
	String message() default "不是有效的id";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * 是否不允许为空集合
	 * <p>
	 * 当设置为true时，集合不能为空（size > 0）
	 * 当设置为false时，允许空集合（size >= 0）
	 * </p>
	 *
	 * @return true表示不允许为空集合，false表示允许空集合
	 * @since 1.0.0
	 */
	boolean notEmpty() default false;
}
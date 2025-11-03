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

package io.github.pangju666.framework.data.mongodb.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mongodb.annotation.validation.MongoIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * MongoDB ObjectId集合格式验证器
 * <p>
 * 实现{@link MongoIds}注解的验证逻辑：
 * <ul>
 *     <li>支持验证字符串集合中的每个元素是否符合ObjectId格式</li>
 *     <li>根据注解配置决定是否允许空集合</li>
 *     <li>使用{@link ObjectId#isValid(String)}进行格式验证</li>
 *     <li>处理可能出现的IllegalArgumentException异常</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class MongoIdsValidator implements ConstraintValidator<MongoId, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, true, id -> {
			try {
				return ObjectId.isValid(id);
			} catch (IllegalArgumentException e) {
				return false;
			}
		});
	}
}

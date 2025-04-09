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

package io.github.pangju666.framework.data.mongo.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mongo.annotation.validation.MongoIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bson.types.ObjectId;

import java.util.Collection;

public class MongoIdValidator implements ConstraintValidator<MongoIds, Collection<String>> {
	private boolean notEmpty;

	@Override
	public void initialize(MongoIds constraintAnnotation) {
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, notEmpty, id -> {
			try {
				return ObjectId.isValid(id);
			} catch (IllegalArgumentException e) {
				return false;
			}
		});
	}
}

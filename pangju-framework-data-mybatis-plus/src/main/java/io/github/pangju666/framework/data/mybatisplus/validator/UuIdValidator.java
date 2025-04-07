package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UuIdValidator implements ConstraintValidator<UUId, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, true,
			id -> {
				try {
					UUID.fromString(id);
					return true;
				} catch (IllegalArgumentException e) {
					return false;
				}
			});
	}
}

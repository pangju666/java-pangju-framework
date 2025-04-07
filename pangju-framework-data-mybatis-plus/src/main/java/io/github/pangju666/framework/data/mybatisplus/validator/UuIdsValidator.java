package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.UUID;

public class UuIdsValidator implements ConstraintValidator<UUIds, Collection<String>> {
	private boolean notEmpty;

	@Override
	public void initialize(UUIds constraintAnnotation) {
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, notEmpty,
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

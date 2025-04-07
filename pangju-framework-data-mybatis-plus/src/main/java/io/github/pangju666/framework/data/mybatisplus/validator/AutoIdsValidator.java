package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.AutoIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Objects;

public class AutoIdsValidator implements ConstraintValidator<AutoIds, Collection<Long>> {
	private boolean notEmpty;

	@Override
	public void initialize(AutoIds constraintAnnotation) {
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<Long> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, notEmpty,
			id -> Objects.nonNull(id) && id >= 1);
	}
}

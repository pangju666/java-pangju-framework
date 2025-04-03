package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Objects;

public class SnowflakeIdsValidator implements ConstraintValidator<SnowflakeIds, Collection<Long>> {
	private boolean allMatch;
	private boolean notEmpty;

	@Override
	public void initialize(SnowflakeIds constraintAnnotation) {
		this.allMatch = constraintAnnotation.allMatch();
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<Long> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, allMatch, notEmpty,
			id -> Objects.nonNull(id) && id >= 0);
	}
}

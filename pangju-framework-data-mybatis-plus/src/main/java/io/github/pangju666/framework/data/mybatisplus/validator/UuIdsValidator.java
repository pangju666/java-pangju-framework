package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds;
import io.github.pangju666.framework.data.mybatisplus.model.entity.uuid.UUId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class UuIdsValidator implements ConstraintValidator<UUIds, Collection<String>> {
	private boolean allMatch;
	private boolean notEmpty;

	@Override
	public void initialize(UUIds constraintAnnotation) {
		this.allMatch = constraintAnnotation.allMatch();
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, allMatch, notEmpty, UUId.PATTERN);
	}
}

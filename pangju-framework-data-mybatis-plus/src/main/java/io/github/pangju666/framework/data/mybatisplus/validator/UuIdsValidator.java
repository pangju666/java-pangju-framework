package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.lang.pool.RegExPool;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.regex.Pattern;

public class UuIdsValidator implements ConstraintValidator<UUIds, Collection<String>> {
	private static final Pattern PATTERN = RegExUtils.compile(RegExPool.UUID, true, true);

	private boolean allMatch;
	private boolean notEmpty;

	@Override
	public void initialize(UUIds constraintAnnotation) {
		this.allMatch = constraintAnnotation.allMatch();
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, allMatch, notEmpty, PATTERN);
	}
}

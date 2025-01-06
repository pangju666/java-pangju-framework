package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.lang.pool.RegExPool;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UuIdValidator implements ConstraintValidator<UUId, String> {
	private static final Pattern PATTERN = RegExUtils.compile(RegExPool.UUID, true, true);

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, true, true, PATTERN);
	}
}

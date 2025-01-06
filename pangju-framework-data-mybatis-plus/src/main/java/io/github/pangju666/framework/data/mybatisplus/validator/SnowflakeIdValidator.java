package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class SnowflakeIdValidator implements ConstraintValidator<SnowflakeId, Long> {
	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return Objects.nonNull(value);
	}
}

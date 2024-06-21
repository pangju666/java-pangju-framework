package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.Id;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class IdValidator implements ConstraintValidator<Id, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        return value >= 1;
    }
}

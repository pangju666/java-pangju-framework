package io.github.pangju666.framework.data.mybatisplus.annotation.validation;

import io.github.pangju666.framework.data.mybatisplus.validator.SnowflakeIdsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = SnowflakeIdsValidator.class)
public @interface SnowflakeIds {
	String message() default "不是有效的id";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean allMatch() default true;

	boolean notEmpty() default false;
}
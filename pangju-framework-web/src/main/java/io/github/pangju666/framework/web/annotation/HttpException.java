package io.github.pangju666.framework.web.annotation;

import io.github.pangju666.framework.web.enums.ExceptionType;
import org.springframework.http.HttpStatus;

public @interface HttpException {
	int code();

	ExceptionType type() default ExceptionType.CUSTOM;

	String description() default "";

	boolean log() default true;

	HttpStatus status() default HttpStatus.OK;
}
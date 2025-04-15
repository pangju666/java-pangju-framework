package io.github.pangju666.framework.web.annotation;

import io.github.pangju666.framework.web.enums.HttpExceptionType;
import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface HttpException {
	int code();

	HttpExceptionType type() default HttpExceptionType.CUSTOM;

	String description() default "";

	boolean log() default true;

	HttpStatus status() default HttpStatus.OK;
}
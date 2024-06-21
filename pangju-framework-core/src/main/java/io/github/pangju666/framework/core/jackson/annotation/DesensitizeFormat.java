package io.github.pangju666.framework.core.jackson.annotation;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.pangju666.framework.core.jackson.databind.serializer.DesensitizedJsonSerializer;
import io.github.pangju666.framework.core.jackson.enums.DesensitizedType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizedJsonSerializer.class)
public @interface DesensitizeFormat {
    DesensitizedType type() default DesensitizedType.CUSTOM;

    String format() default "";

    String regex() default "";

    int prefix() default -1;

    int suffix() default -1;
}
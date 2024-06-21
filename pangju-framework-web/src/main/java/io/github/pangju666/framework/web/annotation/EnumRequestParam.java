package io.github.pangju666.framework.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumRequestParam {
    String value() default "";

    boolean required() default true;

    String defaultValue() default "";

    String description();
}

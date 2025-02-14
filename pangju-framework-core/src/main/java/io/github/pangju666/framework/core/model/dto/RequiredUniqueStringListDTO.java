package io.github.pangju666.framework.core.model.dto;

import io.github.pangju666.commons.validation.annotation.NotBlankElements;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record RequiredUniqueStringListDTO(@NotBlankElements(notEmpty = true, message = "集合可能为空或存在空白的值")
										  @UniqueElements(message = "存在重复的字符串")
										  List<String> values) {
}
package io.github.pangju666.framework.core.model.dto;

import io.github.pangju666.commons.validation.annotation.NotBlankElements;

import java.util.List;

public record RequiredStringListDTO(@NotBlankElements(notEmpty = true, message = "集合可能为空或存在空白的值")
                                    List<String> values) {
}
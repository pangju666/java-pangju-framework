package io.github.pangju666.framework.core.model.dto;

import io.github.pangju666.commons.validation.annotation.NotBlankElements;

import java.util.List;

public record StringListDTO(@NotBlankElements List<String> values) {
}

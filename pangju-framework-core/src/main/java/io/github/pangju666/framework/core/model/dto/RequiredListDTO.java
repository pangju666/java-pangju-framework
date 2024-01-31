package io.github.pangju666.framework.core.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RequiredListDTO<T>(@NotEmpty(message = "集合不能为空")
								 @Valid
								 List<T> list) {
}

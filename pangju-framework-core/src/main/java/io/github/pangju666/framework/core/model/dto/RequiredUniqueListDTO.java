package io.github.pangju666.framework.core.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record RequiredUniqueListDTO<T>(@NotEmpty(message = "集合不能为空")
                                       @UniqueElements(message = "集合中存在重复的值")
                                       @Valid
                                       List<T> list) {
}
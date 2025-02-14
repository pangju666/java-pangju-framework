package io.github.pangju666.framework.core.model.dto;

import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record UniqueListDTO<T>(@UniqueElements(message = "集合中存在重复的值")
							   @Valid
							   List<T> list) {
}
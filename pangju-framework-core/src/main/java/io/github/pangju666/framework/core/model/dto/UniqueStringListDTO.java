package io.github.pangju666.framework.core.model.dto;

import io.github.pangju666.commons.validation.annotation.NotBlankElements;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record UniqueStringListDTO(@NotBlankElements
                                  @UniqueElements(message = "集合中存在重复的值")
                                  List<String> list) {
}

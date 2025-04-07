package io.github.pangju666.framework.data.mybatisplus.model.dto.uuid;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record UUIdRequiredListDTO(@UniqueElements(message = "存在重复的id")
								  @UUIds(notEmpty = true)
								  List<String> ids) {
}

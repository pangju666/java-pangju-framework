package io.github.pangju666.framework.data.mybatisplus.model.dto.uuid;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.UUIds;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record UUIdListDTO(@UniqueElements(message = "存在重复的id")
						  @UUIds List<String> ids) {
}

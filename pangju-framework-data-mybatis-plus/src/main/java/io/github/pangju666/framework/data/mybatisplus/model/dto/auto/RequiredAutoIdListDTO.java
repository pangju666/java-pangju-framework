package io.github.pangju666.framework.data.mybatisplus.model.dto.auto;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.AutoIds;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record RequiredAutoIdListDTO(@UniqueElements(message = "存在重复的id")
									@AutoIds(notEmpty = true)
									List<Long> ids) {
}

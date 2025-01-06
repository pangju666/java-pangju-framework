package io.github.pangju666.framework.data.mybatisplus.model.dto.auto;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.AutoIds;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record AutoIdListDTO(@UniqueElements(message = "存在重复的id")
							@AutoIds List<Long> ids) {
}

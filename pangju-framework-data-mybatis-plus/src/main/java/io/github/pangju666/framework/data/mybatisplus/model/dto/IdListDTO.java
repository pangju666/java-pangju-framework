package io.github.pangju666.framework.data.mybatisplus.model.dto;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.Ids;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record IdListDTO(@UniqueElements(message = "存在重复的id")
                        @Ids List<Long> ids) {
}

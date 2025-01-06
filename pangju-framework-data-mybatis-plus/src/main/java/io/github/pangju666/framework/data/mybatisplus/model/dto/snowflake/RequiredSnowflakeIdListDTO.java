package io.github.pangju666.framework.data.mybatisplus.model.dto.snowflake;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeIds;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record RequiredSnowflakeIdListDTO(@UniqueElements(message = "存在重复的id")
										 @SnowflakeIds(notEmpty = true)
										 List<Long> ids) {
}

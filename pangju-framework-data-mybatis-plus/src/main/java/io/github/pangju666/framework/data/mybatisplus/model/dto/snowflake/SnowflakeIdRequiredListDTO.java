/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.data.mybatisplus.model.dto.snowflake;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeIds;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

/**
 * 必填雪花ID列表数据传输对象
 * <p>
 * 用于传输雪花算法生成的ID列表，支持ID的唯一性校验。
 * 列表不允许为空，且列表中的元素必须符合雪花ID格式。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public record SnowflakeIdRequiredListDTO(@UniqueElements(message = "存在重复的id")
										 @SnowflakeIds(notEmpty = true)
										 List<Long> ids) {
}

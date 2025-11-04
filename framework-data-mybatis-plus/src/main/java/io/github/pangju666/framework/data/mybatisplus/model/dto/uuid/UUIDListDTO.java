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

package io.github.pangju666.framework.data.mybatisplus.model.dto.uuid;

import io.github.pangju666.commons.validation.annotation.UUIDS;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

/**
 * UUID列表数据传输对象
 * <p>
 * 用于传输UUID类型的ID列表，支持ID的唯一性校验。
 * 列表允许为空，但列表中的元素必须符合UUID格式。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public record UUIDListDTO(@UniqueElements(message = "存在重复的id") @UUIDS List<String> ids) {
}

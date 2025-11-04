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

package io.github.pangju666.framework.web.model.dto;

import io.github.pangju666.commons.validation.annotation.NotBlankElements;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

/**
 * 必填且唯一的字符串列表数据传输对象
 * <p>
 * 用于封装字符串列表数据，要求：
 * <ul>
 *     <li>列表不能为空</li>
 *     <li>列表元素不能为空白字符串</li>
 *     <li>列表元素不能重复</li>
 * </ul>
 * </p>
 *
 * @param values 字符串列表
 * @author pangju666
 * @since 1.0.0
 */
public record RequiredUniqueStringListDTO(
	@NotEmpty(message = "列表不允许为空") @NotBlankElements(message = "列表存在空白值")
										  @UniqueElements(message = "存在重复的字符串") List<String> values) {
}
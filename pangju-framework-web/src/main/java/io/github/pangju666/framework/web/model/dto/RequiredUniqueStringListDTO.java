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
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record RequiredUniqueStringListDTO(@NotBlankElements(notEmpty = true, message = "集合可能为空或存在空白的值")
										  @UniqueElements(message = "存在重复的字符串")
										  List<String> values) {
}
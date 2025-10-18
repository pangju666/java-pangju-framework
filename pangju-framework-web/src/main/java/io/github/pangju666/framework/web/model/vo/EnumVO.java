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

package io.github.pangju666.framework.web.model.vo;

/**
 * 枚举值对象
 * <p>
 * 用于传输枚举数据信息，包含：
 * <ul>
 *     <li>标签：用于显示的文本描述</li>
 *     <li>值：实际的枚举值</li>
 * </ul>
 * </p>
 *
 * @param label 显示标签
 * @param value 枚举值
 * @author pangju666
 * @since 1.0.0
 */
public record EnumVO(String label, String value) {
}
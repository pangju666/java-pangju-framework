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
 * HTTP 异常信息值对象
 * <p>用于传输 HTTP 异常的基本信息：</p>
 * <ul>
 *   <li>类型标签（{@code typeLabel}）：来自 {@link io.github.pangju666.framework.web.enums.HttpExceptionType#getLabel()} 的人类可读名称</li>
 *   <li>类型名称（{@code type}）：枚举名字符串，取自 {@link io.github.pangju666.framework.web.enums.HttpExceptionType#name()}</li>
 *   <li>错误码（{@code code}）：通常由 {@link io.github.pangju666.framework.web.enums.HttpExceptionType#computeCode(int)} 计算得到</li>
 *   <li>描述（{@code description}）：异常说明</li>
 * </ul>
 *
 * <p>构建示例：</p>
 * <pre>{@code
 * HttpException annotation;
 * new HttpExceptionVO(
 *     annotation.type().getLabel(),
 *     annotation.type().name(),
 *     annotation.type().computeCode(annotation.code()),
 *     annotation.description()
 * );
 * }</pre>
 *
 * @param typeLabel   类型标签（人类可读）
 * @param type        类型名称（枚举名字符串）
 * @param code        错误码（整型）
 * @param description 异常描述（可为空）
 * @author pangju666
 * @see io.github.pangju666.framework.web.enums.HttpExceptionType
 * @see io.github.pangju666.framework.web.annotation.HttpException
 * @since 1.0.0
 */
public record HttpExceptionVO(String typeLabel, String type, int code, String description) {
}
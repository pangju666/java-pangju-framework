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

package io.github.pangju666.framework.web.model.error;

/**
 * 数据操作错误记录
 * <p>
 * 用于封装数据操作过程中的错误上下文信息，包括：
 * <ul>
 *     <li>数据来源（source）：如用户表、订单表等，指明操作的具体数据来源</li>
 *     <li>数据说明（description）：如用户ID、订单编号等，描述数据字段的含义</li>
 *     <li>数据内容（data）：实际操作的数据值，可以是任意类型的对象</li>
 *     <li>错误原因（reason）：具体说明操作失败的原因，如"记录不存在"、"唯一约束冲突"等</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>用于构建数据操作异常时提供结构化的错误信息</li>
 *     <li>便于错误日志的记录和错误信息的展示</li>
 *     <li>支持统一的错误处理机制</li>
 *     <li>提供操作失败的具体原因，便于问题定位</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 创建数据操作错误对象
 * DataOperationError error = new DataOperationError(
 *     "用户表",     // 数据来源
 *     "用户ID",    // 数据说明
 *     userId,     // 数据值
 *     "记录不存在"  // 错误原因
 * );
 *
 * // 在异常中使用
 * throw new DataOperationException(
 *     error,
 *     "删除",        // 操作类型
 *     "删除用户失败"  // 展示消息
 * );
 * }</pre>
 * </p>
 *
 * <p>
 * 属性说明：
 * <ul>
 *     <li>source: 数据操作的来源，通常是表名、集合名或其他数据源标识</li>
 *     <li>description: 对操作数据的描述，通常是字段名或字段含义</li>
 *     <li>data: 操作涉及的实际数据，可以是任意类型</li>
 *     <li>reason: 操作失败的具体原因，提供更精确的错误上下文</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see io.github.pangju666.framework.web.exception.base.DataOperationException 数据操作异常类
 * @since 1.0.0
 */
public record DataOperationError(String source, String description, Object data, String reason) {
}

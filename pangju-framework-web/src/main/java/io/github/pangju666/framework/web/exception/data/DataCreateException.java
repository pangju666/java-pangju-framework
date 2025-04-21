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

package io.github.pangju666.framework.web.exception.data;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.DataOperationException;
import io.github.pangju666.framework.web.model.error.DataOperationError;

/**
 * 数据创建异常
 * <p>
 * 封装数据创建过程中发生的异常情况，提供结构化的错误上下文信息：
 * <ul>
 *     <li>唯一约束冲突：尝试创建的数据违反唯一性约束条件</li>
 *     <li>外键约束失败：创建数据时关联的外键数据不存在</li>
 *     <li>数据验证失败：创建的数据不符合业务规则或校验标准</li>
 *     <li>重复数据提交：相同数据已经存在于系统中</li>
 * </ul>
 * </p>
 *
 * <p>
 * 核心特性：
 * <ul>
 *     <li>错误码：2200（{@link HttpExceptionType#DATA_OPERATION} + 200）</li>
 *     <li>提供结构化日志记录，便于问题追踪</li>
 *     <li>固定操作类型："创建"</li>
 *     <li>保留完整异常链，可包含原始异常</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <pre>{@code
 * // 基本用法
 * throw new DataCreateException("用户名已存在");
 *
 * // 使用错误数据对象
 * DataOperationError error = new DataOperationError(
 *     "用户表",       // 数据来源
 *     "用户名",      // 数据描述
 *     username,    // 数据值
 *     "用户名已存在" // 错误原因
 * );
 * throw new DataCreateException("用户创建失败", error);
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see DataOperationException 数据操作异常基类
 * @see DataOperationError 数据操作错误记录
 * @since 1.0.0
 */
@HttpException(code = 200, description = "数据创建错误", type = HttpExceptionType.DATA_OPERATION)
public class DataCreateException extends DataOperationException {
	/**
	 * 默认错误消息
	 * <p>
	 * 当未提供自定义错误消息时使用的标准错误消息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String ERROR_MESSAGE = "数据创建错误";
	/**
	 * 操作类型常量
	 * <p>
	 * 标识此异常对应的固定操作类型为"创建"。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String OPERATION = "创建";

	/**
	 * 创建数据创建异常实例
	 * <p>
	 * 使用{@link #ERROR_MESSAGE 默认错误消息}和指定的错误原因构造异常。
	 * </p>
	 *
	 * @param reason 错误原因，描述创建失败的具体原因
	 * @since 1.0.0
	 */
	public DataCreateException(String reason) {
		super(ERROR_MESSAGE, OPERATION, reason);
	}

	/**
	 * 创建带有原因异常的数据创建异常实例
	 * <p>
	 * 使用{@link #ERROR_MESSAGE 默认错误消息}、指定的错误原因及原始异常构造异常。
	 * </p>
	 *
	 * @param reason 错误原因，描述创建失败的具体原因
	 * @param cause  导致此异常的原始异常，保留完整异常链
	 * @since 1.0.0
	 */
	public DataCreateException(String reason, Throwable cause) {
		super(ERROR_MESSAGE, OPERATION, reason, cause);
	}

	/**
	 * 创建自定义消息的数据创建异常实例
	 * <p>
	 * 使用自定义错误消息和指定的错误原因构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据创建错误"
	 * @param reason  错误原因，描述创建失败的具体原因
	 * @since 1.0.0
	 */
	public DataCreateException(String message, String reason) {
		super(message, OPERATION, reason);
	}

	/**
	 * 创建自定义消息且带有原因异常的数据创建异常实例
	 * <p>
	 * 使用自定义错误消息、指定的错误原因及原始异常构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据创建错误"
	 * @param reason  错误原因，描述创建失败的具体原因
	 * @param cause   导致此异常的原始异常，保留完整异常链
	 * @since 1.0.0
	 */
	public DataCreateException(String message, String reason, Throwable cause) {
		super(message, OPERATION, reason, cause);
	}

	/**
	 * 创建包含数据操作错误信息的数据创建异常实例
	 * <p>
	 * 使用自定义错误消息和结构化的错误信息对象构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据创建错误"
	 * @param error   数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @since 1.0.0
	 */
	public DataCreateException(String message, DataOperationError error) {
		super(message, OPERATION, error);
	}

	/**
	 * 创建包含数据操作错误信息和原因异常的数据创建异常实例
	 * <p>
	 * 使用自定义错误消息、结构化的错误信息对象及原始异常构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据创建错误"
	 * @param error   数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @param cause   导致此异常的原始异常，保留完整异常链
	 * @since 1.0.0
	 */
	public DataCreateException(String message, DataOperationError error, Throwable cause) {
		super(message, OPERATION, error, cause);
	}
}
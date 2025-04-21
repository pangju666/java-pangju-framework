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
 * 数据保存异常
 * <p>
 * 封装数据保存过程中发生的异常情况，提供结构化的错误上下文信息：
 * <ul>
 *     <li>数据验证失败：保存的数据不符合业务规则或校验标准</li>
 *     <li>保存操作失败：数据库保存操作执行过程中发生错误</li>
 *     <li>事务回滚：由于其他操作失败导致整个事务回滚</li>
 *     <li>数据状态异常：数据当前状态不允许执行保存操作</li>
 * </ul>
 * </p>
 *
 * <p>
 * 核心特性：
 * <ul>
 *     <li>错误码：2400（{@link HttpExceptionType#DATA_OPERATION} + 400）</li>
 *     <li>提供结构化日志记录，便于问题追踪</li>
 *     <li>固定操作类型："保存"</li>
 *     <li>保留完整异常链，可包含原始异常</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <pre>{@code
 * // 基本用法
 * throw new DataSaveException("数据验证失败");
 *
 * // 使用错误数据对象
 * DataOperationError error = new DataOperationError(
 *     "商品表",              // 数据来源
 *     "商品库存",            // 数据描述
 *     inventory,           // 数据值
 *     "库存数量不能为负数"    // 错误原因
 * );
 * throw new DataSaveException("保存商品信息失败", error);
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see DataOperationException 数据操作异常基类
 * @see DataOperationError 数据操作错误记录
 * @since 1.0.0
 */
@HttpException(code = 400, description = "数据保存错误", type = HttpExceptionType.DATA_OPERATION)
public class DataSaveException extends DataOperationException {
	/**
	 * 默认错误消息
	 * <p>
	 * 当未提供自定义错误消息时使用的标准错误消息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String ERROR_MESSAGE = "数据保存错误";
	/**
	 * 操作类型常量
	 * <p>
	 * 标识此异常对应的固定操作类型为"保存"。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String OPERATION = "保存";

	/**
	 * 创建数据保存异常实例
	 * <p>
	 * 使用{@link #ERROR_MESSAGE 默认错误消息}和指定的错误原因构造异常。
	 * </p>
	 *
	 * @param reason 错误原因，描述保存失败的具体原因
	 * @since 1.0.0
	 */
	public DataSaveException(String reason) {
		super(ERROR_MESSAGE, OPERATION, reason);
	}

	/**
	 * 创建带有原因异常的数据保存异常实例
	 * <p>
	 * 使用{@link #ERROR_MESSAGE 默认错误消息}、指定的错误原因及原始异常构造异常。
	 * </p>
	 *
	 * @param reason 错误原因，描述保存失败的具体原因
	 * @param cause  导致此异常的原始异常，保留完整异常链
	 * @since 1.0.0
	 */
	public DataSaveException(String reason, Throwable cause) {
		super(ERROR_MESSAGE, OPERATION, reason, cause);
	}

	/**
	 * 创建自定义消息的数据保存异常实例
	 * <p>
	 * 使用自定义错误消息和指定的错误原因构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据保存错误"
	 * @param reason  错误原因，描述保存失败的具体原因
	 * @since 1.0.0
	 */
	public DataSaveException(String message, String reason) {
		super(message, OPERATION, reason);
	}

	/**
	 * 创建自定义消息且带有原因异常的数据保存异常实例
	 * <p>
	 * 使用自定义错误消息、指定的错误原因及原始异常构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据保存错误"
	 * @param reason  错误原因，描述保存失败的具体原因
	 * @param cause   导致此异常的原始异常，保留完整异常链
	 * @since 1.0.0
	 */
	public DataSaveException(String message, String reason, Throwable cause) {
		super(message, OPERATION, reason, cause);
	}

	/**
	 * 创建包含数据操作错误信息的数据保存异常实例
	 * <p>
	 * 使用自定义错误消息和结构化的错误信息对象构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据保存错误"
	 * @param error   数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @since 1.0.0
	 */
	public DataSaveException(String message, DataOperationError error) {
		super(message, OPERATION, error);
	}

	/**
	 * 创建包含数据操作错误信息和原因异常的数据保存异常实例
	 * <p>
	 * 使用自定义错误消息、结构化的错误信息对象及原始异常构造异常。
	 * </p>
	 *
	 * @param message 自定义错误消息，替代默认的"数据保存错误"
	 * @param error   数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @param cause   导致此异常的原始异常，保留完整异常链
	 * @since 1.0.0
	 */
	public DataSaveException(String message, DataOperationError error, Throwable cause) {
		super(message, OPERATION, error, cause);
	}
}
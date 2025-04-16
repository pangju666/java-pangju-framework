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

/**
 * 数据查询异常
 * <p>
 * 用于处理数据查询过程中的异常情况，如：
 * <ul>
 *     <li>查询条件无效</li>
 *     <li>数据不存在</li>
 *     <li>查询执行失败</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：2100（{@link HttpExceptionType#DATA_OPERATION} + 100）</li>
 *     <li>记录日志</li>
 *     <li>固定操作类型：查询</li>
 *     <li>支持异常链追踪</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 100, description = "数据查询错误", type = HttpExceptionType.DATA_OPERATION)
public class DataQueryException extends DataOperationException {
	/**
	 * 默认错误消息
	 *
	 * @since 1.0.0
	 */
	protected static final String ERROR_MESSAGE = "数据查询错误";
	/**
	 * 操作类型常量
	 *
	 * @since 1.0.0
	 */
	protected static final String OPERATION = "创建";

	/**
	 * 创建数据查询异常实例
	 *
	 * @param source      数据来源（如：用户表）
	 * @param description 数据说明（如：用户ID）
	 * @param data        查询条件或参数
	 * @param reason      错误原因
	 * @since 1.0.0
	 */
	public DataQueryException(String source, String description, Object data, String reason) {
		super(ERROR_MESSAGE, source, OPERATION, description, data, reason);
	}

	/**
	 * 创建带有原因异常的数据查询异常实例
	 *
	 * @param source      数据来源（如：用户表）
	 * @param description 数据说明（如：用户ID）
	 * @param data        查询条件或参数
	 * @param reason      错误原因
	 * @param cause       导致此异常的原始异常
	 * @since 1.0.0
	 */
	public DataQueryException(String source, String description, Object data, String reason, Throwable cause) {
		super(ERROR_MESSAGE, source, OPERATION, description, data, reason, cause);
	}

	/**
	 * 创建自定义消息的数据查询异常实例
	 *
	 * @param message     自定义错误消息
	 * @param source      数据来源（如：用户表）
	 * @param description 数据说明（如：用户ID）
	 * @param data        查询条件或参数
	 * @param reason      错误原因
	 * @since 1.0.0
	 */
	public DataQueryException(String message, String source, String description, Object data, String reason) {
		super(message, source, OPERATION, description, data, reason);
	}

	/**
	 * 创建自定义消息且带有原因异常的数据查询异常实例
	 *
	 * @param message     自定义错误消息
	 * @param source      数据来源（如：用户表）
	 * @param description 数据说明（如：用户ID）
	 * @param data        查询条件或参数
	 * @param reason      错误原因
	 * @param cause       导致此异常的原始异常
	 * @since 1.0.0
	 */
	public DataQueryException(String message, String source, String description, Object data, String reason, Throwable cause) {
		super(message, source, OPERATION, description, data, reason, cause);
	}
}
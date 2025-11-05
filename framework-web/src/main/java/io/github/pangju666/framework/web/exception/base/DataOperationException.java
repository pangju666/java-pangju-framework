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

package io.github.pangju666.framework.web.exception.base;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.model.error.DataOperationError;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * 数据操作异常
 * <p>
 * 封装数据操作过程中发生的异常，提供结构化的错误上下文信息：
 * <ul>
 *     <li>操作类型：如新增、修改、删除、查询等</li>
 *     <li>错误数据：包含来源、描述、数据值和失败原因</li>
 * </ul>
 * </p>
 *
 * <p>
 * 核心特性：
 * <ul>
 *     <li>错误码：-2000（{@link HttpExceptionType#DATA_OPERATION}）</li>
 *     <li>结构化日志记录，便于问题追踪</li>
 *     <li>支持无错误数据和有错误数据两种构造方式</li>
 *     <li>可包含原始异常，保留完整异常链</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <pre>{@code
 * // 基本用法
 * throw new DataOperationException(
 *     "删除",          // 操作类型
 *     "删除用户失败",   // 展示消息
 *     "记录不存在"      // 错误原因
 * );
 *
 * // 使用错误数据对象
 * DataOperationError error = new DataOperationError(
 *     "用户表",       // 数据来源
 *     "用户ID",      // 数据描述
 *     userId,       // 数据值
 *     "记录不存在"    // 错误原因
 * );
 * throw new DataOperationException(
 *     "删除",        // 操作类型
 *     "删除用户失败", // 展示消息
 *     error         // 错误信息
 * );
 * }</pre>
 * </p>
 *
 * <p>
 * 子类体系：
 * <ul>
 *     <li>{@link io.github.pangju666.framework.web.exception.data.DataCreateException} - 数据创建异常</li>
 *     <li>{@link io.github.pangju666.framework.web.exception.data.DataUpdateException} - 数据更新异常</li>
 *     <li>{@link io.github.pangju666.framework.web.exception.data.DataRemoveException} - 数据删除异常</li>
 *     <li>{@link io.github.pangju666.framework.web.exception.data.DataQueryException} - 数据查询异常</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see DataOperationError 数据操作错误记录
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.DATA_OPERATION, description = "数据操作错误")
public class DataOperationException extends BaseHttpException {
	/**
	 * 表示一个空的数据操作错误对象。
	 * 当未提供具体错误信息时，用作默认值。
	 * 该常量实例化时所有字段均为null，表示没有特定的错误信息。
	 *
	 * @since 1.0.0
	 */
	protected static final DataOperationError EMPTY_ERROR = new DataOperationError(null, null, null, null);

	/**
	 * 操作类型
	 * <p>
	 * 记录具体的操作类型，如：
	 * <ul>
	 *     <li>新增</li>
	 *     <li>修改</li>
	 *     <li>删除</li>
	 *     <li>查询</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final String operation;
	/**
	 * 表示数据操作错误的详细信息。
	 *
	 * @since 1.0.0
	 */
	protected final DataOperationError error;

	/**
	 * 创建一个带有原因异常的数据操作异常。
	 *
	 * @param operation 发生异常的操作名称
	 * @param message   异常消息
	 * @param reason    异常原因的描述
	 * @since 1.0.0
	 */
	public DataOperationException(String operation, String message, String reason) {
		super(message, reason);
		this.operation = operation;
		this.error = EMPTY_ERROR;
	}

	/**
	 * 创建一个带有原因异常的数据操作异常。
	 *
	 * @param operation 发生异常的操作名称
	 * @param message   异常消息
	 * @param reason    异常原因的描述
	 * @param cause     导致此异常的原始异常
	 * @since 1.0.0
	 */
	public DataOperationException(String operation, String message, String reason, Throwable cause) {
		super(message, reason, cause);
		this.operation = operation;
		this.error = EMPTY_ERROR;
	}

	/**
	 * 创建一个包含数据操作错误信息的数据操作异常。
	 *
	 * @param operation 发生异常的操作名称
	 * @param message   异常消息
	 * @param error     数据操作错误信息对象，如果为null则使用EMPTY_ERROR
	 * @since 1.0.0
	 */
	public DataOperationException(String operation, String message, DataOperationError error) {
		super(message, Objects.isNull(error) ? null : error.reason());
		this.operation = operation;
		this.error = ObjectUtils.getIfNull(error, EMPTY_ERROR);
	}

	/**
	 * 创建一个包含数据操作错误信息和原因异常的数据操作异常。
	 *
	 * @param operation 发生异常的操作名称
	 * @param message   异常消息
	 * @param error     数据操作错误信息对象，如果为null则使用EMPTY_ERROR
	 * @param cause     导致此异常的原始异常
	 * @since 1.0.0
	 */
	public DataOperationException(String operation, String message, DataOperationError error, Throwable cause) {
		super(message, Objects.isNull(error) ? null : error.reason(), cause);
		this.operation = operation;
		this.error = ObjectUtils.getIfNull(error, EMPTY_ERROR);
	}

	/**
	 * 获取数据操作错误信息对象。
	 *
	 * @return 如果error是 {@link #EMPTY_ERROR}则返回null，否则返回error对象
	 * @since 1.0.0
	 */
	public DataOperationError getError() {
		return this.error == EMPTY_ERROR ? null : this.error;
	}

	/**
	 * 重写日志记录方法
	 * <p>
	 * 提供数据操作异常的结构化日志信息，包含完整的操作上下文：
	 * <ul>
	 *     <li>数据来源：记录操作的数据表或来源</li>
	 *     <li>操作类型：记录执行的具体操作</li>
	 *     <li>数据描述：记录操作的数据字段含义</li>
	 *     <li>数据内容：记录操作的具体数据值</li>
	 *     <li>错误原因：记录操作失败的具体原因</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 日志格式：
	 * <pre>
	 * 数据操作错误，来源：[error.source]，操作：[operation]，数据描述：[error.description]，数据值：[error.data]，原因：[reason]
	 * </pre>
	 * 所有字段为空时默认显示"未知"
	 * </p>
	 *
	 * @param logger 用于记录日志的Logger实例
	 * @param level  日志记录的级别
	 * @see #valueToString(Object, String)
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger, Level level) {
		String message = String.format("数据操作错误，来源：%s，操作：%s，数据描述：%s，数据值：%s，原因：%s",
			StringUtils.defaultIfBlank(this.error.source(), "未知"),
			StringUtils.defaultIfBlank(this.operation, "未知"),
			StringUtils.defaultIfBlank(this.error.description(), "未知"),
			valueToString(this.error.data(), "未知"),
			StringUtils.defaultIfBlank(this.reason, "未知"));
		logger.atLevel(level)
			.setCause(this)
			.log(message);
	}
}
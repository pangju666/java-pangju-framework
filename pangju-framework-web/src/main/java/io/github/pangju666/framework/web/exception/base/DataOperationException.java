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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * 数据操作异常
 * <p>
 * 用于处理数据操作过程中的异常情况，提供详细的操作上下文信息，包括：
 * <ul>
 *     <li>数据来源：如用户表、订单表等</li>
 *     <li>操作类型：如新增、修改、删除等</li>
 *     <li>数据说明：如用户ID、订单编号等（字段含义）</li>
 *     <li>数据内容：具体的数据值</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>基础错误码：2000（{@link HttpExceptionType#DATA_OPERATION}）</li>
 *     <li>记录日志</li>
 *     <li>提供结构化的错误信息</li>
 *     <li>支持异常链追踪</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * try {
 *     repository.deleteById(id);
 * } catch (Exception e) {
 *     throw new DataOperationException(
 *         "删除用户失败",     // 展示消息
 *         "用户表",         // 数据来源
 *         "删除",          // 操作类型
 *         "用户ID",        // 数据说明
 *         id,             // 数据值
 *         "记录不存在",     // 错误原因
 *         e               // 原始异常
 *     );
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.DATA_OPERATION)
public class DataOperationException extends BaseHttpException {
	/**
	 * 数据来源
	 * <p>
	 * 记录发生异常的数据来源，如：
	 * <ul>
	 *     <li>用户表</li>
	 *     <li>订单表</li>
	 *     <li>商品表</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final String source;
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
	 * 数据内容
	 * <p>
	 * 记录操作涉及的具体数据值，如：
	 * <ul>
	 *     <li>主键值：1001</li>
	 *     <li>字段值：张三</li>
	 *     <li>复杂对象：整个实体对象</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final Object data;
	/**
	 * 数据说明
	 * <p>
	 * 描述数据字段的含义，如：
	 * <ul>
	 *     <li>用户ID</li>
	 *     <li>订单编号</li>
	 *     <li>商品名称</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final String description;

	/**
	 * 创建数据操作异常实例
	 *
	 * @param message     展示给用户的错误消息
	 * @param source      数据来源（如：用户表）
	 * @param operation   操作类型（如：删除）
	 * @param description 数据说明（如：用户ID）
	 * @param data        数据值（如：1001）
	 * @param reason      错误原因
	 * @since 1.0.0
	 */
	public DataOperationException(String message, String source, String operation, String description, Object data,
								  String reason) {
		super(message, reason);
		this.operation = operation;
		this.source = source;
		this.data = data;
		this.description = description;
	}

	/**
	 * 创建带有原因异常的数据操作异常实例
	 *
	 * @param message     展示给用户的错误消息
	 * @param source      数据来源（如：用户表）
	 * @param operation   操作类型（如：删除）
	 * @param description 数据说明（如：用户ID）
	 * @param data        数据值（如：1001）
	 * @param reason      错误原因
	 * @param cause       导致此异常的原始异常
	 * @since 1.0.0
	 */
	public DataOperationException(String message, String source, String operation, String description, Object data,
								  String reason, Throwable cause) {
		super(message, reason, cause);
		this.operation = operation;
		this.source = source;
		this.data = data;
		this.description = description;
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
	 * 数据操作错误，来源：[source]，操作：[operation]，数据描述：[description]，数据值：[data]，原因：[reason]
	 * </pre>
	 * 所有字段为空时默认显示"未知"
	 * </p>
	 *
	 * @param logger 用于记录日志的Logger实例
	 * @param level  日志记录的级别
	 * @since 1.0.0
	 * @see #valueToString(Object, String)
	 */
	@Override
	public void log(Logger logger, Level level) {
		String message = String.format("数据操作错误，来源：%s，操作：%s，数据描述：%s，数据值：%s，原因：%s",
			StringUtils.defaultIfBlank(this.source, "未知"),
			StringUtils.defaultIfBlank(this.operation, "未知"),
			StringUtils.defaultIfBlank(this.description, "未知"),
			valueToString(this.data, "未知"),
			StringUtils.defaultIfBlank(this.reason, "未知"));
		logger.atLevel(level)
			.setCause(this)
			.log(message);
	}
}
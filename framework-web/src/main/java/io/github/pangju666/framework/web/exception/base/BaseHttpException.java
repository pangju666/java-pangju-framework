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

import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.lang.WebConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;

/**
 * HTTP异常基类
 * <p>
 * 该类是所有HTTP相关异常的基础类，继承自{@link NestedRuntimeException}。
 * 提供了统一的异常处理机制，包括错误消息管理和日志记录功能。
 * </p>
 *
 * <p>
 * 主要特点：
 * <ul>
 *     <li>支持嵌套异常，便于异常链追踪</li>
 *     <li>提供统一的日志记录接口</li>
 *     <li>分离用户消息和错误原因</li>
 *     <li>支持多级别日志记录</li>
 * </ul>
 * </p>
 *
 * <p>
 * 重要说明：
 * <ul>
 *     <li>建议所有子类都标注{@link HttpException}注解以配置异常信息</li>
 *     <li>未标注注解的子类将使用以下默认配置：
 *         <ul>
 *             <li>错误码：{@link WebConstants#BASE_ERROR_CODE}</li>
 *             <li>异常类型：{@link HttpExceptionType#UNKNOWN}</li>
 *             <li>HTTP状态码：{@link HttpStatus#OK}</li>
 *             <li>异常概述：无</li>
 *             <li>日志记录：{@code true}</li>
 *         </ul>
 *     </li>
 *     <li>通过注解可以自定义配置错误码、类型、描述等信息</li>
 *     <li>注解信息将用于异常处理和响应生成</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 自定义异常类
 * @HttpException(
 *     code = 0001,  //实际为-6001
 *     type = HttpExceptionType.CUSTOM,
 *     description = "业务处理失败"
 * )
 * public class CustomHttpException extends BaseHttpException {
 *     public CustomHttpException(String message) {
 *         super(message, "详细的错误原因");
 *     }
 * }
 *
 * // 异常使用和日志记录
 * try {
 *     // 业务逻辑
 *     throw new CustomHttpException("操作失败");
 * } catch (CustomHttpException e) {
 *     // 使用ERROR级别记录日志
 *     e.log(logger);
 *     // 或使用指定级别记录日志
 *     e.log(logger, Level.WARN);
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see io.github.pangju666.framework.web.annotation.HttpException
 * @see io.github.pangju666.framework.web.enums.HttpExceptionType
 * @since 1.0.0
 */
public abstract class BaseHttpException extends NestedRuntimeException {
	/**
	 * 异常的详细原因
	 * <p>
	 * 用于记录异常的具体原因，通常包含更多技术细节，主要用于日志记录。
	 * 与message属性不同，reason通常包含对开发人员有用的详细信息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final String reason;

	/**
	 * 创建HTTP异常实例
	 *
	 * @param message 面向用户的异常消息，用于在API响应中展示
	 * @param reason  异常的详细原因，用于日志记录
	 * @since 1.0.0
	 */
	protected BaseHttpException(String message, String reason) {
		super(message);
		this.reason = reason;
	}

	/**
	 * 创建带有原因异常的HTTP异常实例
	 *
	 * @param message 面向用户的异常消息，用于在API响应中展示
	 * @param reason  异常的详细原因，用于日志记录
	 * @param cause   导致当前异常的原始异常
	 * @since 1.0.0
	 */
	protected BaseHttpException(String message, String reason, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	/**
	 * 使用指定级别记录异常日志
	 * <p>
	 * 此方法允许自定义日志级别，提供更灵活的日志记录方式：
	 * <ul>
	 *     <li>支持所有SLF4J定义的日志级别</li>
	 *     <li>自动包含异常堆栈信息</li>
	 *     <li>格式化输出错误原因</li>
	 * </ul>
	 * </p>
	 *
	 * @param logger 用于记录日志的SLF4J日志记录器
	 * @param level  日志记录的级别，如 INFO、WARN、ERROR 等
	 * @since 1.0.0
	 */
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log("原因：" + StringUtils.defaultIfBlank(this.reason, "无"));
	}

	/**
	 * 将值对象转换为字符串
	 * <p>
	 * 转换策略：
	 * <ul>
	 *     <li>空值：返回默认值</li>
	 *     <li>可JSON化对象：转换为JSON字符串</li>
	 *     <li>其他对象：调用toString方法</li>
	 * </ul>
	 * </p>
	 *
	 * @param value        需要转换的值对象
	 * @param defaultValue 值为空时的默认返回值
	 * @return 转换后的字符串
	 * @since 1.0.0
	 */
	protected String valueToString(Object value, String defaultValue) {
		if (ObjectUtils.isEmpty(value)) {
			return defaultValue;
		}
		try {
			return JsonUtils.toString(value);
		} catch (Exception ignored) {
			return value.toString();
		}
	}
}
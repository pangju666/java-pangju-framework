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
import org.springframework.http.HttpStatus;

/**
 * 参数校验异常
 * <p>
 * 用于处理请求参数验证相关的异常情况，如：
 * <ul>
 *     <li>参数格式错误：如数字格式、日期格式、邮箱格式等</li>
 *     <li>必填参数缺失：如必要参数为空、ID未传等</li>
 *     <li>参数值范围错误：如数值越界、字符串长度超限等</li>
 *     <li>参数组合错误：如互斥参数、依赖参数等</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>基础错误码：4000（{@link HttpExceptionType#VALIDATION}）</li>
 *     <li>HTTP状态码：400（{@link HttpStatus#BAD_REQUEST}）</li>
 *     <li>不记录日志</li>
 *     <li>直接使用错误消息作为原因</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 简单参数验证
 * if (StringUtils.isEmpty(name)) {
 *     throw new ValidationException("用户名不能为空");
 * }
 *
 * // 带原因异常的验证
 * try {
 *     DateUtils.parseDate(dateStr);
 * } catch (ParseException e) {
 *     throw new ValidationException("日期格式错误", e);
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.VALIDATION, log = false, status = HttpStatus.BAD_REQUEST)
public class ValidationException extends BaseHttpException {
	/**
	 * 创建参数校验异常实例
	 * <p>
	 * 使用相同的消息作为展示信息和错误原因。
	 * 由于是参数验证错误，错误消息通常可以直接展示给用户。
	 * </p>
	 *
	 * @param message 验证错误消息，同时用作展示信息和错误原因
	 * @since 1.0.0
	 */
	public ValidationException(String message) {
		super(message, message);
	}

	/**
	 * 创建带有原因异常的参数校验异常实例
	 * <p>
	 * 使用相同的消息作为展示信息和错误原因，并包含原始异常。
	 * 适用于包装其他验证框架的异常。
	 * </p>
	 *
	 * @param message 验证错误消息，同时用作展示信息和错误原因
	 * @param cause   导致此异常的原始异常
	 * @since 1.0.0
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, message, cause);
	}
}
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

/**
 * 业务逻辑异常
 * <p>
 * 用于处理业务逻辑相关的异常情况，如：
 * <ul>
 *     <li>业务规则验证失败：如余额不足、库存不足等</li>
 *     <li>业务流程异常：如订单状态流转异常、支付流程中断等</li>
 *     <li>业务状态错误：如重复提交、数据已过期等</li>
 *     <li>业务依赖服务异常：如短信发送失败、邮件推送失败等</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>基础错误码：1000（{@link HttpExceptionType#SERVICE}）</li>
 *     <li>HTTP状态码：200（成功但有业务异常）</li>
 *     <li>记录日志</li>
 *     <li>支持异常链追踪</li>
 *     <li>允许自定义错误消息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 简单业务异常
 * if (balance < amount) {
 *     throw new ServiceException("余额不足");
 * }
 *
 * // 带详细原因的业务异常
 * if (stock <= 0) {
 *     throw new ServiceException("商品已售罄", "商品ID:" + productId + "库存为0");
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.SERVICE)
public class ServiceException extends BaseHttpException {
	/**
	 * 创建业务异常实例
	 * <p>
	 * 使用相同的消息作为展示信息和错误原因。
	 * 适用于简单的业务异常场景。
	 * </p>
	 *
	 * @param message 错误消息，同时用作展示信息和日志记录
	 * @since 1.0.0
	 */
	public ServiceException(String message) {
		super(message, message);
	}

	/**
	 * 创建带有详细原因的业务异常实例
	 * <p>
	 * 分别指定展示消息和错误原因，便于细化错误信息。
	 * 展示消息用于客户端显示，错误原因用于日志记录。
	 * </p>
	 *
	 * @param message 展示给用户的错误消息
	 * @param reason 用于日志记录的详细错误原因
	 * @since 1.0.0
	 */
	public ServiceException(String message, String reason) {
		super(message, reason);
	}

	/**
	 * 创建带有原因异常的业务异常实例
	 * <p>
	 * 使用相同的消息作为展示信息和错误原因，并包含原始异常。
	 * 适用于需要异常链追踪的简单场景。
	 * </p>
	 *
	 * @param message 错误消息，同时用作展示信息和日志记录
	 * @param cause 导致此异常的原始异常
	 * @since 1.0.0
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, message, cause);
	}

	/**
	 * 创建完整的业务异常实例
	 * <p>
	 * 提供所有异常信息，包括展示消息、错误原因和原始异常。
	 * 适用于需要完整异常信息的复杂场景。
	 * </p>
	 *
	 * @param message 展示给用户的错误消息
	 * @param reason 用于日志记录的详细错误原因
	 * @param cause 导致此异常的原始异常
	 * @since 1.0.0
	 */
	public ServiceException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
	}
}
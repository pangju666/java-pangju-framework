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
 * 服务器内部异常
 * <p>
 * 用于处理服务器内部错误和系统级异常，如：
 * <ul>
 *     <li>系统运行时异常：空指针、类型转换等</li>
 *     <li>资源访问失败：文件IO、网络连接等</li>
 *     <li>系统配置错误：配置缺失、配置无效等</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>基础错误码：-5000（{@link HttpExceptionType#SERVER}）</li>
 *     <li>HTTP状态码：500（{@link HttpStatus#INTERNAL_SERVER_ERROR}）</li>
 *     <li>统一错误消息：{@link #SERVER_ERROR_MESSAGE}</li>
 *     <li>记录日志</li>
 *     <li>支持异常链追踪</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * try {
 *     // 业务逻辑
 * } catch (IOException e) {
 *     throw new ServerException("文件读取失败", e);
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.SERVER, description = "服务器内部错误", status = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends BaseHttpException {
	/**
	 * 统一的服务器错误消息
	 * <p>
	 * 用于向客户端展示的标准错误信息，避免暴露系统内部细节。
	 * 具体的错误原因通过reason参数记录，仅用于服务器日志。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String SERVER_ERROR_MESSAGE = "服务器内部错误";

	/**
	 * 创建服务器异常实例
	 * <p>
	 * 使用统一的错误消息和指定的错误原因创建异常。
	 * 错误原因仅用于日志记录，不会返回给客户端。
	 * </p>
	 *
	 * @param reason 具体的错误原因，用于日志记录
	 * @since 1.0.0
	 */
	public ServerException(String reason) {
		super(SERVER_ERROR_MESSAGE, reason);
	}

	/**
	 * 创建仅包含原始异常的服务器异常实例
	 * <p>
	 * 使用统一的错误消息和原始异常的消息创建异常。
	 * 自动使用原始异常的消息作为错误原因，简化异常包装过程。
	 * </p>
	 *
	 * @param cause 导致此异常的原始异常，其消息将用作错误原因
	 * @since 1.0.0
	 */
	public ServerException(Throwable cause) {
		super(SERVER_ERROR_MESSAGE, cause.getMessage(), cause);
	}

	/**
	 * 创建带有原因异常的服务器异常实例
	 * <p>
	 * 使用统一的错误消息、指定的错误原因和原始异常创建异常。
	 * 支持异常链追踪，便于问题定位。
	 * </p>
	 *
	 * @param reason 具体的错误原因，用于日志记录
	 * @param cause  导致此异常的原始异常
	 * @since 1.0.0
	 */
	public ServerException(String reason, Throwable cause) {
		super(SERVER_ERROR_MESSAGE, reason, cause);
	}
}
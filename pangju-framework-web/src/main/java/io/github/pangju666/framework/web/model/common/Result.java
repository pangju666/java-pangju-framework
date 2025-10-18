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

package io.github.pangju666.framework.web.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException;
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError;
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceErrorBuilder;
import io.github.pangju666.framework.web.pool.WebConstants;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.net.URI;
import java.util.Optional;

/**
 * 统一响应结果封装类
 * <p>
 * 用于统一封装API接口的响应结果，包括：
 * <ul>
 *     <li>状态码：表示操作的结果状态</li>
 *     <li>消息：对结果的文字描述</li>
 *     <li>数据：实际的响应数据</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 成功响应
 * Result<User> result = Result.ok(user);
 *
 * // 失败响应
 * Result<Void> error = Result.fail("操作失败");
 *
 * // 获取数据（可能抛出异常）
 * User user = result.getDataIfSuccess("用户服务", "获取用户", uri);
 *
 * // 安全获取数据（不抛出异常）
 * Optional<User> userOpt = result.geOptionalData("用户服务", "获取用户", uri, logger);
 * }</pre>
 * </p>
 *
 * @param <T>     响应数据类型
 * @param code    状态码
 * @param message 消息
 * @param data    数据
 * @author pangju666
 * @since 1.0.0
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record Result<T>(int code, String message, T data) {
	/**
	 * 私有构造方法
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @since 1.0.0
	 */
	public Result {
	}

	/**
	 * 创建成功响应（无数据）
	 * <p>
	 * 使用默认成功状态码和默认成功消息
	 * </p>
	 *
	 * @return 成功响应结果
	 * @since 1.0.0
	 */
	public static Result<Void> ok() {
		return new Result<>(WebConstants.SUCCESS_CODE, WebConstants.DEFAULT_SUCCESS_MESSAGE, null);
	}

	/**
	 * 创建成功响应（带数据）
	 * <p>
	 * 使用默认成功状态码和默认成功消息，并携带响应数据
	 * </p>
	 *
	 * @param data 响应数据
	 * @param <T>  数据类型
	 * @return 成功响应结果
	 * @since 1.0.0
	 */
	public static <T> Result<T> ok(T data) {
		return new Result<>(WebConstants.SUCCESS_CODE, WebConstants.DEFAULT_SUCCESS_MESSAGE, data);
	}

	/**
	 * 创建失败响应（使用默认错误信息）
	 * <p>
	 * 使用默认错误状态码和默认错误消息
	 * </p>
	 *
	 * @return 失败响应结果
	 * @since 1.0.0
	 */
	public static Result<Void> fail() {
		return new Result<>(WebConstants.BASE_ERROR_CODE, WebConstants.DEFAULT_FAILURE_MESSAGE, null);
	}

	/**
	 * 创建失败响应（自定义错误信息）
	 * <p>
	 * 使用默认错误状态码和自定义错误消息
	 * </p>
	 *
	 * @param message 自定义错误消息
	 * @return 失败响应结果
	 * @since 1.0.0
	 */
	public static Result<Void> fail(String message) {
		return new Result<>(WebConstants.BASE_ERROR_CODE, message, null);
	}

	/**
	 * 创建失败响应（自定义状态码和错误信息）
	 * <p>
	 * 如果提供的状态码为成功码，则使用默认错误状态码
	 * </p>
	 *
	 * @param code    自定义状态码
	 * @param message 自定义错误消息
	 * @return 失败响应结果
	 * @since 1.0.0
	 */
	public static Result<Void> fail(int code, String message) {
		return new Result<>(code == WebConstants.SUCCESS_CODE ? WebConstants.BASE_ERROR_CODE : code, message, null);
	}

	/**
	 * 验证并获取响应数据
	 * <p>
	 * 如果响应成功则返回数据，否则抛出远程服务异常。
	 * 适用于必须获取数据的场景，失败时通过异常中断执行。
	 * </p>
	 *
	 * @param service 服务名称，用于构建错误信息
	 * @param api     接口名称，用于构建错误信息
	 * @param uri     请求URI，用于构建错误信息
	 * @return 响应数据
	 * @throws HttpRemoteServiceException 当响应状态码不为成功时抛出
	 * @since 1.0.0
	 */
	public T validateData(final String service, final String api, final URI uri) {
		if (this.code == WebConstants.SUCCESS_CODE) {
			return this.data;
		}

		HttpRemoteServiceError httpRemoteServiceError = new HttpRemoteServiceErrorBuilder(service, api, uri)
			.code(this.code)
			.message(this.message)
			.build();
		throw new HttpRemoteServiceException(httpRemoteServiceError);
	}

	/**
	 * 安全获取响应数据
	 * <p>
	 * 如果响应成功则返回Optional包装的数据，失败则记录警告日志并返回空Optional。
	 * 适用于数据获取失败可降级处理的场景。
	 * </p>
	 *
	 * @param service 服务名称，用于构建错误信息
	 * @param api     接口名称，用于构建错误信息
	 * @param uri     请求URI，用于构建错误信息
	 * @param logger  日志记录器，用于记录失败信息
	 * @return Optional包装的响应数据，失败时返回空Optional
	 * @since 1.0.0
	 */
	public Optional<T> geOptionalData(final String service, final String api, final URI uri, final Logger logger) {
		if (this.code == WebConstants.SUCCESS_CODE) {
			return Optional.ofNullable(this.data);
		}

		HttpRemoteServiceError httpRemoteServiceError = new HttpRemoteServiceErrorBuilder(service, api, uri)
			.code(this.code)
			.message(this.message)
			.build();
		HttpRemoteServiceException httpRemoteServiceException = new HttpRemoteServiceException(httpRemoteServiceError);
		httpRemoteServiceException.log(logger, Level.WARN);
		return Optional.empty();
	}

	/**
	 * 获取响应状态码
	 *
	 * @return 状态码，0表示成功，其他值表示失败
	 * @since 1.0.0
	 */
	@Override
	public int code() {
		return code;
	}

	/**
	 * 获取响应消息
	 *
	 * @return 响应消息文本
	 * @since 1.0.0
	 */
	@Override
	public String message() {
		return message;
	}

	/**
	 * 获取响应数据
	 *
	 * @return 响应数据对象，可能为null
	 * @since 1.0.0
	 */
	@Override
	public T data() {
		return data;
	}

	/**
	 * 将响应结果转换为JSON字符串
	 * <p>
	 * 使用JsonUtils工具类进行序列化，null值会被忽略
	 * </p>
	 *
	 * @return JSON格式的字符串表示
	 * @see JsonUtils#toString()
	 * @since 1.0.0
	 */
	@Override
	public String toString() {
		return JsonUtils.toString(this);
	}
}
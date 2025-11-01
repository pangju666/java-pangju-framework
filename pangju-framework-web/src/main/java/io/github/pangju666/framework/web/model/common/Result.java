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
import io.github.pangju666.framework.web.pool.WebConstants;

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
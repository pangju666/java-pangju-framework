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

package io.github.pangju666.framework.http.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * 远程服务错误信息构建器
 * <p>
 * 用于构建{@link RemoteServiceError}实例的构建器类。采用流式接口设计，
 * 支持链式调用，使错误信息的构建更加灵活和易读。
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * RemoteServiceError error = new RemoteServiceErrorBuilder("鉴权服务", "获取用户信息")
 *     .uri(URI.create("http://example.com/api/users"))
 *     .message("用户不存在：{0}", userId)
 *     .code(404)
 *     .httpStatus(HttpStatus.NOT_FOUND.value())
 *     .build();
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see RemoteServiceError
 * @since 1.0.0
 */
public class RemoteServiceErrorBuilder {
	private final String service;
	private final String api;
	private URI uri;
	private String message;
	private String code;
	private int httpStatus = HttpStatus.OK.value();

	/**
	 * 创建构建器实例
	 *
	 * @param service 远程服务名称
	 * @param api     API接口名称或路径
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder(String service, String api) {
		this.service = service;
		this.api = api;
	}

	/**
	 * 创建构建器实例，并指定URI
	 *
	 * @param service 远程服务名称
	 * @param api    API接口名称或路径
	 * @param uri    请求URI
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder(String service, String api, URI uri) {
		this.service = service;
		this.api = api;
		this.uri = uri;
	}

	/**
	 * 设置请求URI
	 *
	 * @param uri 请求URI
	 * @return 当前构建器实例
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder uri(URI uri) {
		this.uri = uri;
		return this;
	}

	/**
	 * 设置错误消息
	 *
	 * @param message 错误消息
	 * @return 当前构建器实例
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder message(String message) {
		this.message = message;
		return this;
	}

	/**
	 * 设置带参数的错误消息
	 * <p>
	 * 使用{@link MessageFormat}格式化消息，支持参数替换。
	 * 当pattern为空时，不会更新现有消息。
	 * </p>
	 *
	 * <p>
	 * 使用示例：
	 * <pre>{@code
	 * builder.message("用户{0}不存在，请求ID：{1}", username, requestId);
	 * builder.message("服务{0}暂时不可用", serviceName);
	 * }</pre>
	 * </p>
	 *
	 * @param pattern 消息模板，使用{n}作为参数占位符
	 * @param args    要替换到模板中的参数值
	 * @return 当前构建器实例
	 * @see MessageFormat#format(String, Object...)
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder message(String pattern, Object... args) {
		if (StringUtils.isNotEmpty(pattern)) {
			this.message = MessageFormat.format(pattern, args);
		}
		return this;
	}

	/**
	 * 设置业务错误代码
	 *
	 * @param code 业务错误代码
	 * @return 当前构建器实例
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder code(Integer code) {
		this.code = Objects.toString(code, StringUtils.EMPTY);
		return this;
	}

	/**
	 * 设置业务错误代码
	 *
	 * @param code 业务错误代码
	 * @return 当前构建器实例
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder code(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 设置HTTP状态码
	 *
	 * @param httpStatus HTTP状态码
	 * @return 当前构建器实例
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder httpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	/**
	 * 设置HTTP状态码
	 *
	 * @param httpStatus HTTP状态码
	 * @return 当前构建器实例
	 * @since 1.0.0
	 */
	public RemoteServiceErrorBuilder httpStatus(HttpStatus httpStatus) {
		if (Objects.nonNull(httpStatus)) {
			this.httpStatus = httpStatus.value();
		}
		return this;
	}

	/**
	 * 构建远程服务错误信息实例
	 *
	 * @return 新的{@link RemoteServiceError}实例
	 * @since 1.0.0
	 */
	public RemoteServiceError build() {
		return new RemoteServiceError(service, api, uri, message, code, httpStatus);
	}
}
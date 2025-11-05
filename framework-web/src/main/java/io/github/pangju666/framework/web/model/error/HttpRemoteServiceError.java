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

package io.github.pangju666.framework.web.model.error;

import io.github.pangju666.framework.web.client.JsonResponseErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/**
 * 远程服务错误模型
 * <p>
 * 描述通过远程调用捕获的业务或网关错误的上下文信息，包含：
 * 服务名、接口名/路径、请求 URI、业务码、错误消息与 HTTP 状态码。
 * 一般由 {@link JsonResponseErrorHandler}
 * 在错误判定后构造，并随统一异常一并抛出，便于上层消费与日志定位。
 * </p>
 *
 * <p>
 * 异常映射：
 * <ul>
 *   <li>网关超时：{@link io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException}</li>
 *   <li>其他错误：{@link io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException}</li>
 * </ul>
 * </p>
 *
 * <p>使用示例</p>
 * <pre>{@code
 * HttpRemoteServiceError error = new HttpRemoteServiceError.Builder("用户服务", "创建用户")
 * 	.url("http://api.example.com/users")
 *     .code("INVALID_PARAM")
 *     .message("参数不合法")
 *     .httpStatus(HttpStatus.BAD_REQUEST)
 *     .build();
 * }
 * </pre>
 *
 * @author pangju666
 * @since 1.0.0
 * @see JsonResponseErrorHandler
 * @see io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException
 * @see io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException
 */
public class HttpRemoteServiceError {
	/**
	 * 远程服务名称（如：用户服务、订单服务）
	 *
	 * @since 1.0.0
	 */
	private String service;
	/**
	 * 接口名称或路径（如：创建用户、/api/users）
	 *
	 * @since 1.0.0
	 */
	private String api;
	/**
	 * 请求的完整 URL
	 *
	 * @since 1.0.0
	 */
	private URI url;
	/**
	 * 业务错误码（字符串表示，来源于响应体中的业务码字段）
	 *
	 * @since 1.0.0
	 */
	private String code;
	/**
	 * 错误消息（来源于响应体中的错误消息字段，可能为空）
	 *
	 * @since 1.0.0
	 */
	private String message;
	/**
	 * HTTP 状态码（默认 {@link HttpStatus#OK}，由响应状态覆盖）
	 *
	 * @since 1.0.0
	 */
	private HttpStatusCode httpStatus = HttpStatus.OK;

	/**
	 * 受保护的无参构造器，供 {@link Builder} 使用
	 *
	 * @since 1.0.0
	 */
	protected HttpRemoteServiceError() {
	}

	/**
	 * 获取远程服务名称
	 *
	 * @return 服务名称
	 * @since 1.0.0
	 */
	public String getService() {
		return service;
	}

	/**
	 * 获取接口名称或路径
	 *
	 * @return 接口名称或路径
	 * @since 1.0.0
	 */
	public String getApi() {
		return api;
	}

	/**
	 * 获取请求的完整 URL
	 *
	 * @return 请求 URL
	 * @since 1.0.0
	 */
	public URI getUrl() {
		return url;
	}

	/**
	 * 获取业务错误码
	 *
	 * @return 错误码字符串
	 * @since 1.0.0
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 获取错误消息
	 *
	 * @return 错误消息（可能为空）
	 * @since 1.0.0
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 获取 HTTP 状态码
	 *
	 * @return HTTP 状态码
	 * @since 1.0.0
	 */
	public HttpStatusCode getHttpStatus() {
		return httpStatus;
	}

	/**
	 * 远程服务错误模型构建器
	 * <p>
	 * 用于分步设置属性并最终创建 {@link HttpRemoteServiceError} 实例；
	 * 可使用无参构造或携带服务名、接口名的构造函数进行初始化。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static class Builder {
		private final HttpRemoteServiceError httpRemoteServiceError = new HttpRemoteServiceError();

		/**
		 * 创建空构建器实例
		 *
		 * @since 1.0.0
		 */
		public Builder() {
		}

		/**
		 * 携带初始服务名、接口名的构造器
		 *
		 * @param service 服务名称
		 * @param api     接口名称或路径
		 * @since 1.0.0
		 */
		public Builder(String service, String api) {
			this.httpRemoteServiceError.service = service;
			this.httpRemoteServiceError.api = api;
		}

		/**
		 * 设置服务名称
		 *
		 * @param service 服务名称
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder service(String service) {
			this.httpRemoteServiceError.service = service;
			return this;
		}

		/**
		 * 设置接口名称或路径
		 *
		 * @param api 接口名称或路径
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder api(String api) {
			this.httpRemoteServiceError.api = api;
			return this;
		}

		/**
		 * 设置请求 URL
		 *
		 * @param url 请求的完整 URL
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder url(URI url) {
			this.httpRemoteServiceError.url = url;
			return this;
		}

		/**
		 * 设置请求 URL（字符串形式）
		 * <p>
		 * 当参数为非空白字符串时，使用 {@link URI#create(String)} 解析并设置；
		 * 否则不做任何处理。
		 * </p>
		 *
		 * @param url 请求的完整 URL 字符串
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder url(String url) {
			if (StringUtils.isNotBlank(url)) {
				this.httpRemoteServiceError.url = URI.create(url);
			}
			return this;
		}

		/**
		 * 设置业务错误码
		 *
		 * @param code 错误码字符串
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder code(String code) {
			this.httpRemoteServiceError.code = code;
			return this;
		}

		/**
		 * 设置业务错误码（整数值）
		 * <p>
		 * 将整数错误码转换为字符串后保存到模型中。
		 * </p>
		 *
		 * @param code 整数形式的业务错误码
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder code(int code) {
			this.httpRemoteServiceError.code = String.valueOf(code);
			return this;
		}

		/**
		 * 设置错误消息
		 *
		 * @param message 错误消息
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder message(String message) {
			this.httpRemoteServiceError.message = message;
			return this;
		}

		/**
		 * 设置 HTTP 状态码
		 *
		 * @param httpStatus HTTP 状态码
		 * @return 构建器自身
		 * @since 1.0.0
		 */
		public Builder httpStatus(HttpStatusCode httpStatus) {
			this.httpRemoteServiceError.httpStatus = httpStatus;
			return this;
		}

		/**
		 * 构建并返回 {@link HttpRemoteServiceError} 实例
		 *
		 * @return 远程服务错误模型
		 * @since 1.0.0
		 */
		public HttpRemoteServiceError build() {
			return httpRemoteServiceError;
		}
	}
}
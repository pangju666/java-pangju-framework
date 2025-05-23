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

import org.springframework.http.HttpStatus;

import java.net.URI;

/**
 * 远程服务错误信息记录类
 * <p>
 * 用于封装远程服务调用过程中的错误信息，包括服务名称、API接口、URI地址、
 * 错误消息、错误代码以及HTTP状态码等信息。
 * </p>
 *
 * @param service    远程服务名称
 * @param api        API接口名称或路径
 * @param uri        完整的请求URI
 * @param message    错误消息
 * @param code       业务错误代码
 * @param httpStatus HTTP状态码
 * @author pangju666
 * @since 1.0.0
 */
public record HttpRemoteServiceError(String service,
									 String api,
									 URI uri,
									 String message,
									 String code,
									 HttpStatus httpStatus) {
	/**
	 * 创建基础错误信息实例
	 * <p>
	 * 仅包含服务标识信息，其他错误相关字段（消息、错误码、HTTP状态码）均为null。
	 * 适用于需要先创建基础信息，后续再补充错误详情的场景。
	 * </p>
	 *
	 * @param service 远程服务名称
	 * @param api     API接口名称或路径
	 * @param uri     完整的请求URI
	 * @since 1.0.0
	 */
	public HttpRemoteServiceError(String service, String api, URI uri) {
		this(service, api, uri, null, null, null);
	}

	/**
	 * 创建一个新的错误信息实例，仅更改错误消息
	 * <p>
	 * 此方法用于在保持其他属性不变的情况下，创建一个具有新错误消息的实例。
	 * </p>
	 *
	 * @param message 新的错误消息
	 * @return 新的错误信息实例
	 * @since 1.0.0
	 */
	public HttpRemoteServiceError clone(String message) {
		return new HttpRemoteServiceError(service, api, uri, message, code, httpStatus);
	}
}
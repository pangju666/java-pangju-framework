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

package io.github.pangju666.framework.http.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.http.exception.RemoteServiceException;
import io.github.pangju666.framework.http.exception.RemoteServiceTimeoutException;
import io.github.pangju666.framework.http.model.RemoteServiceError;
import io.github.pangju666.framework.http.model.RemoteServiceErrorBuilder;
import io.github.pangju666.framework.web.exception.base.ServerException;
import io.github.pangju666.framework.web.lang.pool.WebConstants;
import io.github.pangju666.framework.web.model.vo.Result;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtils.class);

	protected HttpRequestUtils() {
	}

	public static InputStream getDownloadStream(final RestClient restClient, final URI uri,
												@Nullable final MultiValueMap<String, String> headers,
												@Nullable final MultiValueMap<String, String> queryParams,
												@Nullable final Map<String, ?> uriVariables) throws RestClientResponseException {

		return buildRequest(restClient, uri, HttpMethod.GET, headers, queryParams, uriVariables, null)
			.retrieve()
			.body(InputStream.class);
	}

	public static InputStream postDownloadStream(final RestClient restClient, final URI uri,
												 @Nullable final MultiValueMap<String, String> headers,
												 @Nullable final MultiValueMap<String, String> queryParams,
												 @Nullable final Map<String, ?> uriVariables,
												 @Nullable final Object requestBody) throws RestClientResponseException {

		return buildRequest(restClient, uri, HttpMethod.POST, headers, queryParams, uriVariables, requestBody)
			.retrieve()
			.body(InputStream.class);
	}

	/**
	 * 执行POST方式的文件下载请求
	 *
	 * @param restClient  RestClient实例
	 * @param uri         请求URI
	 * @param headers     请求头信息，可为null
	 * @param requestBody 请求体，可为null
	 * @return 下载文件的字节数组
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static byte[] postDownload(final RestClient restClient, final URI uri,
									  @Nullable final MultiValueMap<String, String> headers,
									  @Nullable final MultiValueMap<String, String> queryParams,
									  @Nullable final Map<String, ?> uriVariables,
									  @Nullable final Object requestBody) throws RestClientResponseException {
		return buildRequest(restClient, uri, HttpMethod.POST, headers, queryParams, uriVariables, requestBody)
			.retrieve()
			.body(byte[].class);
	}

	/**
	 * 执行GET方式的文件下载请求
	 *
	 * @param restClient RestClient实例
	 * @param uri        请求URI
	 * @param headers    请求头信息，可为null
	 * @return 下载文件的字节数组
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static byte[] getDownload(final RestClient restClient, final URI uri,
									 @Nullable final MultiValueMap<String, String> headers,
									 @Nullable final MultiValueMap<String, String> queryParams,
									 @Nullable final Map<String, ?> uriVariables) throws RestClientResponseException {

		return buildRequest(restClient, uri, HttpMethod.GET, headers, queryParams, uriVariables, null)
			.retrieve()
			.body(byte[].class);
	}

	/**
	 * 执行GET请求并返回指定类型的响应
	 *
	 * @param restClient    RestClient实例
	 * @param uri           请求URI
	 * @param headers       请求头信息，可为null
	 * @param typeReference 响应类型引用
	 * @return 指定类型的响应对象
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> T get(final RestClient restClient, final URI uri,
							@Nullable final MultiValueMap<String, String> headers,
							@Nullable final MultiValueMap<String, String> queryParams,
							@Nullable final Map<String, ?> uriVariables,
							final ParameterizedTypeReference<T> typeReference) throws RestClientResponseException {
		return buildRequest(restClient, uri, HttpMethod.GET, headers, queryParams, uriVariables, null)
			.accept(MediaType.APPLICATION_JSON)
			.acceptCharset(StandardCharsets.UTF_8)
			.retrieve()
			.toEntity(typeReference)
			.getBody();
	}


	/**
	 * 执行POST请求并返回指定类型的响应
	 *
	 * @param restClient    RestClient实例
	 * @param uri           请求URI
	 * @param headers       请求头信息，可为null
	 * @param requestBody   请求体，可为null
	 * @param typeReference 响应类型引用
	 * @return 指定类型的响应对象
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> T post(final RestClient restClient, final URI uri,
							 @Nullable final MultiValueMap<String, String> headers,
							 @Nullable final MultiValueMap<String, String> queryParams,
							 @Nullable final Map<String, ?> uriVariables,
							 @Nullable final Object requestBody,
							 final ParameterizedTypeReference<T> typeReference) throws RestClientResponseException {
		return buildRequest(restClient, uri, HttpMethod.POST, headers, queryParams, uriVariables, requestBody)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(typeReference)
			.getBody();
	}

	public static <T> T request(final RestClient restClient, final URI uri, final HttpMethod httpMethod,
								@Nullable final MultiValueMap<String, String> headers,
								@Nullable final MultiValueMap<String, String> queryParams,
								@Nullable final Map<String, ?> uriVariables,
								@Nullable final Object requestBody,
								final ParameterizedTypeReference<T> typeReference) throws RestClientResponseException {
		return buildRequest(restClient, uri, httpMethod, headers, queryParams, uriVariables, requestBody)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(typeReference)
			.getBody();
	}

	/**
	 * 从Result中获取数据，默认抛出异常
	 * <p>
	 * 这是{@link #getResultData(String, String, URI, boolean, Result)}方法的便捷重载，
	 * 默认将throwError参数设置为true。当结果码不为成功时，将抛出异常。
	 * </p>
	 *
	 * @param service 服务名称
	 * @param api     接口名称
	 * @param uri     请求URI
	 * @param result  响应结果
	 * @return 包含数据的Optional对象
	 * @throws RemoteServiceException 当结果码不为成功时抛出
	 * @see #getResultData(String, String, URI, boolean, Result)
	 * @since 1.0.0
	 */
	public static <T> Optional<T> getResultData(final String service, final String api, final URI uri, final Result<T> result) {
		return getResultData(service, api, uri, true, result);
	}

	/**
	 * 从Result中获取数据，可配置是否抛出异常
	 *
	 * @param service    服务名称
	 * @param api        接口名称
	 * @param uri        请求URI
	 * @param throwError 是否抛出异常
	 * @param result     响应结果
	 * @return 包含数据的Optional对象
	 * @throws RemoteServiceException 当throwError为true且结果码不为成功时抛出
	 * @since 1.0.0
	 */
	public static <T> Optional<T> getResultData(final String service, final String api, final URI uri,
												final boolean throwError, final Result<T> result) {
		if (result.code() == WebConstants.SUCCESS_CODE) {
			return Optional.ofNullable(result.data());
		}

		RemoteServiceError remoteServiceError = new RemoteServiceErrorBuilder(service, api, uri)
			.code(result.code())
			.message(result.message())
			.build();
		RemoteServiceException remoteServiceException = new RemoteServiceException(remoteServiceError);
		if (throwError) {
			throw remoteServiceException;
		} else {
			remoteServiceException.log(LOGGER);
			return Optional.empty();
		}
	}

	/**
	 * 处理RestClient异常
	 * <p>
	 * 根据异常类型和响应内容构建相应的业务异常：
	 * <ul>
	 *     <li>网关超时异常转换为{@link RemoteServiceTimeoutException}</li>
	 *     <li>其他异常转换为{@link RemoteServiceException}</li>
	 * </ul>
	 * </p>
	 *
	 * @param exception              RestClient异常
	 * @param builder                远程服务错误构建器
	 * @param errorCodeMemberName    错误码字段名
	 * @param errorMessageMemberName 错误消息字段名
	 * @throws RemoteServiceTimeoutException 当发生网关超时时抛出
	 * @throws RemoteServiceException        当发生其他远程服务错误时抛出
	 * @throws ServerException               当响应体解析失败时抛出
	 * @since 1.0.0
	 */
	public static void handleError(final RestClientException exception, final RemoteServiceErrorBuilder builder,
								   final String errorCodeMemberName, final String errorMessageMemberName) {
		if (exception instanceof HttpServerErrorException.GatewayTimeout) {
			throw new RemoteServiceTimeoutException(builder.build());
		}
		if (exception instanceof RestClientResponseException responseException) {
			try {
				builder.httpStatus(responseException.getStatusCode().value());
				JsonObject response = JsonUtils.parseString(responseException.getResponseBodyAsString()).getAsJsonObject();

				String message = response.getAsJsonPrimitive(errorMessageMemberName).getAsString();
				builder.message(message);

				JsonPrimitive code = response.getAsJsonPrimitive(errorCodeMemberName);
				if (code.isString()) {
					builder.code(code.getAsString());
				} else if (code.isBoolean()) {
					builder.code(BooleanUtils.toStringTrueFalse(code.getAsBoolean()));
				} else if (code.isNumber()) {
					builder.code(String.valueOf(code.getAsInt()));
				}
			} catch (JsonParseException e) {
				throw new ServerException("接口响应体解析失败", e);
			}
		}
		throw new RemoteServiceException(builder.build());
	}

	protected static RestClient.RequestBodySpec buildRequest(final RestClient restClient, final URI uri,
															 final HttpMethod httpMethod,
															 @Nullable final MultiValueMap<String, String> headers,
															 @Nullable final MultiValueMap<String, String> queryParams,
															 @Nullable final Map<String, ?> uriVariables,
															 @Nullable final Object requestBody) {
		URI requestUri = uri;
		if (Objects.nonNull(queryParams)) {
			requestUri = UriComponentsBuilder.fromUri(uri)
				.queryParams(queryParams)
				.encode()
				.build(Objects.isNull(uriVariables) ? Collections.emptyMap() : uriVariables);
		}
		RestClient.RequestBodySpec requestBodySpec = restClient
			.method(httpMethod)
			.uri(requestUri);
		if (Objects.nonNull(headers)) {
			requestBodySpec.headers(httpHeaders -> httpHeaders.addAll(headers));
		}
		if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
			requestBodySpec.contentType(MediaType.APPLICATION_JSON)
				.body(Constants.EMPTY_JSON_OBJECT_STR);
		} else {
			requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED);
		}
		if (Objects.nonNull(requestBody)) {
			if (requestBody instanceof JsonObject jsonBody) {
				requestBodySpec.body(jsonBody.toString());
			} else {
				requestBodySpec.body(JsonUtils.toString(requestBody));
			}
		}
		return requestBodySpec;
	}
}
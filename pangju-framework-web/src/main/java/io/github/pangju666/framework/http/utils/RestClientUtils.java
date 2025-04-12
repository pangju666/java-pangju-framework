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

import com.google.gson.JsonElement;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.framework.web.model.vo.Result;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * RestClient工具类
 * <p>
 * 提供基于Spring RestClient的HTTP请求工具方法，包括：
 * <ul>
 *     <li>响应实体转换</li>
 *     <li>请求规范构建</li>
 *     <li>统一的请求配置</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RestClientUtils {
	public static final Set<HttpMethod> SUPPORT_REQUEST_BODY_METHODS = Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

	protected RestClientUtils() {
	}

	/**
	 * 将请求响应转换为输入流实体，使用默认的二进制流媒体类型
	 *
	 * @param requestBodySpec 请求规范
	 * @return 包含输入流的响应实体
	 * @throws IllegalArgumentException    当requestBodySpec为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static ResponseEntity<InputStream> toInputStreamEntity(final RestClient.RequestBodySpec requestBodySpec) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");

		return requestBodySpec
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.toEntity(InputStream.class);
	}

	/**
	 * 将请求响应转换为输入流实体，使用指定的媒体类型
	 *
	 * @param requestBodySpec      请求规范
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @return 包含输入流的响应实体
	 * @throws IllegalArgumentException    当任何参数为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static ResponseEntity<InputStream> toInputStreamEntity(final RestClient.RequestBodySpec requestBodySpec,
																  final MediaType acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");
		Assert.notNull(acceptableMediaTypes, "acceptableMediaTypes 不可为null");

		return requestBodySpec
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(InputStream.class);
	}

	/**
	 * 将请求响应转换为字节数组实体，使用默认的二进制流媒体类型
	 *
	 * @param requestBodySpec 请求规范
	 * @return 包含字节数组的响应实体
	 * @throws IllegalArgumentException    当requestBodySpec为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static ResponseEntity<byte[]> toBytesEntity(final RestClient.RequestBodySpec requestBodySpec) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");

		return requestBodySpec
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.toEntity(byte[].class);
	}

	/**
	 * 将请求响应转换为字节数组实体，使用指定的媒体类型
	 *
	 * @param requestBodySpec      请求规范
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @return 包含字节数组的响应实体
	 * @throws IllegalArgumentException    当任何参数为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static ResponseEntity<byte[]> toBytesEntity(final RestClient.RequestBodySpec requestBodySpec,
													   final MediaType acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");
		Assert.notNull(acceptableMediaTypes, "acceptableMediaTypes 不可为null");

		return requestBodySpec
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(byte[].class);
	}

	/**
	 * 将请求响应转换为Result包装的实体
	 *
	 * @param requestBodySpec 请求规范
	 * @return 包含Result的响应实体
	 * @throws IllegalArgumentException    当requestBodySpec为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> ResponseEntity<Result<T>> toResultEntity(final RestClient.RequestBodySpec requestBodySpec) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");

		return requestBodySpec
			.accept(MediaType.APPLICATION_JSON)
			.acceptCharset(StandardCharsets.UTF_8)
			.retrieve()
			.toEntity(new ParameterizedTypeReference<Result<T>>() {
			});
	}

	/**
	 * 将请求响应转换为指定类型的JSON实体
	 * <p>
	 * 使用APPLICATION_JSON媒体类型，将响应内容转换为指定的Java类型。
	 * 适用于已知具体类型的响应转换。
	 * </p>
	 *
	 * @param requestBodySpec 请求规范
	 * @param bodyType        响应体目标类型
	 * @return 指定类型的响应实体
	 * @throws IllegalArgumentException    当任何参数为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> ResponseEntity<T> toJSONEntity(final RestClient.RequestBodySpec requestBodySpec,
													 final Class<T> bodyType) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");
		Assert.notNull(bodyType, "bodyType 不可为null");

		return requestBodySpec
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 将请求响应转换为参数化类型的JSON实体
	 * <p>
	 * 使用APPLICATION_JSON媒体类型，将响应内容转换为参数化类型。
	 * 适用于泛型类型的响应转换，如List&lt;User&gt;等。
	 * </p>
	 *
	 * @param requestBodySpec 请求规范
	 * @param bodyType        响应体参数化类型引用
	 * @return 参数化类型的响应实体
	 * @throws IllegalArgumentException    当任何参数为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> ResponseEntity<T> toJSONEntity(final RestClient.RequestBodySpec requestBodySpec,
													 final ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");
		Assert.notNull(bodyType, "bodyType 不可为null");

		return requestBodySpec
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 将请求响应转换为指定类型的实体，使用指定的媒体类型
	 *
	 * @param requestBodySpec      请求规范
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @param bodyType             响应体类型
	 * @return 指定类型的响应实体
	 * @throws IllegalArgumentException    当任何参数为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> ResponseEntity<T> toEntity(final RestClient.RequestBodySpec requestBodySpec,
												 final MediaType acceptableMediaTypes,
												 final Class<T> bodyType) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");
		Assert.notNull(acceptableMediaTypes, "acceptableMediaTypes 不可为null");
		Assert.notNull(bodyType, "bodyType 不可为null");

		return requestBodySpec
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 将请求响应转换为参数化类型的实体，使用指定的媒体类型
	 * <p>
	 * 支持复杂的泛型类型转换，如List&lt;User&gt;等。允许自定义接受的媒体类型，
	 * 适用于需要特定媒体类型处理的场景。
	 * </p>
	 *
	 * @param requestBodySpec      请求规范
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @param bodyType             响应体参数化类型引用
	 * @return 参数化类型的响应实体
	 * @throws IllegalArgumentException    当任何参数为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static <T> ResponseEntity<T> toEntity(final RestClient.RequestBodySpec requestBodySpec,
												 final MediaType acceptableMediaTypes,
												 final ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");
		Assert.notNull(acceptableMediaTypes, "acceptableMediaTypes 不可为null");
		Assert.notNull(bodyType, "bodyType 不可为null");

		return requestBodySpec
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 执行无响应体的请求
	 * <p>
	 * 用于处理不需要响应体的HTTP请求，如DELETE操作。
	 * 仅验证请求是否成功，不返回任何响应数据。
	 * </p>
	 *
	 * @param requestBodySpec 请求规范
	 * @throws IllegalArgumentException    当requestBodySpec为null时抛出
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public static void toBodilessEntity(final RestClient.RequestBodySpec requestBodySpec) throws RestClientResponseException {
		Assert.notNull(requestBodySpec, "requestBodySpec 不可为null");

		requestBodySpec.retrieve().toBodilessEntity();
	}

	/**
	 * 构建HTTP请求规范
	 * <p>
	 * 根据提供的参数构建完整的RestClient请求规范，支持以下功能：
	 * <ul>
	 *     <li>查询参数处理：自动编码并添加到URI中</li>
	 *     <li>URI变量替换：支持路径参数替换</li>
	 *     <li>请求头设置：可选的自定义请求头</li>
	 *     <li>请求体处理：
	 *         <ul>
	 *             <li>支持POST/PUT/PATCH请求使用{@code requestBody}</li>
	 *             <li>其他请求使用{@code application/x-www-form-urlencode}格式</li>
	 *             <li>当请求体中包含{@code File}或{@code byte[]}时，自动转换为multipart/form-data格式</li>
	 *             <li>支持Map、JsonElement等多种类型的请求体处理</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 * </p>
	 *
	 * @param restClient   RestClient实例
	 * @param uri          请求URI
	 * @param httpMethod   HTTP请求方法
	 * @param headers      请求头信息（可选）
	 * @param queryParams  查询参数（可选）
	 * @param uriVariables URI变量映射（可选）
	 * @param requestBody  请求体对象（可选），支持Map中包含File对象
	 * @return 构建好的请求规范
	 * @throws IllegalArgumentException 当restClient、httpMethod或uri为null时抛出
	 * @see #addRequestBody(RestClient.RequestBodySpec, Object) 请求体处理的具体实现
	 * @since 1.0.0
	 */
	public static RestClient.RequestBodySpec buildRequestBodySpec(final RestClient restClient, final URI uri,
																  final HttpMethod httpMethod,
																  @Nullable final MultiValueMap<String, String> headers,
																  @Nullable final MultiValueMap<String, String> queryParams,
																  @Nullable final Map<String, ?> uriVariables,
																  @Nullable final Object requestBody) {
		Assert.notNull(restClient, "restClient 不可为null");
		Assert.notNull(httpMethod, "httpMethod 不可为null");
		Assert.notNull(uri, "uri 不可为null");

		RestClient.RequestBodyUriSpec requestBodySpec = restClient.method(httpMethod);
		if (!CollectionUtils.isEmpty(queryParams)) {
			URI requestUri = UriComponentsBuilder.fromUri(uri)
				.queryParams(queryParams)
				.encode()
				.build(CollectionUtils.isEmpty(uriVariables) ? Collections.emptyMap() : uriVariables);
			requestBodySpec.uri(requestUri);
		} else if (!CollectionUtils.isEmpty(uriVariables)) {
			requestBodySpec.uri(uri.toString(), uriVariables);
		}
		if (Objects.nonNull(headers)) {
			requestBodySpec.headers(httpHeaders -> httpHeaders.addAll(headers));
		}
		if (SUPPORT_REQUEST_BODY_METHODS.contains(httpMethod)) {
			requestBodySpec
				.contentType(MediaType.APPLICATION_JSON)
				.body(Constants.EMPTY_JSON_OBJECT_STR);

			if (Objects.nonNull(requestBody)) {
				addRequestBody(requestBodySpec, requestBody);
			}
		} else {
			requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED);
		}
		return requestBodySpec;
	}

	/**
	 * 添加请求体到请求规范中
	 * <p>
	 * 根据请求体对象的类型进行不同的处理：
	 * <ul>
	 *     <li>Map类型：
	 *         <ul>
	 *             <li>包含File对象或byte[]时自动转换为multipart/form-data格式</li>
	 *             <li>不包含File对象或byte[]时直接作为请求体</li>
	 *         </ul>
	 *     </li>
	 *     <li>{@link JsonElement}类型：转换为JSON字符串</li>
	 *     <li>其他类型：直接作为请求体</li>
	 * </ul>
	 * </p>
	 *
	 * @param requestBodySpec 请求规范
	 * @param requestBody     请求体对象
	 * @since 1.0.0
	 */
	protected static void addRequestBody(RestClient.RequestBodySpec requestBodySpec, Object requestBody) {
		if (requestBody instanceof Map<?, ?> map) {
			boolean flag = false;
			MultipartBodyBuilder builder = new MultipartBodyBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (Objects.nonNull(entry.getValue())) {
					if (entry.getValue() instanceof File file) {
						builder.part(entry.getKey().toString(), file);
						flag = true;
					} else if (entry.getValue() instanceof byte[] bytes) {
						builder.part(entry.getKey().toString(), bytes);
						flag = true;
					} else {
						builder.part(entry.getKey().toString(), entry.getValue().toString());
					}
				}
			}
			if (flag) {
				requestBodySpec
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(builder.build());
			} else {
				requestBodySpec.body(requestBody);
			}
		} else if (requestBody instanceof JsonElement jsonElement) {
			requestBodySpec.body(jsonElement.toString());
		} else {
			requestBodySpec.body(requestBody);
		}
	}
}
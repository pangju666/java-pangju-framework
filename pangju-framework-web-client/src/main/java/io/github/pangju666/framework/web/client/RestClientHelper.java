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

package io.github.pangju666.framework.web.client;

import com.google.gson.JsonElement;
import io.github.pangju666.commons.lang.pool.Constants;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * RestClient辅助类
 * <p>
 * 提供流式API风格的HTTP请求构建器，支持以下功能：
 * <ul>
 *     <li>URI构建：支持路径、查询参数、URI变量的设置</li>
 *     <li>请求头管理：支持单个或批量添加请求头</li>
 *     <li>请求体处理：
 *         <ul>
 *             <li>自动处理文件上传（File、Path、byte[]类型）</li>
 *             <li>支持JSON格式的请求体</li>
 *             <li>支持表单数据提交</li>
 *         </ul>
 *     </li>
 *     <li>响应处理：支持多种响应类型的转换</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RestClientHelper {
	public static final Set<HttpMethod> SUPPORT_REQUEST_BODY_METHODS = Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

	private final RestClient restClient;
	private final UriComponentsBuilder uriComponentsBuilder;
	private final HttpHeaders headers = new HttpHeaders();
	private final Map<String, Object> uriVariables = new HashMap<>(10);

	private HttpMethod method = HttpMethod.GET;
	private MediaType contentType = MediaType.APPLICATION_FORM_URLENCODED;
	private Object body = null;

	protected RestClientHelper(RestClient restClient, UriComponentsBuilder uriComponentsBuilder) {
		this.restClient = restClient;
		this.uriComponentsBuilder = uriComponentsBuilder;
	}

	public static RestClientHelper fromUriString(RestClient restClient, String uriString) {
		Assert.notNull(restClient, "restClient 不可为null");

		if (StringUtils.isNotBlank(uriString)) {
			return new RestClientHelper(restClient, UriComponentsBuilder.fromUriString(uriString));
		} else {
			return new RestClientHelper(restClient, UriComponentsBuilder.newInstance());
		}
	}

	public static RestClientHelper fromUri(RestClient restClient, URI uri) {
		Assert.notNull(restClient, "restClient 不可为null");

		if (Objects.nonNull(uri)) {
			return new RestClientHelper(restClient, UriComponentsBuilder.fromUri(uri));
		} else {
			return new RestClientHelper(restClient, UriComponentsBuilder.newInstance());
		}
	}

	/**
	 * 设置HTTP请求方法
	 *
	 * @param method HTTP方法
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestClientHelper method(HttpMethod method) {
		if (Objects.nonNull(method)) {
			this.method = method;
		}
		return this;
	}

	/**
	 * 添加请求路径
	 *
	 * @param path 请求路径
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestClientHelper path(String path) {
		if (StringUtils.isNotBlank(path)) {
			this.uriComponentsBuilder.path(path);
		}
		return this;
	}

	/**
	 * 添加单个查询参数
	 *
	 * @param name   参数名
	 * @param value 参数值
	 * @return 当前实例
	 * @throws IllegalArgumentException 当name为空时抛出
	 * @since 1.0.0
	 */
	public RestClientHelper queryParam(String name, @Nullable Object value) {
		Assert.hasText(name, "name 不可为空");

		this.uriComponentsBuilder.queryParam(name, value);
		return this;
	}

	/**
	 * 批量添加查询参数
	 *
	 * @param params 参数映射
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestClientHelper queryParams(@Nullable Map<String, Object> params) {
		if (Objects.nonNull(params) && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				this.uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public RestClientHelper uriVariable(String name, @Nullable Object value) {
		Assert.hasText(name, "name 不可为空");

		if (Objects.nonNull(value)) {
			this.uriVariables.put(name, value);
		}
		return this;
	}

	/**
	 * 设置URI变量
	 *
	 * @param uriVariables URI变量映射
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestClientHelper uriVariables(@Nullable Map<String, Object> uriVariables) {
		if (Objects.nonNull(uriVariables) && !uriVariables.isEmpty()) {
			this.uriVariables.putAll(uriVariables);
		}
		return this;
	}

	/**
	 * 添加单个请求头
	 *
	 * @param headerName  请求头名称
	 * @param headerValue 请求头值
	 * @return 当前实例
	 * @throws IllegalArgumentException 当headerName为空时抛出
	 * @since 1.0.0
	 */
	public RestClientHelper header(String headerName, @Nullable String headerValue) {
		Assert.hasText(headerName, "name 不可为空");

		this.headers.add(headerName, headerValue);
		return this;
	}

	/**
	 * 批量添加请求头
	 *
	 * @param headers 请求头映射
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestClientHelper headers(@Nullable MultiValueMap<String, String> headers) {
		if (Objects.nonNull(headers) && !headers.isEmpty()) {
			this.headers.addAll(headers);
		}
		return this;
	}

	public RestClientHelper jsonBody(@Nullable Object body) {
		this.contentType = MediaType.APPLICATION_JSON;
		if (Objects.isNull(body)) {
			this.body = Constants.EMPTY_JSON_OBJECT_STR;
		} else {
			if (body instanceof JsonElement jsonElement) {
				this.body = jsonElement.toString();
			} else {
				this.body = body;
			}
		}
		return this;
	}

	public RestClientHelper xmlBody(@Nullable Object body) {
		this.contentType = MediaType.APPLICATION_XML;
		if (Objects.nonNull(body) && Objects.nonNull(body.getClass().getAnnotation(XmlRootElement.class))) {
			this.body = body;
		}
		return this;
	}

	public RestClientHelper textBody(@Nullable String body) {
		this.contentType = MediaType.TEXT_PLAIN;
		if (Objects.nonNull(body)) {
			this.body = body;
		}
		return this;
	}

	public RestClientHelper bytesBody(@Nullable byte[] body) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		if (Objects.nonNull(body)) {
			this.body = body;
		}
		return this;
	}

	public RestClientHelper fromDataBody(Object body) {
		if (body instanceof Map<?, ?> map) {
			MultipartBodyBuilder builder = new MultipartBodyBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (Objects.nonNull(entry.getValue())) {
					if (entry.getValue() instanceof File file) {
						builder.part(entry.getKey().toString(), file);
					}
					if (entry.getValue() instanceof Path path) {
						builder.part(entry.getKey().toString(), path.toFile());
					} else if (entry.getValue() instanceof byte[] bytes) {
						builder.part(entry.getKey().toString(), bytes);
					} else {
						builder.part(entry.getKey().toString(), entry.getValue().toString());
					}
				}
			}
			this.contentType = MediaType.MULTIPART_FORM_DATA;
			this.body = builder.build();
		} else {
			this.body = body;
		}
		return this;
	}

	/**
	 * 将响应转换为输入流实体
	 *
	 * @return 输入流响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public ResponseEntity<InputStream> toInputStreamEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.toEntity(InputStream.class);
	}

	/**
	 * 将响应转换为输入流实体，使用指定的媒体类型
	 *
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @return 输入流响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public ResponseEntity<InputStream> toInputStreamEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(InputStream.class);
	}

	/**
	 * 将响应转换为字节数组实体
	 *
	 * @return 字节数组响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public ResponseEntity<byte[]> toBytesEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.toEntity(byte[].class);
	}

	/**
	 * 将响应转换为字节数组实体，使用指定的媒体类型
	 *
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @return 字节数组响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public ResponseEntity<byte[]> toBytesEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(byte[].class);
	}

	/**
	 * 将响应转换为指定类型的实体
	 *
	 * @param bodyType 响应体类型
	 * @return 指定类型的响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @throws IllegalArgumentException    当bodyType为null时抛出
	 * @since 1.0.0
	 */
	public <T> ResponseEntity<T> toEntity(Class<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 将响应转换为参数化类型的实体
	 *
	 * @param bodyType 响应体参数化类型
	 * @return 参数化类型的响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @throws IllegalArgumentException    当bodyType为null时抛出
	 * @since 1.0.0
	 */
	public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 将响应转换为指定类型的实体，使用指定的媒体类型
	 *
	 * @param bodyType             响应体类型
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @return 指定类型的响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @throws IllegalArgumentException    当bodyType为null时抛出
	 * @since 1.0.0
	 */
	public <T> ResponseEntity<T> toEntity(Class<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 将响应转换为参数化类型的实体，使用指定的媒体类型
	 *
	 * @param bodyType             响应体参数化类型
	 * @param acceptableMediaTypes 可接受的媒体类型
	 * @return 参数化类型的响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @throws IllegalArgumentException    当bodyType为null时抛出
	 * @since 1.0.0
	 */
	public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	/**
	 * 执行无响应体的请求
	 *
	 * @return 无响应体的响应实体
	 * @throws RestClientResponseException 当请求发生错误时抛出
	 * @since 1.0.0
	 */
	public ResponseEntity<Void> toBodilessEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toBodilessEntity();
	}

	protected RestClient.RequestBodySpec buildRequestBodySpec() {
		RestClient.RequestBodySpec requestBodySpec = restClient
			.method(method)
			.uri(uriComponentsBuilder.build(uriVariables))
			.contentType(contentType)
			.headers(httpHeaders -> httpHeaders.addAll(headers));

		if (!SUPPORT_REQUEST_BODY_METHODS.contains(method)) {
			requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED);
		} else {
			if (Objects.nonNull(body)) {
				requestBodySpec
					.contentType(contentType)
					.body(body);
			} else {
				requestBodySpec
					.contentType(MediaType.APPLICATION_JSON)
					.body(Constants.EMPTY_JSON_OBJECT_STR);
			}
		}
		return requestBodySpec;
	}
}
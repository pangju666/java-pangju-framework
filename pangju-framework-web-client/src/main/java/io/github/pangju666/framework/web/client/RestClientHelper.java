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
import io.github.pangju666.commons.io.utils.FileUtils;
import io.github.pangju666.commons.lang.pool.Constants;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class RestClientHelper {
	public static final Set<HttpMethod> SUPPORT_REQUEST_BODY_METHODS = Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

	private final RestClient restClient;
	private final UriComponentsBuilder uriComponentsBuilder;
	private final MultipartBodyBuilder formDataBuilder = new MultipartBodyBuilder();
	private final HttpHeaders headers = new HttpHeaders();
	private final Map<String, Object> uriVariables = new HashMap<>(4);

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

	public RestClientHelper queryParam(String name, @Nullable Object... values) {
		Assert.hasText(name, "name 不可为空");

		this.uriComponentsBuilder.queryParam(name, values);
		return this;
	}

	public RestClientHelper query(@Nullable String query) {
		this.uriComponentsBuilder.query(query);
		return this;
	}

	public RestClientHelper queryParams(@Nullable MultiValueMap<String, String> params) {
		this.uriComponentsBuilder.queryParams(params);
		return this;
	}

	public RestClientHelper queryParams(@Nullable Map<String, Object> params) {
		if (!CollectionUtils.isEmpty(params)) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				this.uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public RestClientHelper queryParamIfPresent(String name, Optional<?> value) {
		Assert.hasText(name, "name 不可为空");
		Assert.notNull(value, "value 不可为空");

		this.uriComponentsBuilder.queryParamIfPresent(name, value);
		return this;
	}

	public RestClientHelper uriVariable(String name, @Nullable Object value) {
		Assert.hasText(name, "name 不可为空");

		if (Objects.nonNull(value)) {
			this.uriVariables.put(name, value);
		}
		return this;
	}

	public RestClientHelper uriVariables(@Nullable Map<String, Object> uriVariables) {
		if (!CollectionUtils.isEmpty(uriVariables)) {
			this.uriVariables.putAll(uriVariables);
		}
		return this;
	}

	public RestClientHelper header(String headerName, @Nullable Object headerValue) {
		Assert.hasText(headerName, "headerName 不可为空");

		this.headers.add(headerName, Objects.toString(headerValue, null));
		return this;
	}

	public RestClientHelper headers(String key, @Nullable List<?> values) {
		if (!CollectionUtils.isEmpty(values)) {
			this.headers.addAll(key, values.stream()
				.map(value -> Objects.toString(value, null))
				.toList()
			);
		}
		return this;
	}

	public RestClientHelper headers(@Nullable MultiValueMap<String, String> headers) {
		if (!CollectionUtils.isEmpty(headers)) {
			this.headers.addAll(headers);
		}
		return this;
	}

	public RestClientHelper jsonBody(@Nullable Object body) {
		return jsonBody(body, true);
	}

	public RestClientHelper jsonBody(@Nullable Object body, boolean emptyIfNull) {
		this.contentType = MediaType.APPLICATION_JSON;
		if (Objects.isNull(body)) {
			this.body = emptyIfNull ? Constants.EMPTY_JSON_OBJECT_STR : null;
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
		if (Objects.nonNull(body) && Objects.isNull(body.getClass().getAnnotation(XmlRootElement.class))) {
			throw new IllegalArgumentException("类型" + body.getClass() + "上未找到XmlRootElement注解");
		}
		this.body = body;
		return this;
	}

	public RestClientHelper textBody(@Nullable Object body) {
		this.contentType = MediaType.TEXT_PLAIN;
		this.body = Objects.toString(body, null);
		return this;
	}

	public RestClientHelper bytesBody(@Nullable byte[] body) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = body;
		return this;
	}

	public RestClientHelper bytesBody(@Nullable InputStream inputStream) throws IOException {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		if (Objects.nonNull(inputStream)) {
			this.body = inputStream.readAllBytes();
		} else {
			this.body = null;
		}
		return this;
	}

	public RestClientHelper bytesBody(@Nullable File file) throws IOException {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		if (Objects.nonNull(file)) {
			FileUtils.checkFile(file, null);
			try (InputStream inputStream = FileUtils.openUnsynchronizedBufferedInputStream(file)) {
				this.body = inputStream.readAllBytes();
			}
		} else {
			this.body = null;
		}
		return this;
	}

	public RestClientHelper fromDataPart(String name, @Nullable Object part) throws MalformedURLException {
		Assert.hasText(name, "name 不可为空");

		this.contentType = MediaType.MULTIPART_FORM_DATA;
		if (Objects.nonNull(part)) {
			if (part instanceof File file) {
				formDataBuilder.part(name, new FileSystemResource(file)).filename(file.getName());
			} else if (part instanceof Path path) {
				formDataBuilder.part(name, new FileSystemResource(path)).filename(path.toFile().getName());
			} else if (part instanceof byte[] bytes) {
				formDataBuilder.part(name, new ByteArrayResource(bytes));
			} else if (part instanceof InputStream inputStream) {
				formDataBuilder.part(name, new InputStreamResource(inputStream));
			} else if (part instanceof URI uri) {
				formDataBuilder.part(name, new UrlResource(uri));
			} else if (part instanceof URL url) {
				formDataBuilder.part(name, new UrlResource(url));
			} else {
				formDataBuilder.part(name, part);
			}
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
			if (contentType == MediaType.MULTIPART_FORM_DATA) {
				requestBodySpec
					.contentType(contentType)
					.body(formDataBuilder.build());
			} else {
				requestBodySpec
					.contentType(contentType)
					.body(body);
			}
		}

		return requestBodySpec;
	}
}
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
import io.github.pangju666.framework.web.model.vo.Result;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RestClientHelper {
	public static final Set<HttpMethod> SUPPORT_REQUEST_BODY_METHODS = Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

	private final RestClient restClient;
	private final UriComponentsBuilder uriComponentsBuilder;
	private final HttpHeaders headers = new HttpHeaders();

	private HttpMethod method = HttpMethod.GET;
	private MediaType contentType = MediaType.APPLICATION_FORM_URLENCODED;
	private Object body = null;

	public RestClientHelper(RestClient restClient, String uriString) {
		Assert.notNull(restClient, "restClient 不可为null");

		this.restClient = restClient;
		if (StringUtils.isNotBlank(uriString)) {
			this.uriComponentsBuilder = UriComponentsBuilder.fromUriString(uriString);
		} else {
			this.uriComponentsBuilder = UriComponentsBuilder.newInstance();
		}
	}

	public RestClientHelper(RestClient restClient, URI uri) {
		Assert.notNull(restClient, "restClient 不可为null");

		this.restClient = restClient;
		if (Objects.nonNull(uri)) {
			this.uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
		} else {
			this.uriComponentsBuilder = UriComponentsBuilder.newInstance();
		}
	}

	public RestClientHelper method(HttpMethod method) {
		if (Objects.nonNull(method)) {
			this.method = method;
		}
		return this;
	}

	public RestClientHelper path(String path) {
		if (StringUtils.isNotBlank(path)) {
			this.uriComponentsBuilder.path(path);
		}
		return this;
	}

	public RestClientHelper queryParam(String name, @Nullable Object values) {
		Assert.hasText(name, "name 不可为空");

		this.uriComponentsBuilder.queryParam(name, values);
		return this;
	}

	public RestClientHelper queryParams(@Nullable Map<String, Object> params) {
		if (Objects.nonNull(params) && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				this.uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public RestClientHelper uriVariables(@Nullable Map<String, Object> uriVariables) {
		if (Objects.nonNull(uriVariables) && !uriVariables.isEmpty()) {
			this.uriComponentsBuilder.uriVariables(uriVariables);
		}
		return this;
	}

	public RestClientHelper header(String headerName, @Nullable String headerValue) {
		Assert.hasText(headerName, "name 不可为空");

		this.headers.add(headerName, headerValue);
		return this;
	}

	public RestClientHelper headers(@Nullable MultiValueMap<String, String> headers) {
		if (Objects.nonNull(headers) && !headers.isEmpty()) {
			this.headers.addAll(headers);
		}
		return this;
	}

	public RestClientHelper body(@Nullable Object body) {
		this.contentType = MediaType.APPLICATION_JSON;

		if (body instanceof Map<?, ?> map) {
			boolean flag = false;
			MultipartBodyBuilder builder = new MultipartBodyBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (Objects.nonNull(entry.getValue())) {
					if (entry.getValue() instanceof File file) {
						builder.part(entry.getKey().toString(), file);
						flag = true;
					}
					if (entry.getValue() instanceof Path path) {
						builder.part(entry.getKey().toString(), path.toFile());
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
				this.contentType = MediaType.MULTIPART_FORM_DATA;
				this.body = builder.build();
			} else {
				this.body = body;
			}
			return this;
		}

		if (body instanceof JsonElement jsonElement) {
			this.body = jsonElement.toString();
		} else {
			this.body = body;
		}
		return this;
	}

	public ResponseEntity<InputStream> toInputStreamEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.toEntity(InputStream.class);
	}

	public ResponseEntity<InputStream> toInputStreamEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(InputStream.class);
	}

	public ResponseEntity<byte[]> toBytesEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.toEntity(byte[].class);
	}

	public ResponseEntity<byte[]> toBytesEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(byte[].class);
	}

	public <T> ResponseEntity<Result<T>> toResultEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_JSON)
			.acceptCharset(StandardCharsets.UTF_8)
			.retrieve()
			.toEntity(new ParameterizedTypeReference<Result<T>>() {
			});
	}

	public <T> ResponseEntity<T> toEntity(Class<T> bodyType) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(bodyType);
	}

	public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(bodyType);
	}

	public <T> ResponseEntity<T> toEntity(Class<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	public ResponseEntity<Void> toBodilessEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toBodilessEntity();
	}

	protected RestClient.RequestBodySpec buildRequestBodySpec() {
		RestClient.RequestBodySpec requestBodySpec = restClient
			.method(method)
			.uri(uriComponentsBuilder.build().toUri())
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
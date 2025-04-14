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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.*;

public class RestClientHelper {
	public static final Set<MediaType> FORM_MEDIA_TYPES = Set.of(MediaType.APPLICATION_FORM_URLENCODED,
		MediaType.MULTIPART_FORM_DATA, MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED);

	private final RestClient restClient;
	private final UriComponentsBuilder uriComponentsBuilder;

	private final HttpHeaders headers = new HttpHeaders();
	private final Map<String, Object> uriVariables = new HashMap<>(4);
	private final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

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

	public RestClientHelper queryParam(String name, @Nullable Object... values) {
		Assert.hasText(name, "name 不可为空");

		this.uriComponentsBuilder.queryParam(name, values);
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

	public RestClientHelper part(String name, @Nullable Resource value) {
		Assert.hasText(name, "name 不可为空");

		this.contentType = MediaType.MULTIPART_FORM_DATA;
		this.formData.add(name, value);
		return this;
	}

	public RestClientHelper form(String name, @Nullable Object value) {
		Assert.hasText(name, "name 不可为空");

		this.contentType = MediaType.MULTIPART_FORM_DATA;
		this.formData.add(name, value);
		return this;
	}

	public RestClientHelper part(String name, @Nullable Resource value, MediaType mediaType) {
		Assert.hasText(name, "name 不可为空");

		this.contentType = mediaType;
		this.formData.add(name, value);
		return this;
	}

	public RestClientHelper form(String name, @Nullable Object value, MediaType mediaType) {
		Assert.hasText(name, "name 不可为空");

		this.contentType = mediaType;
		this.formData.add(name, value);
		return this;
	}

	public RestClientHelper form(MultiValueMap<String, Object> formData) {
		this.contentType = MediaType.MULTIPART_FORM_DATA;
		this.formData.addAll(formData);
		return this;
	}

	public RestClientHelper form(MultiValueMap<String, Object> formData, MediaType mediaType) {
		this.contentType = mediaType;
		this.formData.addAll(formData);
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

	public RestClientHelper textBody(@Nullable String body) {
		this.contentType = MediaType.TEXT_PLAIN;
		this.body = body;
		return this;
	}

	public RestClientHelper bytesBody(@Nullable byte[] body) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = body;
		return this;
	}

	public RestClientHelper textBody(@Nullable String body, boolean emptyIfNull) {
		this.contentType = MediaType.TEXT_PLAIN;
		this.body = ObjectUtils.defaultIfNull(body, emptyIfNull ? StringUtils.EMPTY : null);
		return this;
	}

	public RestClientHelper bytesBody(@Nullable byte[] body, boolean emptyIfNull) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = ObjectUtils.defaultIfNull(body, emptyIfNull ? ArrayUtils.EMPTY_BYTE_ARRAY : null);
		return this;
	}

	public RestClientHelper body(@Nullable Object body, MediaType mediaType) {
		this.contentType = mediaType;
		this.body = body;
		return this;
	}

	public ResponseEntity<BufferedImage> toBufferedImageEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toEntity(BufferedImage.class);
	}

	public ResponseEntity<BufferedImage> toBufferedImageEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(BufferedImage.class);
	}

	public ResponseEntity<ResourceRegion> toResourceRegionEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toEntity(ResourceRegion.class);
	}

	public ResponseEntity<ResourceRegion> toResourceRegionEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(ResourceRegion.class);
	}

	public ResponseEntity<Resource> toResourceEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toEntity(Resource.class);
	}

	public ResponseEntity<Resource> toResourceEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(Resource.class);
	}

	public ResponseEntity<byte[]> toBytesEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toEntity(byte[].class);
	}

	public ResponseEntity<byte[]> toBytesEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(byte[].class);
	}

	public ResponseEntity<String> toStringEntity() throws RestClientResponseException {
		return buildRequestBodySpec()
			.retrieve()
			.toEntity(String.class);
	}

	public ResponseEntity<String> toStringEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(String.class);
	}

	public <T> ResponseEntity<T> toEntity(Class<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.retrieve()
			.toEntity(bodyType);
	}

	public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.retrieve()
			.toEntity(bodyType);
	}

	public <T> ResponseEntity<T> toEntity(Class<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

		return buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve()
			.toEntity(bodyType);
	}

	public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

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
			.uri(uriComponentsBuilder.build(uriVariables))
			.contentType(contentType)
			.headers(httpHeaders -> httpHeaders.addAll(headers));

		if (FORM_MEDIA_TYPES.contains(contentType)) {
			requestBodySpec
				.contentType(contentType)
				.body(formData);
		} else {
			requestBodySpec
				.contentType(contentType)
				.body(body);
		}

		return requestBodySpec;
	}
}
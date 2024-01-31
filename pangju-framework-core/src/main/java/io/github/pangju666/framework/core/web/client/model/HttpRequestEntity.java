package io.github.pangju666.framework.core.web.client.model;

import io.github.pangju666.framework.core.exception.base.BaseRuntimeException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.function.Function;

public record HttpRequestEntity(
	HttpEntity<?> httpEntity,
	HttpMethod method,
	URI uri,
	Function<RestClientException, ? extends BaseRuntimeException> exceptionCallback) {
	public static HttpRequestEntity newInstance(HttpMethod method, URI uri) {
		return new HttpRequestEntity(HttpEntity.EMPTY, method, uri, null);
	}

	public static HttpRequestEntity newInstance(HttpEntity<?> httpEntity, HttpMethod method, URI uri, Function<RestClientException, ? extends BaseRuntimeException> exceptionCallback) {
		return new HttpRequestEntity(httpEntity, method, uri, exceptionCallback);
	}
}

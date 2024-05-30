package io.github.pangju666.framework.http.utils;

import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.core.exception.base.RemoteServiceException;
import io.github.pangju666.framework.core.exception.remote.RemoteServiceTimeoutException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import io.github.pangju666.framework.web.model.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.Objects;

public class RestClientUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientUtils.class);
	private static final RestClient DEFAULT_CLIENT = RestClient.create();

	protected RestClientUtils() {
	}

	public static <R> R get(String service, String api, String httpUrl,
							MultiValueMap<String, String> headers,
							MultiValueMap<String, String> queryParams) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.GET, headers, null, true);
	}

	public static <R> R get(String service, String api, URI uri, MultiValueMap<String, String> headers) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.GET, headers, null, true);
	}

	public static <R> R get(String service, String api, String httpUrl,
							MultiValueMap<String, String> headers,
							MultiValueMap<String, String> queryParams,
							boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.GET, headers, null, throwError);
	}

	public static <R> R get(String service, String api, URI uri, MultiValueMap<String, String> headers, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.GET, headers, null, throwError);
	}

	public static <T, R> R post(String service, String api, String httpUrl,
								MultiValueMap<String, String> headers,
								MultiValueMap<String, String> queryParams,
								T body) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.POST, headers, body, true);
	}

	public static <T, R> R post(String service, String api, URI uri, MultiValueMap<String, String> headers, T body) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.POST, headers, body, true);
	}

	public static <T, R> R post(String service, String api, String httpUrl,
								MultiValueMap<String, String> queryParams,
								MultiValueMap<String, String> headers,
								T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.POST, headers, body, throwError);
	}

	public static <T, R> R post(String service, String api, URI uri, MultiValueMap<String, String> headers, T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.POST, headers, body, throwError);
	}

	public static <T, R> R put(String service, String api, String httpUrl,
							   MultiValueMap<String, String> headers,
							   MultiValueMap<String, String> queryParams,
							   T body) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PUT, headers, body, true);
	}

	public static <T, R> R put(String service, String api, URI uri, MultiValueMap<String, String> headers, T body) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.PUT, headers, body, true);
	}

	public static <T, R> R put(String service, String api, String httpUrl,
							   MultiValueMap<String, String> queryParams,
							   MultiValueMap<String, String> headers,
							   T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PUT, headers, body, throwError);
	}

	public static <T, R> R put(String service, String api, URI uri, MultiValueMap<String, String> headers, T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.PUT, headers, body, throwError);
	}

	public static <T, R> R delete(String service, String api, String httpUrl,
								  MultiValueMap<String, String> headers,
								  MultiValueMap<String, String> queryParams,
								  T body) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.DELETE, headers, body, true);
	}

	public static <T, R> R delete(String service, String api, URI uri, MultiValueMap<String, String> headers, T body) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.DELETE, headers, body, true);
	}

	public static <T, R> R delete(String service, String api, String httpUrl,
								  MultiValueMap<String, String> queryParams,
								  MultiValueMap<String, String> headers,
								  T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.DELETE, headers, body, throwError);
	}

	public static <T, R> R delete(String service, String api, URI uri, MultiValueMap<String, String> headers, T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, uri, HttpMethod.DELETE, headers, body, throwError);
	}

	public static <T, R> R request(String service, String api, String httpUrl, HttpMethod httpMethod,
								   MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers,
								   T body, boolean throwError) {
		return request(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), httpMethod, headers, body, throwError);
	}

	public static <T, R> R request(RestClient restClient, String service, String api, URI uri, HttpMethod httpMethod,
								   MultiValueMap<String, String> headers, T body, boolean throwError) {
		try {
			RestClient.RequestBodySpec requestBodySpec = restClient
				.method(httpMethod)
				.uri(uri);
			if (Objects.nonNull(headers)) {
				requestBodySpec.headers(httpHeaders -> httpHeaders.addAll(headers));
			}
			if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
				requestBodySpec
					.contentType(MediaType.APPLICATION_JSON)
					.body(ConstantPool.EMPTY_JSON_OBJECT_STR);
			}
			if (Objects.nonNull(body)) {
				requestBodySpec.body(JsonUtils.toString(body));
			}
			String response = requestBodySpec
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(String.class);
			Result<R> result = JsonUtils.fromString(response, new TypeToken<Result<R>>() {
			});
			if (result.code() != ConstantPool.SUCCESS_RESPONSE_CODE) {
				String message = StringUtils.defaultIfBlank(result.message(), "无");
				if (throwError) {
					RemoteServiceException exception = new RemoteServiceException(service, api, message);
					exception.setPath(uri.getPath());
					exception.setResponseCode(result.code());
					exception.setResponseMessage(result.message());
					throw exception;
				} else {
					LOGGER.error("服务：{}，接口：{}，路径：{} 请求失败，错误码：{}，错误信息：{}", service, api, uri, result.code(), message);
					return null;
				}
			}
			return result.data();
		} catch (HttpServerErrorException.GatewayTimeout e) {
			RemoteServiceTimeoutException exception = new RemoteServiceTimeoutException(service, api);
			exception.setPath(uri.getPath());
			throw exception;
		} catch (RestClientResponseException e) {
			Result<Void> result = JsonUtils.fromString(e.getResponseBodyAsString(), new TypeToken<Result<Void>>() {
			});
			RemoteServiceException exception = new RemoteServiceException(service, api);
			exception.setPath(uri.getPath());
			exception.setResponseCode(result.code());
			exception.setResponseMessage(result.message());
			throw exception;
		}
	}
}

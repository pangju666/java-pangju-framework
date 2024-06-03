package io.github.pangju666.framework.http.utils;

import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.core.exception.remote.RemoteServiceException;
import io.github.pangju666.framework.core.exception.remote.RemoteServiceTimeoutException;
import io.github.pangju666.framework.core.exception.remote.model.RemoteServiceError;
import io.github.pangju666.framework.core.exception.remote.model.RemoteServiceErrorBuilder;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import io.github.pangju666.framework.web.model.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.Objects;

public class ResultApiUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResultApiUtils.class);
	private static final RestClient DEFAULT_CLIENT = RestClient.create();

	protected ResultApiUtils() {
	}

	public static <R> R get(String service, String api, String httpUrl,
							MultiValueMap<String, String> headers,
							MultiValueMap<String, String> queryParams,
							TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.GET,
			headers, null, true, typeToken).data();
	}

	public static <R> R get(String service, String api, URI uri, MultiValueMap<String, String> headers,
							TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.GET, headers, null, true, typeToken).data();
	}

	public static <R> R get(String service, String api, String httpUrl,
							MultiValueMap<String, String> headers,
							MultiValueMap<String, String> queryParams,
							boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.GET,
			headers, null, throwError, typeToken).data();
	}

	public static <R> R get(String service, String api, URI uri, MultiValueMap<String, String> headers,
							boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.GET, headers, null, throwError, typeToken).data();
	}

	public static <R> Result<R> getForResult(String service, String api, String httpUrl,
											 MultiValueMap<String, String> headers,
											 MultiValueMap<String, String> queryParams,
											 TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.GET,
			headers, null, true, typeToken);
	}

	public static <R> Result<R> getForResult(String service, String api, URI uri, MultiValueMap<String, String> headers,
											 TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.GET, headers, null, true, typeToken);
	}

	public static <R> Result<R> getForResult(String service, String api, String httpUrl,
											 MultiValueMap<String, String> headers,
											 MultiValueMap<String, String> queryParams,
											 boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.GET,
			headers, null, throwError, typeToken);
	}

	public static <R> Result<R> getForResult(String service, String api, URI uri, MultiValueMap<String, String> headers,
											 boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.GET, headers, null, throwError, typeToken);
	}

	public static <T, R> R post(String service, String api, String httpUrl,
								MultiValueMap<String, String> headers,
								MultiValueMap<String, String> queryParams,
								T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.POST,
			headers, body, true, typeToken).data();
	}

	public static <T, R> R post(String service, String api, URI uri, MultiValueMap<String, String> headers,
								T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.POST, headers, body, true, typeToken).data();
	}

	public static <T, R> R post(String service, String api, String httpUrl,
								MultiValueMap<String, String> queryParams,
								MultiValueMap<String, String> headers,
								T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.POST,
			headers, body, throwError, typeToken).data();
	}

	public static <T, R> R post(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
								boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.POST, headers, body, throwError, typeToken).data();
	}

	public static <T, R> Result<R> postForResult(String service, String api, String httpUrl,
												 MultiValueMap<String, String> headers,
												 MultiValueMap<String, String> queryParams,
												 T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.POST,
			headers, body, true, typeToken);
	}

	public static <T, R> Result<R> postForResult(String service, String api, URI uri, MultiValueMap<String, String> headers,
												 T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.POST, headers, body, true, typeToken);
	}

	public static <T, R> Result<R> postForResult(String service, String api, String httpUrl,
												 MultiValueMap<String, String> queryParams,
												 MultiValueMap<String, String> headers,
												 T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.POST,
			headers, body, throwError, typeToken);
	}

	public static <T, R> Result<R> postForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												 boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.POST, headers, body, throwError, typeToken);
	}

	public static <T, R> R put(String service, String api, String httpUrl,
							   MultiValueMap<String, String> headers,
							   MultiValueMap<String, String> queryParams,
							   T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PUT,
			headers, body, true, typeToken).data();
	}

	public static <T, R> R put(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
							   TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PUT, headers, body, true, typeToken).data();
	}

	public static <T, R> R put(String service, String api, String httpUrl,
							   MultiValueMap<String, String> queryParams,
							   MultiValueMap<String, String> headers,
							   T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PUT,
			headers, body, throwError, typeToken).data();
	}

	public static <T, R> R put(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
							   boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PUT, headers, body, throwError, typeToken).data();
	}

	public static <T, R> Result<R> putForResult(String service, String api, String httpUrl,
												MultiValueMap<String, String> headers,
												MultiValueMap<String, String> queryParams,
												T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PUT,
			headers, body, true, typeToken);
	}

	public static <T, R> Result<R> putForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PUT, headers, body, true, typeToken);
	}

	public static <T, R> Result<R> putForResult(String service, String api, String httpUrl,
												MultiValueMap<String, String> queryParams,
												MultiValueMap<String, String> headers,
												T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PUT,
			headers, body, throwError, typeToken);
	}

	public static <T, R> Result<R> putForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PUT, headers, body, throwError, typeToken);
	}

	public static <T, R> R patch(String service, String api, String httpUrl,
								 MultiValueMap<String, String> headers,
								 MultiValueMap<String, String> queryParams,
								 T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PATCH,
			headers, body, true, typeToken).data();
	}

	public static <T, R> R patch(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
								 TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PATCH, headers, body, true, typeToken).data();
	}

	public static <T, R> R patch(String service, String api, String httpUrl,
								 MultiValueMap<String, String> queryParams,
								 MultiValueMap<String, String> headers,
								 T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PATCH,
			headers, body, throwError, typeToken).data();
	}

	public static <T, R> R patch(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
								 boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PATCH, headers, body, throwError, typeToken).data();
	}

	public static <T, R> R patchForResult(String service, String api, String httpUrl,
										  MultiValueMap<String, String> headers,
										  MultiValueMap<String, String> queryParams,
										  T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PATCH,
			headers, body, true, typeToken).data();
	}

	public static <T, R> Result<R> patchForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												  TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PATCH, headers, body, true, typeToken);
	}

	public static <T, R> Result<R> patchForResult(String service, String api, String httpUrl,
												  MultiValueMap<String, String> queryParams,
												  MultiValueMap<String, String> headers,
												  T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.PATCH,
			headers, body, throwError, typeToken);
	}

	public static <T, R> Result<R> patchForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												  boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.PATCH, headers, body, throwError, typeToken);
	}

	public static <T, R> R delete(String service, String api, String httpUrl,
								  MultiValueMap<String, String> headers,
								  MultiValueMap<String, String> queryParams,
								  T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.DELETE,
			headers, body, true, typeToken).data();
	}

	public static <T, R> R delete(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
								  TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.DELETE, headers, body, true, typeToken).data();
	}

	public static <T, R> R delete(String service, String api, String httpUrl,
								  MultiValueMap<String, String> queryParams,
								  MultiValueMap<String, String> headers,
								  T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.DELETE,
			headers, body, throwError, typeToken).data();
	}

	public static <T, R> R delete(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
								  boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.DELETE, headers, body, throwError, typeToken).data();
	}

	public static <T, R> Result<R> deleteForResult(String service, String api, String httpUrl,
												   MultiValueMap<String, String> headers,
												   MultiValueMap<String, String> queryParams,
												   T body, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.DELETE,
			headers, body, true, typeToken);
	}

	public static <T, R> Result<R> deleteForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												   TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.DELETE, headers, body, true, typeToken);
	}

	public static <T, R> Result<R> deleteForResult(String service, String api, String httpUrl,
												   MultiValueMap<String, String> queryParams,
												   MultiValueMap<String, String> headers,
												   T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), HttpMethod.DELETE,
			headers, body, throwError, typeToken);
	}

	public static <T, R> Result<R> deleteForResult(String service, String api, URI uri, MultiValueMap<String, String> headers, T body,
												   boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, uri, HttpMethod.DELETE, headers, body, throwError, typeToken);
	}

	public static <T, R> R request(String service, String api, String httpUrl, HttpMethod httpMethod,
								   MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers,
								   T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), httpMethod, headers,
			body, throwError, typeToken).data();
	}

	public static <T, R> Result<R> requestForResult(String service, String api, String httpUrl, HttpMethod httpMethod,
													MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers,
													T body, boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(DEFAULT_CLIENT, service, api, UriUtils.fromHttpUrl(httpUrl, queryParams), httpMethod, headers,
			body, throwError, typeToken);
	}

	public static <T, R> R request(RestClient restClient, String service, String api, URI uri, HttpMethod httpMethod,
								   MultiValueMap<String, String> headers, T body,
								   boolean throwError, TypeToken<Result<R>> typeToken) {
		return requestForResult(restClient, service, api, uri, httpMethod, headers, body, throwError, typeToken).data();
	}

	public static <T, R> Result<R> requestForResult(RestClient restClient, String service, String api, URI uri, HttpMethod httpMethod,
													MultiValueMap<String, String> headers, T body,
													boolean throwError, TypeToken<Result<R>> typeToken) {
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
			ResponseEntity<String> responseEntity = requestBodySpec
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(String.class);
			Result<R> result = JsonUtils.fromString(responseEntity.getBody(), typeToken);
			if (result.code() != ConstantPool.SUCCESS_RESPONSE_CODE) {
				String message = StringUtils.defaultIfBlank(result.message(), "无");
				if (throwError) {
					RemoteServiceError remoteServiceError = RemoteServiceErrorBuilder.newInstance(service, api)
						.path(uri.getPath())
						.code(result.code())
						.message(result.message())
						.httpStatus(responseEntity.getStatusCode().value())
						.build();
					throw new RemoteServiceException(remoteServiceError);
				} else {
					LOGGER.error("服务：{}，接口：{}，路径：{} 请求失败，错误码：{}，错误信息：{}", service, api, uri, result.code(), message);
					return null;
				}
			}
			return result;
		} catch (HttpServerErrorException.GatewayTimeout e) {
			RemoteServiceError remoteServiceError = RemoteServiceErrorBuilder.newInstance(service, api)
				.path(uri.getPath())
				.build();
			throw new RemoteServiceTimeoutException(remoteServiceError);
		} catch (RestClientResponseException e) {
			Result<Void> result = JsonUtils.fromString(e.getResponseBodyAsString(), new TypeToken<Result<Void>>() {
			});
			RemoteServiceError remoteServiceError = RemoteServiceErrorBuilder.newInstance(service, api)
				.path(uri.getPath())
				.code(result.code())
				.message(result.message())
				.httpStatus(e.getStatusCode().value())
				.build();
			throw new RemoteServiceException(remoteServiceError);
		}
	}
}
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
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtils.class);

	protected HttpRequestUtils() {
	}

	public static byte[] postDownload(RestClient restClient, URI uri, @Nullable MultiValueMap<String, String> headers,
									  @Nullable Object requestBody) throws RestClientResponseException {

		return buildRequest(restClient, uri, HttpMethod.POST, headers, requestBody)
			.retrieve()
			.body(byte[].class);
	}

	public static byte[] getDownload(RestClient restClient, URI uri, @Nullable MultiValueMap<String, String> headers) throws RestClientResponseException {

		return buildRequest(restClient, uri, HttpMethod.GET, headers, null)
			.retrieve()
			.body(byte[].class);
	}

	public static <T> T get(RestClient restClient, URI uri, @Nullable MultiValueMap<String, String> headers,
							ParameterizedTypeReference<T> typeReference) throws RestClientResponseException {
		return buildRequest(restClient, uri, HttpMethod.GET, headers, null)
			.accept(MediaType.APPLICATION_JSON)
			.acceptCharset(StandardCharsets.UTF_8)
			.retrieve()
			.toEntity(typeReference)
			.getBody();
	}

	public static <T> T post(RestClient restClient, URI uri, @Nullable MultiValueMap<String, String> headers,
							 @Nullable Object requestBody, ParameterizedTypeReference<T> typeReference) throws RestClientResponseException {
		return buildRequest(restClient, uri, HttpMethod.POST, headers, requestBody)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(typeReference)
			.getBody();
	}

	public static <T> T request(RestClient restClient, URI uri, HttpMethod httpMethod,
								@Nullable MultiValueMap<String, String> headers,
								@Nullable Object requestBody,
								ParameterizedTypeReference<T> typeReference) throws RestClientResponseException {
		return buildRequest(restClient, uri, httpMethod, headers, requestBody)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toEntity(typeReference)
			.getBody();
	}

	public static <T> Optional<T> getResultData(String service, String api, URI uri, Result<T> result) {
		return getResultData(service, api, uri, true, result);
	}

	public static <T> Optional<T> getResultData(String service, String api, URI uri, boolean throwError, Result<T> result) {
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

	public static void handleError(RestClientResponseException exception, RemoteServiceErrorBuilder builder,
								   String errorCodeMemberName, String errorMessageMemberName) {
		if (exception instanceof HttpServerErrorException.GatewayTimeout) {
			throw new RemoteServiceTimeoutException(builder.build());
		}

		try {
			builder.httpStatus(exception.getStatusCode().value());
			JsonObject response = JsonUtils.parseString(exception.getResponseBodyAsString()).getAsJsonObject();

			String message = response.getAsJsonPrimitive(errorMessageMemberName).getAsString();
			builder.message(message);

			JsonPrimitive code = response.getAsJsonPrimitive(errorCodeMemberName);
			if (code.isString()) {
				try {
					builder.code(Integer.parseInt(code.getAsString()));
				} catch (NumberFormatException ignored) {
				}
			} else if (code.isBoolean()) {
				builder.code(BooleanUtils.toInteger(code.getAsBoolean()));
			} else if (code.isNumber()) {
				builder.code(code.getAsInt());
			}

			throw new RemoteServiceException(builder.build());
		} catch (JsonParseException | IllegalStateException e) {
			throw new ServerException("接口响应体解析失败", e);
		}
	}

	protected static RestClient.RequestBodySpec buildRequest(RestClient restClient, URI uri, HttpMethod httpMethod,
															 @Nullable MultiValueMap<String, String> headers,
															 @Nullable Object requestBody) {
		RestClient.RequestBodySpec requestBodySpec = restClient
			.method(httpMethod)
			.uri(uri);
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
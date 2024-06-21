package io.github.pangju666.framework.http.utils;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.core.exception.remote.RemoteServiceException;
import io.github.pangju666.framework.core.exception.remote.RemoteServiceTimeoutException;
import io.github.pangju666.framework.core.exception.remote.model.RemoteServiceError;
import io.github.pangju666.framework.core.exception.remote.model.RemoteServiceErrorBuilder;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import io.github.pangju666.framework.web.model.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ResultApiUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultApiUtils.class);
    private static final RestClient DEFAULT_CLIENT = RestClient.create();

    protected ResultApiUtils() {
    }

    public static <R> Optional<R> get(String service, String api, String httpUrl,
                                      @Nullable MultiValueMap<String, String> queryParams,
                                      @Nullable MultiValueMap<String, String> headers,
                                      TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.GET, headers, null, true, typeToken);
    }

    public static <R> Optional<R> get(String service, String api, URI uri,
                                      @Nullable MultiValueMap<String, String> headers,
                                      TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.GET, headers, null, true, typeToken);
    }

    public static <R> Optional<R> get(String service, String api, String httpUrl,
                                      @Nullable MultiValueMap<String, String> queryParams,
                                      @Nullable MultiValueMap<String, String> headers,
                                      boolean throwError, TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.GET, headers, null, throwError, typeToken);
    }

    public static <R> Optional<R> get(String service, String api, URI uri,
                                      @Nullable MultiValueMap<String, String> headers,
                                      boolean throwError, TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.GET, headers, null, throwError, typeToken);
    }

    public static <T, R> Optional<R> post(String service, String api, String httpUrl,
                                          @Nullable MultiValueMap<String, String> queryParams,
                                          @Nullable MultiValueMap<String, String> headers,
                                          @Nullable T requestBody,
                                          TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.POST, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> post(String service, String api, URI uri,
                                          @Nullable MultiValueMap<String, String> headers,
                                          @Nullable T requestBody,
                                          TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.POST, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> post(String service, String api, String httpUrl,
                                          @Nullable MultiValueMap<String, String> queryParams,
                                          @Nullable MultiValueMap<String, String> headers,
                                          @Nullable T requestBody,
                                          boolean throwError, TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.POST, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> post(String service, String api, URI uri,
                                          @Nullable MultiValueMap<String, String> headers,
                                          @Nullable T requestBody,
                                          boolean throwError, TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.POST, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> put(String service, String api, String httpUrl,
                                         @Nullable MultiValueMap<String, String> queryParams,
                                         @Nullable MultiValueMap<String, String> headers,
                                         @Nullable T requestBody,
                                         TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.PUT, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> put(String service, String api, URI uri,
                                         @Nullable MultiValueMap<String, String> headers,
                                         @Nullable T requestBody,
                                         TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.PUT, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> put(String service, String api, String httpUrl,
                                         @Nullable MultiValueMap<String, String> queryParams,
                                         @Nullable MultiValueMap<String, String> headers,
                                         @Nullable T requestBody,
                                         boolean throwError, TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.PUT, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> put(String service, String api, URI uri,
                                         @Nullable MultiValueMap<String, String> headers,
                                         @Nullable T requestBody,
                                         boolean throwError, TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.PUT, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> patch(String service, String api, String httpUrl,
                                           @Nullable MultiValueMap<String, String> queryParams,
                                           @Nullable MultiValueMap<String, String> headers,
                                           @Nullable T requestBody,
                                           TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.PATCH, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> patch(String service, String api, URI uri,
                                           @Nullable MultiValueMap<String, String> headers,
                                           @Nullable T requestBody,
                                           TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.PATCH, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> patch(String service, String api, String httpUrl,
                                           @Nullable MultiValueMap<String, String> queryParams,
                                           @Nullable MultiValueMap<String, String> headers,
                                           @Nullable T requestBody,
                                           boolean throwError, TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.PATCH, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> patch(String service, String api, URI uri,
                                           @Nullable MultiValueMap<String, String> headers,
                                           @Nullable T requestBody,
                                           boolean throwError, TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.PATCH, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> delete(String service, String api, String httpUrl,
                                            @Nullable MultiValueMap<String, String> queryParams,
                                            @Nullable MultiValueMap<String, String> headers,
                                            @Nullable T requestBody,
                                            TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.DELETE, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> delete(String service, String api, URI uri,
                                            @Nullable MultiValueMap<String, String> headers,
                                            @Nullable T requestBody,
                                            TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.DELETE, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> delete(String service, String api, String httpUrl,
                                            @Nullable MultiValueMap<String, String> queryParams,
                                            @Nullable MultiValueMap<String, String> headers,
                                            @Nullable T requestBody,
                                            boolean throwError, TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, HttpMethod.DELETE, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> delete(String service, String api, URI uri,
                                            @Nullable MultiValueMap<String, String> headers,
                                            @Nullable T requestBody,
                                            boolean throwError, TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, HttpMethod.DELETE, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> request(String service, String api, String httpUrl, HttpMethod httpMethod,
                                             @Nullable MultiValueMap<String, String> queryParams,
                                             @Nullable MultiValueMap<String, String> headers,
                                             @Nullable T requestBody,
                                             TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, httpMethod, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> request(String service, String api, URI uri, HttpMethod httpMethod,
                                             @Nullable MultiValueMap<String, String> headers,
                                             @Nullable T requestBody,
                                             TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, httpMethod, headers, requestBody, true, typeToken);
    }

    public static <T, R> Optional<R> request(String service, String api, String httpUrl, HttpMethod httpMethod,
                                             @Nullable MultiValueMap<String, String> queryParams,
                                             @Nullable MultiValueMap<String, String> headers,
                                             @Nullable T requestBody,
                                             boolean throwError, TypeToken<Result<R>> typeToken) {
        URI uri = UriUtils.fromHttpUrl(httpUrl, ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
        return request(service, api, uri, httpMethod, headers, requestBody, throwError, typeToken);
    }

    public static <T, R> Optional<R> request(String service, String api, URI uri, HttpMethod httpMethod,
                                             @Nullable MultiValueMap<String, String> headers,
                                             @Nullable T requestBody,
                                             boolean throwError, TypeToken<Result<R>> typeToken) {
        return request(service, api, uri, throwError, restClient -> {
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
            if (Objects.nonNull(requestBody)) {
                if (requestBody instanceof JsonObject jsonBody) {
                    requestBodySpec.body(jsonBody.toString());
                } else {
                    requestBodySpec.body(JsonUtils.toString(requestBody));
                }
            }
            ResponseEntity<String> responseEntity = requestBodySpec
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class);
            return JsonUtils.fromString(responseEntity.getBody(), typeToken);
        });
    }

    public static <T> Optional<T> request(String service, String api, URI uri, Function<RestClient, Result<T>> function) {
        return request(service, api, uri, true, function);
    }

    public static <T> Optional<T> request(String service, String api, URI uri, boolean throwError, Function<RestClient, Result<T>> function) {
        try {
            Result<T> result = function.apply(DEFAULT_CLIENT);
            if (result.code() == ConstantPool.SUCCESS_RESPONSE_CODE) {
                return Optional.ofNullable(result.data());
            }
            RemoteServiceError remoteServiceError = new RemoteServiceErrorBuilder(service, api)
                    .uri(uri)
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
        } catch (HttpServerErrorException.GatewayTimeout e) {
            RemoteServiceError remoteServiceError = new RemoteServiceErrorBuilder(service, api)
                    .uri(uri)
                    .httpStatus(e.getStatusCode().value())
                    .build();
            throw new RemoteServiceTimeoutException(remoteServiceError);
        } catch (RestClientResponseException e) {
            Result<Void> result = JsonUtils.fromString(e.getResponseBodyAsString(), new TypeToken<Result<Void>>() {
            });
            RemoteServiceError remoteServiceError = new RemoteServiceErrorBuilder(service, api)
                    .uri(uri)
                    .code(result.code())
                    .message(result.message())
                    .httpStatus(e.getStatusCode().value())
                    .build();
            throw new RemoteServiceException(remoteServiceError);
        }
    }
}
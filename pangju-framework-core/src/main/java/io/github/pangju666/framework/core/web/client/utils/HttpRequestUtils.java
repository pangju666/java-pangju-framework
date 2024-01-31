package io.github.pangju666.framework.core.web.client.utils;

import io.github.pangju666.framework.core.web.client.model.HttpRequestEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

public class HttpRequestUtils {
	protected static final RestTemplate REST_TEMPLATE = new RestTemplate();

	protected HttpRequestUtils() {
	}

	public static <T> ResponseEntity<T> exchange(HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		return exchange(REST_TEMPLATE, httpRequestEntity, responseType);
	}

	public static <T> ResponseEntity<T> exchange(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		try {
			return restTemplate.exchange(httpRequestEntity.uri(), httpRequestEntity.method(), httpRequestEntity.httpEntity(), responseType);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static <T> T execute(HttpRequestEntity httpRequestEntity, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) {
		return execute(REST_TEMPLATE, httpRequestEntity, requestCallback, responseExtractor);
	}

	public static <T> T execute(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity,
								@Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) {
		try {
			return restTemplate.execute(httpRequestEntity.uri(), httpRequestEntity.method(), requestCallback, responseExtractor);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static <T> ResponseEntity<T> getForEntity(HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		return getForEntity(REST_TEMPLATE, httpRequestEntity, responseType);
	}

	public static <T> ResponseEntity<T> getForEntity(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		try {
			return restTemplate.getForEntity(httpRequestEntity.uri(), responseType);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static <T> T getForObject(HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		return getForObject(REST_TEMPLATE, httpRequestEntity, responseType);
	}

	public static <T> T getForObject(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		try {
			return restTemplate.getForObject(httpRequestEntity.uri(), responseType);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static HttpHeaders headForHeaders(HttpRequestEntity httpRequestEntity) throws RestClientException {
		return headForHeaders(REST_TEMPLATE, httpRequestEntity);
	}

	public static HttpHeaders headForHeaders(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity) throws RestClientException {
		try {
			return restTemplate.headForHeaders(httpRequestEntity.uri());
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static <T> ResponseEntity<T> postForEntity(HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		return postForEntity(REST_TEMPLATE, httpRequestEntity, responseType);
	}

	public static <T> ResponseEntity<T> postForEntity(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		try {
			return restTemplate.postForEntity(httpRequestEntity.uri(), httpRequestEntity.httpEntity(), responseType);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static <T> T postForObject(HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		return postForObject(REST_TEMPLATE, httpRequestEntity, responseType);
	}

	public static <T> T postForObject(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		try {
			return restTemplate.postForObject(httpRequestEntity.uri(), httpRequestEntity.httpEntity(), responseType);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static URI postForLocation(HttpRequestEntity httpRequestEntity) {
		return postForLocation(REST_TEMPLATE, httpRequestEntity);
	}

	public static URI postForLocation(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity) {
		try {
			return restTemplate.postForLocation(httpRequestEntity.uri(), httpRequestEntity.httpEntity());
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static void put(HttpRequestEntity httpRequestEntity) throws RestClientException {
		put(REST_TEMPLATE, httpRequestEntity);
	}

	public static void put(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity) {
		try {
			restTemplate.put(httpRequestEntity.uri(), httpRequestEntity.httpEntity());
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			ExceptionUtils.rethrow(e);
		}
	}

	public static <T> T patchForObject(HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		return patchForObject(REST_TEMPLATE, httpRequestEntity, responseType);
	}

	public static <T> T patchForObject(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity, Class<T> responseType) {
		try {
			return restTemplate.patchForObject(httpRequestEntity.uri(), httpRequestEntity.httpEntity(), responseType);
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}

	public static void delete(HttpRequestEntity httpRequestEntity) {
		delete(REST_TEMPLATE, httpRequestEntity);
	}

	public static void delete(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity) {
		try {
			restTemplate.delete(httpRequestEntity.uri());
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			ExceptionUtils.rethrow(e);
		}
	}

	public static Set<HttpMethod> optionsForAllow(HttpRequestEntity httpRequestEntity) {
		return optionsForAllow(REST_TEMPLATE, httpRequestEntity);
	}

	public static Set<HttpMethod> optionsForAllow(RestTemplate restTemplate, HttpRequestEntity httpRequestEntity) {
		try {
			return restTemplate.optionsForAllow(httpRequestEntity.uri());
		} catch (RestClientException e) {
			if (Objects.nonNull(httpRequestEntity.exceptionCallback())) {
				throw httpRequestEntity.exceptionCallback().apply(e);
			}
			return ExceptionUtils.rethrow(e);
		}
	}
}

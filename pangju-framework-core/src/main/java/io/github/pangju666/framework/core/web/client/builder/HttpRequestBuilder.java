package io.github.pangju666.framework.core.web.client.builder;

import io.github.pangju666.commons.lang.pool.ConstantPool;
import io.github.pangju666.framework.core.exception.base.BaseRuntimeException;
import io.github.pangju666.framework.core.web.client.model.HttpRequestEntity;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HttpRequestBuilder {
	private HttpEntity<?> httpEntity = HttpEntity.EMPTY;
	private HttpMethod method = HttpMethod.GET;
	private UriBuilder uriBuilder = UriComponentsBuilder.newInstance();
	private Function<RestClientException, ? extends BaseRuntimeException> exceptionCallback;
	private String contextPath;
	private List<String> paths = new ArrayList<>();

	protected HttpRequestBuilder() {
	}

	protected HttpRequestBuilder(UriBuilder uriBuilder) {
		if (Objects.nonNull(uriBuilder)) {
			this.uriBuilder = uriBuilder;
		}
	}

	public static HttpRequestBuilder newInstance() {
		return new HttpRequestBuilder();
	}

	public static HttpRequestBuilder fromUrl(String url) {
		return new HttpRequestBuilder(UriComponentsBuilder.fromHttpUrl(url));
	}

	public static HttpRequestBuilder fromUri(String uri) {
		return new HttpRequestBuilder(UriComponentsBuilder.fromUriString(uri));
	}

	public static HttpRequestBuilder fromUri(URI uri) {
		return new HttpRequestBuilder(UriComponentsBuilder.fromUri(uri));
	}

	public HttpRequestBuilder contextPath(String contextPath) {
		if (StringUtils.isNotBlank(contextPath)) {
			if (!contextPath.startsWith(ConstantPool.HTTP_PATH_SEPARATOR)) {
				this.contextPath = ConstantPool.HTTP_PATH_SEPARATOR + contextPath;
			} else {
				this.contextPath = contextPath;
			}
		}
		return this;
	}

	public HttpRequestBuilder queryParams(MultiValueMap<String, String> queryParams) {
		if (Objects.isNull(uriBuilder)) {
			this.uriBuilder = UriComponentsBuilder.newInstance();
		}
		this.uriBuilder.queryParams(ObjectUtils.defaultIfNull(queryParams, new LinkedMultiValueMap<>()));
		return this;
	}

	public HttpRequestBuilder headers(HttpHeaders headers) {
		if (this.httpEntity.hasBody()) {
			this.httpEntity = new HttpEntity<>(this.httpEntity.getBody(), headers);
		} else {
			this.httpEntity = new HttpEntity<>(headers);
		}
		return this;
	}

	public <T> HttpRequestBuilder body(T body) {
		this.httpEntity = new HttpEntity<>(body, this.httpEntity.getHeaders());
		return this;
	}

	public HttpRequestBuilder port(int port) {
		if (Objects.isNull(uriBuilder)) {
			this.uriBuilder = UriComponentsBuilder.newInstance();
		}
		uriBuilder.port(port);
		return this;
	}

	public HttpRequestBuilder httpEntity(HttpEntity<?> httpEntity) {
		this.httpEntity = ObjectUtils.defaultIfNull(httpEntity, HttpEntity.EMPTY);
		return this;
	}

	public HttpRequestBuilder http() {
		if (Objects.isNull(uriBuilder)) {
			this.uriBuilder = UriComponentsBuilder.newInstance();
		}
		uriBuilder.scheme("http");
		return this;
	}

	public HttpRequestBuilder method(HttpMethod method) {
		this.method = method;
		return this;
	}

	public <E extends BaseRuntimeException> HttpRequestBuilder exceptionCallback(Function<RestClientException, E> exceptionCallback) {
		this.exceptionCallback = exceptionCallback;
		return this;
	}

	public HttpRequestBuilder https() {
		if (Objects.isNull(uriBuilder)) {
			this.uriBuilder = UriComponentsBuilder.newInstance();
		}
		uriBuilder.scheme("https");
		return this;
	}

	public HttpRequestBuilder host(String host) {
		if (Objects.isNull(uriBuilder)) {
			this.uriBuilder = UriComponentsBuilder.newInstance();
		}
		if (StringUtils.endsWith(host, ConstantPool.HTTP_PATH_SEPARATOR)) {
			uriBuilder.host(host.substring(0, host.length() - 1));
		} else {
			uriBuilder.host(host);
		}
		return this;
	}

	public HttpRequestBuilder path(String path) {
		if (StringUtils.isNotBlank(path)) {
			if (!path.startsWith(ConstantPool.HTTP_PATH_SEPARATOR)) {
				this.paths.add(ConstantPool.HTTP_PATH_SEPARATOR + path);
			} else {
				this.paths.add(path);
			}
		}
		return this;
	}

	public HttpRequestBuilder paths(String... paths) {
		this.paths = Arrays.stream(paths)
			.filter(StringUtils::isNotBlank)
			.map(path -> {
				if (!path.startsWith(ConstantPool.HTTP_PATH_SEPARATOR)) {
					return ConstantPool.HTTP_PATH_SEPARATOR + path;
				}
				return path;
			})
			.collect(Collectors.toList());
		return this;
	}

	public HttpRequestEntity build() {
		return build(Collections.emptyMap());
	}

	public HttpRequestEntity build(Map<String, ?> uriVariables) {
		if (Objects.isNull(uriBuilder)) {
			this.uriBuilder = UriComponentsBuilder.newInstance();
		}

		this.uriBuilder.path(contextPath);
		for (String path : this.paths) {
			this.uriBuilder.path(path);
		}

		URI uri;
		if (MapUtils.isEmpty(uriVariables)) {
			uri = uriBuilder.build();
		} else {
			uri = uriBuilder.build(uriVariables);
		}

		return HttpRequestEntity.newInstance(this.httpEntity, this.method, uri, this.exceptionCallback);
	}
}

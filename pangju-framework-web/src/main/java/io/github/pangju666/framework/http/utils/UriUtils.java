package io.github.pangju666.framework.http.utils;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriUtils {
	protected UriUtils() {
	}

	public static URI fromHttpUrl(String httpUrl) {
		return UriComponentsBuilder.fromUriString(httpUrl)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromHttpUrl(String httpUrl, String path) {
		return UriComponentsBuilder.fromUriString(httpUrl)
			.path(path)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromUri(URI uri, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromUri(uri)
			.queryParams(queryParams)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromHttpUrl(String httpUrl, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromUriString(httpUrl)
			.queryParams(queryParams)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromHttpUrl(String httpUrl, String path, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromUriString(httpUrl)
			.path(path)
			.queryParams(queryParams)
			.build()
			.encode()
			.toUri();
	}
}
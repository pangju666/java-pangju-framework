package io.github.pangju666.framework.http.utils;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriUtils {
	protected UriUtils() {
	}

	public static URI fromHttpUrl(String httpUrl) {
		return UriComponentsBuilder.fromHttpUrl(httpUrl)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromHttpUrl(String httpUrl, String path) {
		return UriComponentsBuilder.fromHttpUrl(httpUrl)
			.path(path)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromHttpUrl(String httpUrl, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromHttpUrl(httpUrl)
			.queryParams(queryParams)
			.build()
			.encode()
			.toUri();
	}

	public static URI fromHttpUrl(String httpUrl, String path, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromHttpUrl(httpUrl)
			.path(path)
			.queryParams(queryParams)
			.build()
			.encode()
			.toUri();
	}
}

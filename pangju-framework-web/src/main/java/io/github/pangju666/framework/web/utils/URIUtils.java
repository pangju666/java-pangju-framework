package io.github.pangju666.framework.web.utils;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class URIUtils {
	protected URIUtils() {
	}

	public static URI fromHttpUrlAndQueryParams(String httpUrl, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromHttpUrl(httpUrl)
			.queryParams(queryParams)
			.build()
			.encode()
			.toUri();
	}
}

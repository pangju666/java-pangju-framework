/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.utils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestUtils {
	protected static final String[] MOBILE_AGENTS = {"Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser"};

	protected RequestUtils() {
	}

	public static String getRequestUrl(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String contextPath = request.getServletContext().getContextPath();
		return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
	}

	public static HttpServletRequest getCurrentRequest() {
		return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
	}

	public static RequestAttributes getCurrentRequestAttributes() {
		return RequestContextHolder.currentRequestAttributes();
	}

	public static boolean isFormMobile(final String userAgent) {
		if (!userAgent.contains("Windows NT") || (userAgent.contains("Windows NT") && userAgent.contains("compatible; MSIE 9.0;"))) {
			// 排除苹果桌面系统
			if (!userAgent.contains("Windows NT") && !userAgent.contains("Macintosh")) {
				for (String item : MOBILE_AGENTS) {
					if (userAgent.contains(item)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isFromAjax(final HttpServletRequest request) {
		String accept = request.getHeader(HttpHeaders.ACCEPT);
		if (StringUtils.contains(accept, MediaType.APPLICATION_JSON_VALUE)) {
			return true;
		}

		String xRequestedWith = request.getHeader("X-Requested-With");
		if (StringUtils.contains(xRequestedWith, "XMLHttpRequest")) {
			return true;
		}

		String uri = request.getRequestURI();
		if (StringUtils.containsAnyIgnoreCase(uri, ".json", ".xml")) {
			return true;
		}

		String ajax = request.getParameter("__ajax");
		return StringUtils.containsAnyIgnoreCase(ajax, "json", "xml");
	}

	public static String getRequestPath(final HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (StringUtils.isNotBlank(contextPath)) {
			return contextPath + request.getServletPath();
		}
		return request.getServletPath();
	}

	public static String getIpAddress(final HttpServletRequest request) {
		if (Objects.isNull(request)) {
			return IpUtils.UNKNOWN_ADDRESS;
		}
		String ip = request.getHeader("x-forwarded-for");
		if (IpUtils.isUnknown(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (IpUtils.isUnknown(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (IpUtils.isUnknown(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (IpUtils.isUnknown(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (IpUtils.isUnknown(ip)) {
			ip = request.getRemoteAddr();
		}
		return Constants.LOCALHOST_IPV6_ADDRESS.equals(ip) ? Constants.LOCALHOST_IPV4_ADDRESS : IpUtils.getMultistageReverseProxyIp(ip);
	}

	public static Map<String, Object> getParameterMap(final HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, Object> requestParamMap = new HashMap<>(parameterMap.size());
		for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
			String[] value = stringEntry.getValue();
			if (value.length == 1) {
				requestParamMap.put(stringEntry.getKey(), value[0]);
			} else {
				requestParamMap.put(stringEntry.getKey(), Arrays.asList(value));
			}
		}
		return requestParamMap;
	}

	public static Map<String, Object> getHeaderMap(final HttpServletRequest request) {
		Map<String, Object> requestHeaderMap = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> enumeration = request.getHeaders(headerName);

			LinkedList<String> values = new LinkedList<>();
			int count = 0;
			while (enumeration.hasMoreElements()) {
				values.add(enumeration.nextElement());
				++count;
			}
			if (count == 1) {
				requestHeaderMap.put(headerName, values.getFirst());
			} else {
				requestHeaderMap.put(headerName, values);
			}
		}
		return requestHeaderMap;
	}

	public static Map<String, Object> getMultipartMap(final HttpServletRequest request) throws ServletException, IOException {
		Collection<Part> parts = request.getParts();
		Map<String, Object> formDataMap = new HashMap<>(parts.size());
		for (Part part : parts) {
			if (Objects.nonNull(part.getContentType())) {
				Map<String, Object> multipartFileMap = new HashMap<>(3);
				multipartFileMap.put("contentType", part.getContentType());
				multipartFileMap.put("filename", part.getSubmittedFileName());
				multipartFileMap.put("size", part.getSize());
				formDataMap.put(part.getName(), multipartFileMap);
			} else {
				formDataMap.put(part.getName(), request.getParameter(part.getName()));
			}
		}
		return formDataMap;
	}

	public static Map<String, Object> getRequestBodyMap(final HttpServletRequest request) throws IOException {
		if (!MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType()) &&
			!MediaType.APPLICATION_JSON_UTF8_VALUE.equals(request.getContentType())) {
			return Collections.emptyMap();
		}
		String requestBodyStr;
		if (request instanceof ContentCachingRequestWrapper requestWrapper) {
			requestBodyStr = requestWrapper.getContentAsString();
		} else {
			try (InputStream inputStream = request.getInputStream()) {
				requestBodyStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		}
		JsonElement requestBody = JsonUtils.parseString(requestBodyStr);
		if (!requestBody.isJsonObject()) {
			return Collections.emptyMap();
		}
		return JsonUtils.fromJson(requestBody, new TypeToken<Map<String, Object>>() {
		});
	}

	public static String getDomain(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String contextPath = request.getServletContext().getContextPath();
		return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
	}
}
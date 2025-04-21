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
import com.google.gson.JsonNull;
import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HTTP请求工具类
 * <p>
 * 提供对HTTP请求的全面操作和提取工具，主要包含以下功能：
 * <ul>
 *     <li>请求来源分析：判断移动设备、Ajax请求等</li>
 *     <li>IP地址解析：准确获取客户端IP，支持多级代理</li>
 *     <li>请求数据提取：参数、头信息、文件上传内容等</li>
 *     <li>请求体处理：支持原始字节流、文本内容和JSON解析</li>
 * </ul>
 * </p>
 * <p>
 * 本工具类中的所有方法都针对{@link jakarta.servlet.http.HttpServletRequest}对象进行操作，
 * 提供不可变返回结果，确保线程安全性。方法参数均进行非空校验，避免空指针异常。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RequestUtils {
	/**
	 * 移动设备的用户代理标识字符串数组
	 * <p>
	 * 包含常见移动设备平台的标识，用于判断请求是否来自移动设备。
	 * </p>
	 *
	 * @see #isFormMobile(HttpServletRequest)
	 * @since 1.0.0
	 */
	protected static final String[] MOBILE_AGENTS = {"Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser"};

	protected RequestUtils() {
	}

	/**
	 * 判断请求是否来自移动设备
	 * <p>
	 * 通过分析 User-Agent 请求头判断请求是否来自移动设备，规则如下：
	 * <ul>
	 *     <li>排除 Windows 桌面系统（除了IE9）</li>
	 *     <li>排除 Mac 桌面系统</li>
	 *     <li>包含 {@link #MOBILE_AGENTS} 中定义的移动设备标识</li>
	 * </ul>
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 如果请求来自移动设备则返回true，否则返回false
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static boolean isFormMobile(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

		String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
		if (StringUtils.isNotBlank(userAgent)) {
			if (!StringUtils.containsIgnoreCase(userAgent, "Windows NT") ||
				(StringUtils.containsIgnoreCase(userAgent, "Windows NT") &&
					StringUtils.containsIgnoreCase(userAgent, "compatible; MSIE 9.0;"))) {
				// 排除 苹果桌面系统
				if (!StringUtils.containsIgnoreCase(userAgent, "Windows NT") &&
					!StringUtils.containsIgnoreCase(userAgent, "Macintosh")) {
					return StringUtils.containsAnyIgnoreCase(userAgent, MOBILE_AGENTS);
				}
			}
		}
		return false;
	}

	/**
	 * 判断请求是否为Ajax请求
	 * <p>
	 * 通过以下方式判断是否为Ajax请求：
	 * <ul>
	 *     <li>检查Accept头是否包含application/json</li>
	 *     <li>检查X-Requested-With头是否包含XMLHttpRequest</li>
	 *     <li>检查请求URI是否包含.json或.xml后缀</li>
	 *     <li>检查请求参数__ajax是否包含json或xml值</li>
	 * </ul>
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 如果是Ajax请求则返回true，否则返回false
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static boolean isFromAjax(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

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

	/**
	 * 获取请求的真实IP地址
	 * <p>
	 * 依次从以下请求头中获取IP地址：
	 * <ol>
	 *     <li>x-forwarded-for</li>
	 *     <li>Proxy-Client-IP</li>
	 *     <li>X-Forwarded-For</li>
	 *     <li>WL-Proxy-Client-IP</li>
	 *     <li>X-Real-IP</li>
	 *     <li>最后使用request.getRemoteAddr()</li>
	 * </ol>
	 * 如果获取到的IP是本地IP，则返回标准IPv4本地地址(127.0.0.1)。
	 * 如果IP包含多个地址（多级代理），则使用{@link IpUtils#getMultistageReverseProxyIp(String)}获取第一个非unknown地址。
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 客户端真实IP地址
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static String getIpAddress(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

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

		return StringUtils.equalsAnyIgnoreCase(ip, Constants.LOCALHOST_IPV4_ADDRESS,
			Constants.LOCALHOST_IPV6_ADDRESS) ? Constants.LOCALHOST_IPV4_ADDRESS : IpUtils.getMultistageReverseProxyIp(ip);
	}

	/**
	 * 获取请求中的所有URL参数
	 * <p>
	 * 将请求中的参数Map转换为Spring的MultiValueMap结构，保持参数值的顺序。
	 * 对于单值参数直接存储；对于多值参数保留为列表。
	 * 返回的MultiValueMap是不可修改的。
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 包含所有请求参数的不可修改MultiValueMap
	 * @throws IllegalArgumentException 当request为null时抛出
	 * @since 1.0.0
	 */
	public static MultiValueMap<String, String> getRequestParameters(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

		Map<String, String[]> parameterMap = request.getParameterMap();
		MultiValueMap<String, String> requestParamMap = new LinkedMultiValueMap<>(parameterMap.size());
		for (Map.Entry<String, String[]> parameterEntry : parameterMap.entrySet()) {
			String[] value = parameterEntry.getValue();
			if (value.length == 1) {
				requestParamMap.add(parameterEntry.getKey(), value[0]);
			} else {
				requestParamMap.addAll(parameterEntry.getKey(), Arrays.asList(value));
			}
		}
		return CollectionUtils.unmodifiableMultiValueMap(requestParamMap);
	}

	/**
	 * 获取请求中的所有HTTP头信息
	 * <p>
	 * 将请求中的所有头信息提取到Spring的HttpHeaders对象中。
	 * 对于单值头部直接存储；对于多值头部保留为列表。
	 * 返回的HttpHeaders是只读的。
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 包含所有HTTP头信息的只读HttpHeaders对象
	 * @throws IllegalArgumentException 当request为null时抛出
	 * @since 1.0.0
	 */
	public static HttpHeaders getHttpHeaders(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

		Enumeration<String> headerNames = request.getHeaderNames();
		HttpHeaders headers = new HttpHeaders();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> enumeration = request.getHeaders(headerName);

			List<String> values = new ArrayList<>(5);
			int count = 0;
			while (enumeration.hasMoreElements()) {
				values.add(enumeration.nextElement());
				++count;
			}
			if (count == 1) {
				headers.add(headerName, values.get(0));
			} else {
				headers.addAll(headerName, values);
			}
		}
		return HttpHeaders.readOnlyHttpHeaders(headers);
	}

	/**
	 * 获取请求中的文件上传部分
	 * <p>
	 * 从multipart请求中提取所有包含文件的部分。该方法只返回那些具有提交文件名
	 * 的部分（即实际上传的文件），过滤掉表单字段等不包含文件的部分。
	 * 返回的Map是不可修改的，以防止意外修改。
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 以表单字段名为键，Part对象为值的不可修改Map
	 * @throws IllegalArgumentException 当request为null时抛出
	 * @throws ServletException 解析multipart请求失败时抛出
	 * @throws IOException 读取请求内容失败时抛出
	 * @since 1.0.0
	 */
	public static Map<String, Part> getRequestParts(final HttpServletRequest request) throws ServletException, IOException {
		Assert.notNull(request, "request 不可为null");

		Collection<Part> parts = request.getParts();
		Map<String, Part> requestPartMap = new HashMap<>(parts.size());
		for (Part part : parts) {
			if (Objects.nonNull(part.getSubmittedFileName())) {
				requestPartMap.put(part.getName(), part);
			}
		}
		return Collections.unmodifiableMap(requestPartMap);
	}

	/**
	 * 获取HTTP原始请求体
	 * <p>
	 * 从HTTP请求中读取完整的请求体内容，并以字节数组形式返回。处理逻辑如下：
	 * <ul>
	 *     <li>优先从ContentCachingRequestWrapper缓存获取内容（如果已包装）</li>
	 *     <li>如缓存数组为空或未使用包装器，则从请求输入流直接读取</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 此方法适用于需要访问原始请求体数据的场景，如：
	 * <ul>
	 *     <li>请求内容审计与日志记录</li>
	 *     <li>请求体签名验证</li>
	 *     <li>自定义格式请求体解析</li>
	 * </ul>
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 包含请求体内容的字节数组
	 * @throws IOException              读取请求内容失败时抛出
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static byte[] getRawRequestBody(final HttpServletRequest request) throws IOException {
		Assert.notNull(request, "request 不可为null");

		if (request instanceof ContentCachingRequestWrapper requestWrapper) {
			byte[] bytes = requestWrapper.getContentAsByteArray();
			if (ArrayUtils.isNotEmpty(bytes)) {
				return bytes;
			}
		}
		try (InputStream inputStream = request.getInputStream()) {
			return inputStream.readAllBytes();
		}
	}

	/**
	 * 获取HTTP请求体的字符串表示
	 * <p>
	 * 将请求体内容读取并转换为字符串，字符集判断逻辑如下：
	 * <ul>
	 *     <li>尝试从Content-Type头部提取字符集信息</li>
	 *     <li>如提取失败或字符集名称无效，则使用UTF-8作为默认字符集</li>
	 * </ul>
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 请求体内容的字符串表示
	 * @throws IOException              读取请求内容失败时抛出
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static String getStringRequestBody(final HttpServletRequest request) throws IOException {
		byte[] requestBodyBytes = getRawRequestBody(request);

		Charset charset;
		try {
			String charsetName = StringUtils.substringAfterLast(request.getContentType(), ";");
			if (StringUtils.isBlank(charsetName)) {
				charset = StandardCharsets.UTF_8;
			} else {
				charset = Charset.forName(charsetName.trim());
			}
		} catch (IllegalCharsetNameException e) {
			charset = StandardCharsets.UTF_8;
		}

		return new String(requestBodyBytes, charset);
	}

	/**
	 * 获取JSON格式的请求体并解析为JsonElement
	 * <p>
	 * 读取请求体并将其解析为通用JSON对象。处理逻辑如下：
	 * <ul>
	 *     <li>验证请求Content-Type是否为JSON格式</li>
	 *     <li>非JSON请求则返回JsonNull实例</li>
	 *     <li>读取请求体并解析为JSON结构</li>
	 * </ul>
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 解析后的JSON对象，如请求体不是JSON则返回JsonNull
	 * @throws IOException              读取或解析请求内容失败时抛出
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static JsonElement getJsonRequestBody(final HttpServletRequest request) throws IOException {
		if (!isJsonRequestBody(request)) {
			return JsonNull.INSTANCE;
		}

		String requestBodyStr = getStringRequestBody(request);
		return JsonUtils.parseString(requestBodyStr);
	}

	/**
	 * 获取JSON格式的请求体并转换为指定类型
	 * <p>
	 * 读取请求体并将其反序列化为指定Java对象。处理逻辑如下：
	 * <ul>
	 *     <li>验证请求Content-Type是否为JSON格式</li>
	 *     <li>非JSON请求则直接返回null</li>
	 *     <li>解析请求体并转换为目标类型的对象实例</li>
	 * </ul>
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @param type    目标类型的Class对象
	 * @param <T>     返回对象的类型
	 * @return 转换后的类型实例，如非JSON请求则返回null
	 * @throws IOException              读取或解析请求内容失败时抛出
	 * @throws IllegalArgumentException 如果request或type为null
	 * @since 1.0.0
	 */
	public static <T> T getJsonRequestBody(final HttpServletRequest request, final Class<T> type) throws IOException {
		Assert.notNull(type, "type不可为null");
		if (!isJsonRequestBody(request)) {
			return null;
		}

		String requestBodyStr = getStringRequestBody(request);
		return JsonUtils.fromString(requestBodyStr, type);
	}

	/**
	 * 获取JSON格式的请求体并转换为泛型类型
	 * <p>
	 * 读取请求体并将其反序列化为指定的泛型类型。处理逻辑如下：
	 * <ul>
	 *     <li>验证请求Content-Type是否为JSON格式</li>
	 *     <li>非JSON请求则直接返回null</li>
	 *     <li>解析请求体并转换为目标泛型类型（如List、Map或其他复杂类型）</li>
	 * </ul>
	 * </p>
	 *
	 * @param request   HTTP请求对象，不能为null
	 * @param typeToken 目标泛型类型的TypeToken实例
	 * @param <T>       返回对象的类型
	 * @return 转换后的泛型类型实例，如非JSON请求则返回null
	 * @throws IOException              读取或解析请求内容失败时抛出
	 * @throws IllegalArgumentException 如果request或typeToken为null
	 * @since 1.0.0
	 */
	public static <T> T getJsonRequestBody(final HttpServletRequest request, final TypeToken<T> typeToken) throws IOException {
		Assert.notNull(typeToken, "typeToken不可为null");
		if (!isJsonRequestBody(request)) {
			return null;
		}

		String requestBodyStr = getStringRequestBody(request);
		return JsonUtils.fromString(requestBodyStr, typeToken);
	}

	/**
	 * 判断请求是否为JSON类型
	 * <p>
	 * 通过检查Content-Type头部值，确定请求是否为JSON格式。
	 * 此方法支持带字符集参数的JSON类型，如"application/json;charset=UTF-8"。
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 如果Content-Type以"application/json"开头则返回true，否则返回false
	 * @throws IllegalArgumentException 当request为null时抛出
	 * @since 1.0.0
	 */
	public static boolean isJsonRequestBody(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

		return StringUtils.startsWith(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}
}
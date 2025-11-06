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

package io.github.pangju666.framework.web.servlet.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.lang.WebConstants;
import io.github.pangju666.framework.web.utils.IpUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
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
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

/**
 * HTTP请求工具类
 * <p>
 * 提供对HTTP请求的全面处理功能，简化Web应用中常见的请求处理任务。本工具类主要功能包括：
 * <ul>
 *     <li>客户端信息识别：判断请求来源（移动设备、Ajax等）</li>
 *     <li>IP地址获取：从各种代理头中提取真实客户端IP</li>
 *     <li>请求参数和头信息处理：获取并转换为标准数据结构</li>
 *     <li>文件上传处理：获取multipart请求中的文件部分</li>
 *     <li>请求体内容处理：支持获取原始、字符串和JSON格式的请求体</li>
 * </ul>
 * </p>
 *
 * <p>
 * 设计特点：
 * <ul>
 *     <li>方法参数进行严格的非空校验，确保运行安全性</li>
 *     <li>兼容多种请求场景，如移动终端、浏览器、Ajax等</li>
 *     <li>提供多种JSON请求解析方式，支持通用和特定类型转换</li>
 *     <li>对文件上传和复杂请求结构提供友好处理</li>
 *     <li>使用Spring和Jakarta Servlet API，与主流框架集成良好</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 1. 判断请求来源
 * if (RequestUtils.isFormMobile(request)) {
 *     // 处理移动设备请求
 * }
 *
 * if (RequestUtils.isFromAjax(request)) {
 *     // 处理Ajax请求
 * }
 *
 * // 2. 获取客户端IP地址
 * String clientIp = RequestUtils.getIpAddress(request);
 *
 * // 3. 获取请求参数和头信息
 * MultiValueMap<String, String> params = RequestUtils.getParameters(request);
 * HttpHeaders headers = RequestUtils.getHeaders(request);
 *
 * // 4. 处理文件上传
 * Map<String, Part> fileParts = RequestUtils.getParts(request);
 * for (Part part : fileParts.values()) {
 *     String filename = part.getSubmittedFileName();
 *     // 处理文件
 * }
 *
 * // 5. 获取和处理JSON请求体
 * if (RequestUtils.isJsonRequestBody(request)) {
 *     // 获取为通用JsonElement
 *     JsonElement jsonElement = RequestUtils.getJsonRequestBody(request);
 *
 *     // 转换为特定类型
 *     User user = RequestUtils.getJsonRequestBody(request, User.class);
 *
 *     // 转换为泛型集合
 *     List<User> users = RequestUtils.getJsonRequestBody(request,
 *         new TypeToken<List<User>>(){});
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 * @see jakarta.servlet.http.HttpServletRequest
 * @see org.springframework.util.MultiValueMap
 * @see org.springframework.http.HttpHeaders
 * @see com.google.gson.JsonElement
 * @see org.springframework.web.bind.ServletRequestUtils
 */
public class HttpRequestUtils extends org.springframework.web.bind.ServletRequestUtils {
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

	protected HttpRequestUtils() {
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
	 * <p>代码修改自<a href="https://github.com/yangzongzhuan/RuoYi/blob/master/ruoyi-common/src/main/java/com/ruoyi/common/utils/ServletUtils.java">RuoYi Common ServletUtils</a></p>
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
			if (!Strings.CI.contains(userAgent, "Windows NT") ||
				(Strings.CI.contains(userAgent, "Windows NT") &&
					Strings.CI.contains(userAgent, "compatible; MSIE 9.0;"))) {
				// 排除 苹果桌面系统
				if (!Strings.CI.contains(userAgent, "Windows NT") &&
					!Strings.CI.contains(userAgent, "Macintosh")) {
					return Strings.CI.containsAny(userAgent, MOBILE_AGENTS);
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
	 * <p>代码修改自<a href="https://github.com/yangzongzhuan/RuoYi/blob/master/ruoyi-common/src/main/java/com/ruoyi/common/utils/ServletUtils.java">RuoYi Common ServletUtils</a></p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 如果是Ajax请求则返回true，否则返回false
	 * @throws IllegalArgumentException 如果request参数为null
	 * @since 1.0.0
	 */
	public static boolean isFromAjax(final HttpServletRequest request) {
		Assert.notNull(request, "request 不可为null");

		String accept = request.getHeader(HttpHeaders.ACCEPT);
		if (Strings.CS.contains(accept, MediaType.APPLICATION_JSON_VALUE)) {
			return true;
		}

		String xRequestedWith = request.getHeader("X-Requested-With");
		if (Strings.CS.contains(xRequestedWith, "XMLHttpRequest")) {
			return true;
		}

		String uri = request.getRequestURI();
		if (Strings.CI.containsAny(uri, ".json", ".xml")) {
			return true;
		}

		String ajax = request.getParameter("__ajax");
		return Strings.CI.containsAny(ajax, "json", "xml");
	}

	/**
	 * 获取请求的IP地址
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
	 * <p>代码修改自<a href="https://github.com/yangzongzhuan/RuoYi/blob/master/ruoyi-common/src/main/java/com/ruoyi/common/utils/ServletUtils.java">RuoYi Common ServletUtils</a></p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 客户端请求IP地址
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

		return Strings.CS.equalsAny(ip, WebConstants.LOCALHOST_IPV4_ADDRESS,
			WebConstants.LOCALHOST_IPV6_ADDRESS) ? WebConstants.LOCALHOST_IPV4_ADDRESS :
			IpUtils.getMultistageReverseProxyIp(ip);
	}

	/**
	 * 获取所有的请求参数
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
	public static MultiValueMap<String, String> getParameters(final HttpServletRequest request) {
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
	 * 获取所有的请求头信息
	 * <p>
	 * 将所有请求头信息转换为Spring的`HttpHeaders`结构。
	 * 对于单值头部直接存储；对于多值头部保留为列表。
	 * 返回的HttpHeaders是只读的。
	 * </p>
	 *
	 * @param request HTTP请求对象，不能为null
	 * @return 包含所有HTTP头信息的只读HttpHeaders对象
	 * @throws IllegalArgumentException 当request为null时抛出
	 * @since 1.0.0
	 */
	public static HttpHeaders getHeaders(final HttpServletRequest request) {
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
	 * 获取请求参数中的文件信息
	 * <p>
	 * 从form-data请求参数中提取所有包含文件的部分。该方法只返回那些具有提交文件名
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
	public static Map<String, Part> getParts(final HttpServletRequest request) throws ServletException, IOException {
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
	 * 获取原始请求体的字节数组
	 * <p>
	 * 读取请求体的优先级与行为如下：
	 * <ul>
	 *   <li>校验 {@code request} 非空；为空时抛出 {@link IllegalArgumentException}</li>
	 *   <li>若请求已被 {@link ContentCachingRequestWrapper} 包装，优先返回其缓存字节；
	 *   缓存为 {@code null} 时返回空数组</li>
	 *   <li>否则直接从 {@link HttpServletRequest#getInputStream()} 读取全部字节并返回</li>
	 * </ul>
	 * 注意：直接读取输入流会消费请求体，如需后续重复读取，请在过滤器中预先使用
	 * {@link ContentCachingRequestWrapper} 对请求进行包装以缓存内容。
	 * </p>
	 *
	 * @param request HTTP 请求对象，不能为空
	 * @return 原始请求体字节数组；未缓存或空内容时返回空数组
	 * @throws IOException IO 读取失败时抛出
	 * @throws IllegalArgumentException 当请求对象为空时抛出
	 * @since 1.0.0
	 */
	public static byte[] getRawRequestBody(final HttpServletRequest request) throws IOException {
		Assert.notNull(request, "request 不可为null");

		if (request instanceof ContentCachingRequestWrapper requestWrapper) {
			return ArrayUtils.nullToEmpty(requestWrapper.getContentAsByteArray());
		}
		try (InputStream inputStream = request.getInputStream()) {
			return inputStream.readAllBytes();
		}
	}

	/**
	 * 获取请求体并按字符集解码为字符串
	 * <p>
	 * 优先使用 {@link HttpServletRequest#getCharacterEncoding()} 指定的字符集；
	 * 当未设置、名称为空或非法/不受支持时回退为 {@link StandardCharsets#UTF_8}。
	 * 原始字节通过 {@link #getRawRequestBody(HttpServletRequest)} 读取。
	 * </p>
	 *
	 * @param request HTTP 请求对象
	 * @return 请求体的字符串表示；若请求体为空，返回空字符串
	 * @throws IOException 读取请求体失败时抛出
	 * @since 1.0.0
	 */
	public static String getStringRequestBody(final HttpServletRequest request) throws IOException {
		byte[] requestBodyBytes = getRawRequestBody(request);

		Charset charset = StandardCharsets.UTF_8;
		try {
			String characterEncoding = request.getCharacterEncoding();
			if (StringUtils.isNotBlank(characterEncoding)) {
				charset = Charset.forName(characterEncoding);
			}
		} catch (UnsupportedCharsetException | IllegalCharsetNameException ignored) {
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
	 * 判断请求体是否为JSON类型
	 * <p>
	 * 通过检查Content-Type头部值，确定请求体是否为JSON格式。
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

		return Strings.CS.startsWith(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}
}
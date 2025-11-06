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

package io.github.pangju666.framework.web.lang;

/**
 * Web模块常量池
 *
 * @author pangju666
 * @since 1.0.0
 */
public class WebConstants {
	/**
	 * http协议前缀
	 *
	 * @since 1.0.0
	 */
	public static final String HTTP_PREFIX = "http://";
	/**
	 * https协议前缀
	 *
	 * @since 1.0.0
	 */
	public static final String HTTPS_PREFIX = "https://";
	/**
	 * http路径分隔符
	 *
	 * @since 1.0.0
	 */
	public static final String HTTP_PATH_SEPARATOR = "/";

	/**
	 * Ant 风格的全局路径匹配模式
	 * <p>
	 * 表示匹配所有路径及其子路径的 Ant 风格通配符（{@code /**}）。
	 * 适用于 Spring MVC 拦截器与 Handler 的路径配置，例如
	 * {@code InterceptorRegistry#addInterceptor(...).addPathPatterns(WebConstants.ANT_ANY_PATH_PATTERN)}。
	 * 与 {@link #FILTER_ANY_URL_PATTERN}（Servlet 过滤器 URL 模式 {@code /*}）不同，
	 * 本常量用于 MVC 层的路径匹配，不用于 Servlet 过滤器。
	 * </p>
	 *
	 * @see org.springframework.web.servlet.HandlerInterceptor
	 * @see org.springframework.web.servlet.config.annotation.InterceptorRegistry
	 * @see org.springframework.util.AntPathMatcher
	 * @since 1.0.0
	 */
	public static final String ANT_ANY_PATH_PATTERN = "/**";
	/**
	 * Servlet 过滤器的全局 URL 匹配模式
	 * <p>
	 * 表示拦截应用中的所有请求路径。适用于 {@code web.xml} 的
	 * {@code <url-pattern>} 或 Spring Boot 的
	 * {@code FilterRegistrationBean#addUrlPatterns(..)} 等过滤器注册场景。
	 * 与 {@link #ANT_ANY_PATH_PATTERN}（Ant 风格 {@code /**}，用于拦截器/Handler）不同，
	 * 本常量用于 Servlet 过滤器的 URL 模式。
	 * </p>
	 *
	 * @see jakarta.servlet.Filter
	 * @since 1.0.0
	 */
	public static final String FILTER_ANY_URL_PATTERN = "/*";

	/**
	 * Token前缀
	 *
	 * @since 1.0.0
	 */
	public static final String TOKEN_PREFIX = "Bearer ";
	/**
	 * 管理员角色标识
	 * <p>
	 * 用于标识具有最高权限的管理员角色
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String ADMIN_ROLE = "admin";

	/**
	 * 操作成功状态码
	 * <p>
	 * 表示业务操作执行成功的标准状态码
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final int SUCCESS_CODE = 0;
	/**
	 * 基础错误状态码
	 * <p>
	 * 表示发生通用错误时的基础状态码
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final int BASE_ERROR_CODE = -1;
	/**
	 * 默认成功响应消息
	 * <p>
	 * 当未指定具体成功消息时使用的标准提示文本
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";
	/**
	 * 默认失败响应消息
	 * <p>
	 * 当未指定具体错误消息时使用的标准提示文本
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String DEFAULT_FAILURE_MESSAGE = "请求失败";

	/**
	 * 本地Ipv4地址
	 *
	 * @since 1.0.0
	 */
	public static final String LOCALHOST_IPV4_ADDRESS = "127.0.0.1";
	/**
	 * 本地Ipv6地址
	 *
	 * @since 1.0.0
	 */
	public static final String LOCALHOST_IPV6_ADDRESS = "0:0:0:0:0:0:0:1";
	/**
	 * 本地主机名常量
	 * <p>
	 * 表示本地回环地址的标准主机名，等同于使用IP地址 {@link #LOCALHOST_IPV4_ADDRESS}。
	 * 在网络编程中，通常可以互换使用"localhost"和"127.0.0.1"。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String LOCAL_HOST_NAME = "localhost";
	/**
	 * 未知地址标识
	 *
	 * @since 1.0.0
	 */
	public static final String UNKNOWN_ADDRESS = "unknown";

	protected WebConstants() {
	}
}

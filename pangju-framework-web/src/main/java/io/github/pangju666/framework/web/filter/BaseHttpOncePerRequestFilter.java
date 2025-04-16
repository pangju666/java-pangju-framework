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

package io.github.pangju666.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * HTTP请求过滤器基类
 * <p>
 * 提供基础的请求过滤功能：
 * <ul>
 *     <li>支持排除路径配置</li>
 *     <li>基于Ant风格的路径匹配</li>
 *     <li>确保每个请求只处理一次</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see OncePerRequestFilter
 * @since 1.0.0
 */
public abstract class BaseHttpOncePerRequestFilter extends OncePerRequestFilter {
	/**
	 * 路径匹配器
	 *
	 * @since 1.0.0
	 */
	private final PathMatcher pathMatcher;
	/**
	 * 排除路径模式集合
	 *
	 * @since 1.0.0
	 */
	private final Set<String> excludePathPatterns;

	/**
	 * 创建过滤器实例（无排除路径）
	 *
	 * @since 1.0.0
	 */
	protected BaseHttpOncePerRequestFilter() {
		this(Collections.emptySet());
	}

	/**
	 * 创建过滤器实例（指定排除路径）
	 *
	 * @param excludePathPatterns 排除路径模式集合
	 * @since 1.0.0
	 */
	protected BaseHttpOncePerRequestFilter(Set<String> excludePathPatterns) {
		this.pathMatcher = new AntPathMatcher();
		this.excludePathPatterns = Objects.isNull(excludePathPatterns) ? Collections.emptySet() :
			Collections.unmodifiableSet(excludePathPatterns);
	}

	/**
	 * 执行过滤器内部处理逻辑
	 * <p>
	 * 处理流程：
	 * <ol>
	 *     <li>检查是否配置了排除路径</li>
	 *     <li>如果未配置排除路径，直接执行处理逻辑</li>
	 *     <li>如果配置了排除路径，检查当前请求路径是否匹配排除规则</li>
	 *     <li>匹配排除规则则跳过处理，否则执行处理逻辑</li>
	 * </ol>
	 * </p>
	 *
	 * @param request     HTTP请求对象
	 * @param response    HTTP响应对象
	 * @param filterChain 过滤器链
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 * @since 1.0.0
	 */
	@Override
	protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
										  FilterChain filterChain) throws ServletException, IOException {
		if (excludePathPatterns.isEmpty()) {
			handle(request, response, filterChain);
			return;
		}

		String servletPath = request.getServletPath();
		boolean shouldExclude = excludePathPatterns.stream()
			.anyMatch(pattern -> pathMatcher.match(pattern, servletPath));
		if (shouldExclude) {
			filterChain.doFilter(request, response);
		} else {
			handle(request, response, filterChain);
		}
	}

	/**
	 * 处理HTTP请求
	 * <p>
	 * 由子类实现具体的请求处理逻辑
	 * </p>
	 *
	 * @param request     HTTP请求对象
	 * @param response    HTTP响应对象
	 * @param filterChain 过滤器链
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 * @since 1.0.0
	 */
	protected abstract void handle(HttpServletRequest request, HttpServletResponse response,
								   FilterChain filterChain) throws ServletException, IOException;
}
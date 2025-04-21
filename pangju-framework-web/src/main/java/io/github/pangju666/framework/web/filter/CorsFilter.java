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
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 跨域资源共享过滤器
 * <p>
 * 扩展Spring的CorsFilter，增加了路径排除功能：
 * <ul>
 *     <li>支持配置排除路径，匹配的请求将跳过CORS处理</li>
 *     <li>使用Ant风格的路径匹配规则</li>
 *     <li>继承了Spring CORS过滤器的所有功能</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see org.springframework.web.filter.CorsFilter
 * @since 1.0.0
 */
public class CorsFilter extends org.springframework.web.filter.CorsFilter {
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
	 * @param configSource CORS配置源
	 * @since 1.0.0
	 */
	public CorsFilter(CorsConfigurationSource configSource) {
		this(configSource, Collections.emptySet());
	}

	/**
	 * 创建过滤器实例（指定排除路径）
	 *
	 * @param configSource        CORS配置源
	 * @param excludePathPatterns 排除路径模式集合，匹配的请求将跳过CORS处理
	 * @since 1.0.0
	 */
	public CorsFilter(CorsConfigurationSource configSource, Set<String> excludePathPatterns) {
		super(configSource);
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
	 *     <li>如果未配置排除路径，执行标准CORS处理</li>
	 *     <li>如果配置了排除路径，检查当前请求是否匹配排除规则</li>
	 *     <li>匹配排除规则则跳过CORS处理，否则执行标准CORS处理</li>
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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		if (excludePathPatterns.isEmpty()) {
			super.doFilterInternal(request, response, filterChain);
			return;
		}

		String servletPath = request.getServletPath();
		boolean shouldExclude = excludePathPatterns.stream()
			.anyMatch(pattern -> pathMatcher.match(pattern, servletPath));
		if (shouldExclude) {
			filterChain.doFilter(request, response);
		} else {
			super.doFilterInternal(request, response, filterChain);
		}
	}
}
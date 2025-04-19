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

package io.github.pangju666.framework.web.interceptor;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 请求拦截器基类
 * <p>
 * 提供基础的请求拦截功能：
 * <ul>
 *     <li>支持配置拦截器执行顺序</li>
 *     <li>支持配置拦截路径模式</li>
 *     <li>支持配置排除路径模式</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see HandlerInterceptor
 * @see Ordered
 * @since 1.0.0
 */
public abstract class BaseHttpHandlerInterceptor implements HandlerInterceptor {
	/**
	 * 排除路径模式列表
	 * <p>匹配这些路径的请求将被跳过拦截</p>
	 *
	 * @since 1.0.0
	 */
	protected final List<String> excludePathPatterns;
	/**
	 * 拦截路径模式列表
	 * <p>只有匹配这些路径的请求才会被拦截</p>
	 *
	 * @since 1.0.0
	 */
	protected final List<String> patterns;
	/**
	 * 拦截器执行顺序
	 * <p>默认使用最低优先级</p>
	 *
	 * @since 1.0.0
	 */
	protected int order = Ordered.LOWEST_PRECEDENCE;

	/**
	 * 创建拦截器实例
	 *
	 * @param order               拦截器执行顺序（数值越小优先级越高）
	 * @param excludePathPatterns 排除路径模式集合
	 * @param patterns            拦截路径模式集合
	 * @since 1.0.0
	 */
	protected BaseHttpHandlerInterceptor(int order, Set<String> excludePathPatterns, Set<String> patterns) {
		this.order = order;
		this.excludePathPatterns = Objects.isNull(excludePathPatterns) ? Collections.emptyList() :
			List.copyOf(excludePathPatterns);
		this.patterns = Objects.isNull(patterns) ? Collections.emptyList() :
			List.copyOf(patterns);
	}

	/**
	 * 获取拦截器执行顺序
	 *
	 * @return 执行顺序值（数值越小优先级越高）
	 * @since 1.0.0
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * 获取排除路径模式列表
	 *
	 * @return 排除路径模式列表
	 * @since 1.0.0
	 */
	public List<String> getExcludePathPatterns() {
		return excludePathPatterns;
	}

	/**
	 * 获取拦截路径模式列表
	 *
	 * @return 拦截路径模式列表
	 * @since 1.0.0
	 */
	public List<String> getPatterns() {
		return patterns;
	}
}
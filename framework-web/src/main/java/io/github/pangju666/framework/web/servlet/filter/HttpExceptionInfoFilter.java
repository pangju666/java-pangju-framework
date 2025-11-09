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

package io.github.pangju666.framework.web.servlet.filter;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.lang.WebConstants;
import io.github.pangju666.framework.web.model.vo.EnumVO;
import io.github.pangju666.framework.web.model.vo.HttpExceptionVO;
import io.github.pangju666.framework.web.servlet.HttpResponseBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * HTTP 异常信息过滤器
 * <p>
 * 提供两个端点用于获取系统中的异常信息：
 * <ul>
 *   <li>异常类型列表端点：返回所有已定义的异常类型（枚举）</li>
 *   <li>异常信息列表端点：返回所有使用 {@link HttpException} 注解标注的异常类信息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 注册与匹配：
 * <ul>
 *   <li>推荐在 {@code FilterRegistrationBean} 中直接注册构造参数 {@code typesRequestPath} 与 {@code listRequestPath}
 *       两个具体路径进行 URL 模式匹配（按 {@code servletPath} 精确匹配）。</li>
 * </ul>
 * </p>
 *
 * <p>示例（注册与使用）：</p>
 * <pre>{@code
 * @Bean
 * public FilterRegistrationBean<HttpExceptionInfoFilter> httpExceptionInfoFilter() {
 *     HttpExceptionInfoFilter filter = new HttpExceptionInfoFilter(
 *         "/http-exception/types",
 *         "/http-exception/list",
 *         List.of("com.example.app") // 可选扫描包
 *     );
 *     FilterRegistrationBean<HttpExceptionInfoFilter> registration = new FilterRegistrationBean<>(filter);
 *     // 建议直接注册构造参数对应的两个端点路径
 *     registration.addUrlPatterns("/http-exception/types", "/http-exception/list");
 *     registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
 *     return registration;
 * }
 * }</pre>
 *
 * <p>
 * 线程模型与性能：
 * <ul>
 *   <li>继承 {@link org.springframework.web.filter.OncePerRequestFilter}，保证每个请求仅执行一次。</li>
 *   <li>异常信息在构造时完成扫描与缓存；运行期命中端点直接返回缓存结果，避免重复扫描。</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see HttpException
 * @see BaseHttpException
 * @see io.github.pangju666.framework.web.lang.WebConstants#FILTER_ANY_URL_PATTERN
 * @since 1.0.0
 */
public class HttpExceptionInfoFilter extends OncePerRequestFilter {
	/**
	 * 框架内置异常包路径
	 * <p>
	 * 始终包含在异常扫描范围内，用于收集框架内置的异常定义。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final static String FRAMEWORK_EXCEPTION_PACKAGE = "io.github.pangju666.framework.web.exception";

	/**
	 * 异常列表请求路径
	 *
	 * @since 1.0.0
	 */
	private final String listRequestPath;
	/**
	 * 异常类型列表请求路径
	 *
	 * @since 1.0.0
	 */
	private final String typesRequestPath;
	/**
	 * 异常类型列表缓存
	 *
	 * @since 1.0.0
	 */
	private final List<EnumVO> httpExceptionTypeList;
	/**
	 * 异常信息列表缓存
	 *
	 * @since 1.0.0
	 */
	private final List<HttpExceptionVO> httpExceptionList;

	/**
	 * 创建过滤器实例
	 * <p>
	 * 初始化过滤器，设置异常信息访问的请求路径与需要扫描的包路径。
	 * 可自定义「异常类型列表」与「异常信息列表」两个端点的请求路径，
	 * 并通过 {@code packages} 限定反射扫描范围，以提升启动与运行期性能。
	 * </p>
	 *
	 * @param typesRequestPath 异常类型列表请求路径，不能为空
	 * @param listRequestPath  异常列表请求路径，不能为空
	 * @param packages         需要扫描的包路径集合；可为 {@code null} 或空集合，
	 *                         此时仅扫描框架内置包（{@link #FRAMEWORK_EXCEPTION_PACKAGE}）
	 * @throws IllegalArgumentException 当请求路径参数为空时抛出
	 * @since 1.0.0
	 */
	public HttpExceptionInfoFilter(String typesRequestPath, String listRequestPath, Collection<String> packages) {
		Assert.hasText(typesRequestPath, "typesRequestPath must not be null");
		Assert.hasText(listRequestPath, "listRequestPath must not be null");

		this.typesRequestPath = typesRequestPath;
		this.listRequestPath = listRequestPath;

		this.httpExceptionTypeList = Arrays.stream(HttpExceptionType.values())
			.map(type -> new EnumVO(type.getLabel(), type.name()))
			.toList();

		String[] packagesArray = new String[]{};
		if (packages != null && !packages.isEmpty()) {
			packagesArray = packages.stream()
				.filter(StringUtils::isNotBlank)
				.map(String::strip)
				.distinct()
				.toArray(String[]::new);
		}
		this.httpExceptionList = scanHttpExceptions(packagesArray);
	}

	/**
	 * 执行过滤器内部处理逻辑
	 * <p>
	 * 根据请求路径分发到不同的处理方法：
	 * <ul>
	 *     <li>{@link #typesRequestPath}: 返回异常类型列表</li>
	 *     <li>{@link #listRequestPath}: 返回异常信息列表</li>
	 *     <li>其他路径: 继续过滤器链处理</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 响应构建：使用 {@link io.github.pangju666.framework.web.servlet.HttpResponseBuilder} 输出 JSON。
	 * 类型列表端点禁用响应缓冲（{@code buffer(false)}) 以减少开销；异常信息端点直接写入缓存结果。
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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String servletPath = request.getServletPath();
		if (servletPath.equals(typesRequestPath)) {
			HttpResponseBuilder.from(response).writeBean(this.httpExceptionTypeList);
		} else if (servletPath.equals(listRequestPath)) {
			HttpResponseBuilder.from(response).buffer().writeBean(this.httpExceptionList);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * 扫描 HTTP 异常类
	 * <p>
	 * 扫描流程：
	 * <ol>
	 *     <li>配置扫描器，设置扫描范围为框架包和用户指定的包</li>
	 *     <li>获取所有 {@link BaseHttpException} 的子类</li>
	 *     <li>添加默认的未知异常类型</li>
	 *     <li>处理所有带有 {@link HttpException} 注解的异常类</li>
	 * </ol>
	 * </p>
	 *
	 * @param packages 额外扫描的包路径数组；可为空数组以仅扫描框架包
	 * @return 异常信息列表
	 * @see org.reflections.Reflections
	 * @see org.reflections.scanners.Scanners
	 * @see org.reflections.util.ConfigurationBuilder
	 * @since 1.0.0
	 */
	protected List<HttpExceptionVO> scanHttpExceptions(String[] packages) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
			.setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
			.forPackage(FRAMEWORK_EXCEPTION_PACKAGE);
		if (packages.length > 0) {
			configurationBuilder.forPackages(packages);
		}

		Reflections reflections = new Reflections(configurationBuilder);
		Set<Class<? extends BaseHttpException>> classes = reflections.getSubTypesOf(BaseHttpException.class);

		List<HttpExceptionVO> exceptionList = new ArrayList<>(classes.size() + 1);
		// 添加未知异常类型
		exceptionList.add(new HttpExceptionVO(HttpExceptionType.UNKNOWN.getLabel(),
			HttpExceptionType.UNKNOWN.name(), WebConstants.BASE_ERROR_CODE, null));

		// 扫描并添加所有标注了HttpException注解的异常类
		classes.stream()
			.map(clazz -> clazz.getAnnotation(HttpException.class))
			.filter(Objects::nonNull)
			.map(annotation -> new HttpExceptionVO(
				annotation.type().getLabel(),
				annotation.type().name(),
				annotation.type().computeCode(annotation.code()),
				annotation.description()
			))
			.forEach(exceptionList::add);

		return exceptionList;
	}
}
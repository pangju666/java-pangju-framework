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

package io.github.pangju666.framework.spring.utils;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Spring 表达式语言（SpEL）工具类
 *
 * <p>该工具类封装了 Spring 表达式语言（SpEL）的常用功能，简化表达式解析与计算逻辑。
 * 提供了以下核心功能：</p>
 *
 * <ul>
 *     <li>初始化方法级别的表达式计算上下文（支持参数名绑定）</li>
 *     <li>提供默认的表达式解析器 {@link SpelExpressionParser}</li>
 *     <li>提供可复用的表达式计算上下文 {@link StandardEvaluationContext}</li>
 * </ul>
 *
 * <h3>主要用途：</h3>
 * <ul>
 *     <li>在 Spring AOP 切面中根据方法参数动态计算表达式</li>
 *     <li>在配置文件、规则引擎、数据校验等场景中进行动态值解析</li>
 * </ul>
 *
 * <h3>线程安全说明：</h3>
 * <ul>
 *     <li>{@link #DEFAULT_EXPRESSION_PARSER} 是线程安全的，可在多线程间共享。</li>
 *     <li>{@link #DEFAULT_EVALUATION_CONTEXT} 为共享实例，若需要隔离上下文（例如不同请求间变量不同），
 *         应使用 {@link StandardEvaluationContext} 的新实例。</li>
 * </ul>
 *
 * @author pangju666
 * @see SpelExpressionParser
 * @see EvaluationContext
 * @see MethodBasedEvaluationContext
 * @since 1.0.0
 */
public class SpELUtils {
	/**
	 * 默认的 SpEL 表达式解析器
	 *
	 * <p>该解析器基于 {@link org.springframework.expression.spel.standard.SpelExpressionParser}，
	 * 用于解析 SpEL 表达式字符串，例如：
	 * <pre>{@code
	 * Expression expression = SpELUtils.DEFAULT_EXPRESSION_PARSER.parseExpression("#a + #b");
	 * }</pre>
	 *
	 * <p>线程安全，可在多线程场景下共享使用。</p>
	 *
	 * @since 1.0.0
	 */
	public static final SpelExpressionParser DEFAULT_EXPRESSION_PARSER = new SpelExpressionParser();

	/**
	 * 默认的表达式计算上下文
	 *
	 * <p>用于提供 SpEL 表达式执行时的变量、函数和根对象等上下文信息。</p>
	 *
	 * <h3>注意：</h3>
	 * <ul>
	 *     <li>该实例是共享的，修改其中变量会影响其他使用方。</li>
	 *     <li>如需线程隔离或动态变量，应通过 {@code new StandardEvaluationContext()} 创建新实例。</li>
	 * </ul>
	 *
	 * @since 1.0.0
	 */
	public static final StandardEvaluationContext DEFAULT_EVALUATION_CONTEXT = new StandardEvaluationContext();

	protected SpELUtils() {
	}

	/**
	 * 初始化基于方法的表达式计算上下文
	 * <p>
	 * 该方法创建一个基于方法的表达式计算上下文，并将方法参数作为变量添加到上下文中。
	 * 这对于在AOP等场景中获取方法参数并在表达式中使用非常有用。
	 * </p>
	 *
	 * <p>
	 * 示例:
	 * <pre>{@code
	 * Method method = MyClass.class.getMethod("myMethod", String.class, Integer.class);
	 * Object[] args = new Object[]{"test", 123};
	 * ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
	 * EvaluationContext context = SpELUtils.initEvaluationContext(method, args, discoverer);
	 * // 现在可以在表达式中使用方法参数，如 #paramName
	 * }</pre>
	 * </p>
	 *
	 * @param method     目标方法（不可为 {@code null}）
	 * @param args       方法参数值数组（不可为 {@code null}，可为空数组）
	 * @param discoverer 参数名称发现器，用于获取方法参数名（不可为 {@code null}）
	 * @return 初始化后的表达式计算上下文
	 * @throws IllegalArgumentException 当 {@code method}、{@code args} 或 {@code discoverer} 为 {@code null} 时抛出
	 * @throws NullPointerException     如果参数名称发现器无法获取参数名（返回 {@code null}）
	 * @since 1.0.0
	 */
	public static MethodBasedEvaluationContext initEvaluationContext(final Method method, final Object[] args,
																	 final ParameterNameDiscoverer discoverer) {
		Assert.notNull(method, "method 不可为null");
		Assert.notNull(args, "args 不可为null");
		Assert.notNull(discoverer, "discoverer 不可为null");

		MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(method, method, args, discoverer);
		String[] parametersName = discoverer.getParameterNames(method);
		for (int i = 0; i < args.length; i++) {
			context.setVariable(Objects.requireNonNull(parametersName)[i], args[i]);
		}
		return context;
	}
}

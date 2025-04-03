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

package io.github.pangju666.framework.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;

public class SpELUtils {
	public static final SpelExpressionParser DEFAULT_EXPRESSION_PARSER = new SpelExpressionParser();
	public static final StandardEvaluationContext DEFAULT_EVALUATION_CONTEXT = new StandardEvaluationContext();

	protected SpELUtils() {
	}

	public static EvaluationContext initEvaluationContext(final Method method, final Object[] args,
														  final ParameterNameDiscoverer discoverer) {
		EvaluationContext context = new MethodBasedEvaluationContext(method, method, args, discoverer);
		String[] parametersName = discoverer.getParameterNames(method);
		if (ArrayUtils.isNotEmpty(args)) {
			for (int i = 0; i < args.length; i++) {
				context.setVariable(Objects.requireNonNull(parametersName)[i], args[i]);
			}
		}
		return context;
	}

	public static Object parseExpression(final String expressionString) {
		Expression expression = DEFAULT_EXPRESSION_PARSER.parseExpression(expressionString);
		return expression.getValue(DEFAULT_EVALUATION_CONTEXT);
	}

	public static <T> T parseExpression(final String expressionString, final Class<T> desiredResultType) {
		Expression expression = DEFAULT_EXPRESSION_PARSER.parseExpression(expressionString);
		return expression.getValue(DEFAULT_EVALUATION_CONTEXT, desiredResultType);
	}
}

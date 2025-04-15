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

package io.github.pangju666.framework.web.exception.base;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.model.vo.HttpExceptionVO;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.core.NestedRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class BaseHttpException extends NestedRuntimeException {
	protected final String reason;

	protected BaseHttpException(String message, String reason) {
		super(message);
		this.reason = reason;
	}

	protected BaseHttpException(String message, String reason, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	/**
	 * 使用ERROR级别记录异常日志
	 * <p>
	 * 此方法会将异常信息和堆栈跟踪记录到指定的日志记录器中
	 * </p>
	 *
	 * @param logger 用于记录日志的SLF4J日志记录器
	 * @since 1.0.0
	 */
	public void log(Logger logger) {
		logger.error(this.reason, this);
	}

	/**
	 * 使用指定级别记录异常日志
	 * <p>
	 * 此方法允许指定日志级别，适用于不同严重程度的异常情况
	 * </p>
	 *
	 * @param logger 用于记录日志的SLF4J日志记录器
	 * @param level 日志级别，如{@link Level#ERROR}、{@link Level#WARN}等
	 * @since 1.0.0
	 */
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log(this.reason);
	}

	public static List<HttpExceptionVO> getHttpExceptionInfos(String... packages) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
			.setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
			.forPackage("io.github.pangju666.framework")
			.forPackages(packages);
		Reflections reflections = new Reflections(configurationBuilder);

		Set<Class<? extends BaseHttpException>> classes = reflections.getSubTypesOf(BaseHttpException.class);
		List<HttpExceptionVO> httpExceptionList = new ArrayList<>(classes.size());
		for (Class<?> clazz : classes) {
			HttpException annotation = clazz.getAnnotation(HttpException.class);
			if (Objects.nonNull(annotation)) {
				httpExceptionList.add(new HttpExceptionVO(annotation.type(), annotation.code(), annotation.description()));
			}
		}
		return httpExceptionList;
	}
}
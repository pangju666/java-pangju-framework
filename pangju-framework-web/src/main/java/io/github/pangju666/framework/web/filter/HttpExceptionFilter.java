package io.github.pangju666.framework.web.filter;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.model.vo.EnumVO;
import io.github.pangju666.framework.web.model.vo.HttpExceptionVO;
import io.github.pangju666.framework.web.utils.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

public class HttpExceptionFilter extends OncePerRequestFilter {
	private final static String FRAMEWORK_PACKAGE = "io.github.pangju666.framework";

	private final String listRequestPath;
	private final String typesRequestPath;
	private final String[] packages;
	private List<EnumVO> httpExceptionTypeList;
	private List<HttpExceptionVO> httpExceptionList;

	public HttpExceptionFilter(String... packages) {
		this.listRequestPath = "/exception/list";
		this.typesRequestPath = "/exception/types";
		this.packages = packages;
	}

	public HttpExceptionFilter(String typesRequestPath, String listRequestPath, String... packages) {
		this.typesRequestPath = typesRequestPath;
		this.listRequestPath = listRequestPath;
		this.packages = packages;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getServletPath().equals(typesRequestPath)) {
			if (Objects.isNull(httpExceptionTypeList)) {
				synchronized (this) {
					if (Objects.isNull(httpExceptionList)) {
						this.httpExceptionTypeList = Arrays.stream(HttpExceptionType.values())
							.map(type -> new EnumVO(type.getDescription(), type.name()))
							.toList();
					}
				}
			}

			ResponseUtils.writeBeanToResponse(this.httpExceptionList, response);
		} else if (request.getServletPath().equals(listRequestPath)) {
			if (Objects.isNull(this.httpExceptionList)) {
				synchronized (this) {
					if (Objects.isNull(httpExceptionList)) {
						ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
							.setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
							.forPackage(FRAMEWORK_PACKAGE)
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
						this.httpExceptionList = httpExceptionList;
					}
				}
			}

			ResponseUtils.writeBeanToResponse(this.httpExceptionList, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}
}
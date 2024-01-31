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
import java.util.Set;

public abstract class BaseRequestFilter extends OncePerRequestFilter {
	private final PathMatcher pathMatcher;
	private final Set<String> excludePathPatterns;

	protected BaseRequestFilter() {
		this(Collections.emptySet());
	}

	protected BaseRequestFilter(Set<String> excludePathPatterns) {
		this.pathMatcher = new AntPathMatcher();
		this.excludePathPatterns = excludePathPatterns;
	}

	@Override
	protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (excludePathPatterns.isEmpty()) {
			handle(request, response, filterChain);
			return;
		}
		for (String excludePathPattern : excludePathPatterns) {
			if (pathMatcher.matchStart(excludePathPattern, request.getServletPath())) {
				filterChain.doFilter(request, response);
				return;
			}
		}
		handle(request, response, filterChain);
	}

	protected abstract void handle(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;
}

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
import java.util.Set;

public class CorsFilter extends org.springframework.web.filter.CorsFilter {
	private final PathMatcher pathMatcher;
	private final Set<String> excludePathPatterns;

	public CorsFilter(CorsConfigurationSource configSource) {
		this(configSource, Collections.emptySet());
	}

	public CorsFilter(CorsConfigurationSource configSource, Set<String> excludePathPatterns) {
		super(configSource);
		this.pathMatcher = new AntPathMatcher();
		this.excludePathPatterns = excludePathPatterns;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (excludePathPatterns.isEmpty()) {
			super.doFilterInternal(request, response, filterChain);
			return;
		}
		for (String excludePathPattern : excludePathPatterns) {
			if (pathMatcher.matchStart(excludePathPattern, request.getServletPath())) {
				filterChain.doFilter(request, response);
				return;
			}
		}
		super.doFilterInternal(request, response, filterChain);
	}
}

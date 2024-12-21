package io.github.pangju666.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;

public class ContentCachingWrapperFilter extends BaseRequestFilter {
	public ContentCachingWrapperFilter(Set<String> excludePathPatterns) {
		super(excludePathPatterns);
	}

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response,
						  FilterChain filterChain) throws ServletException, IOException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
		filterChain.doFilter(requestWrapper, responseWrapper);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType(responseWrapper.getContentType());
			responseWrapper.getContentInputStream().transferTo(outputStream);
		}
	}
}
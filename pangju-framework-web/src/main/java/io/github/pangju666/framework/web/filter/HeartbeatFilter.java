package io.github.pangju666.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class HeartbeatFilter extends BaseRequestFilter {
	public HeartbeatFilter(Set<String> excludePathPatterns) {
		super(excludePathPatterns);
	}

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();
	}
}

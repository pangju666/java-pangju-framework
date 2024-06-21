package io.github.pangju666.framework.web.interceptor;


import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.List;

public abstract class BaseRequestInterceptor implements HandlerInterceptor {
    protected int order = Ordered.LOWEST_PRECEDENCE;
    protected List<String> excludePathPatterns = Collections.emptyList();
    protected List<String> patterns = Collections.singletonList("/**");

    protected BaseRequestInterceptor() {
    }

    protected BaseRequestInterceptor(int order, List<String> excludePathPatterns, List<String> patterns) {
        this.order = order;
        this.excludePathPatterns = excludePathPatterns;
        this.patterns = patterns;
    }

    public int getOrder() {
        return order;
    }

    public List<String> getExcludePathPatterns() {
        return excludePathPatterns;
    }

    public List<String> getPatterns() {
        return patterns;
    }
}

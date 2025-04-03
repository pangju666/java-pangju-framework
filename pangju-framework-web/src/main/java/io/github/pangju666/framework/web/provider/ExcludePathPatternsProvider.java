package io.github.pangju666.framework.web.provider;

import io.github.pangju666.commons.lang.utils.StringUtils;

import java.util.Collection;
import java.util.List;

public class ExcludePathPatternsProvider {
	private final List<String> excludePaths;

	public ExcludePathPatternsProvider(Collection<String> excludePaths) {
		this.excludePaths = StringUtils.getUniqueNotBlankElements(excludePaths);
	}

	public ExcludePathPatternsProvider(String... excludePaths) {
		this.excludePaths = StringUtils.getUniqueNotBlankElements(excludePaths);
	}

	public List<String> getExcludePaths() {
		return excludePaths;
	}
}
package io.github.pangju666.framework.web.provider;

import org.apache.commons.collections4.ListUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcludePathPatternProvider {
	private final List<String> excludePaths;
	private final Set<String> excludePathSet;

	public ExcludePathPatternProvider(List<String> excludePaths) {
		this.excludePaths = excludePaths;
		this.excludePathSet = new HashSet<>(ListUtils.emptyIfNull(excludePaths));
	}

	public List<String> getExcludePaths() {
		return excludePaths;
	}

	public Set<String> getExcludePathSet() {
		return excludePathSet;
	}
}
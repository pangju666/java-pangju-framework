package io.github.pangju666.framework.web.provider;

import java.util.*;

public class ExcludePathPatternProvider {
	private final List<String> excludePaths;
	private final Set<String> excludePathSet;

	public ExcludePathPatternProvider(List<String> excludePaths) {
		if (Objects.isNull(excludePaths) || excludePaths.isEmpty()) {
			this.excludePaths = Collections.emptyList();
			this.excludePathSet = Collections.emptySet();
		} else {
			this.excludePaths = excludePaths;
			this.excludePathSet = new HashSet<>(excludePaths);
		}
	}

	public List<String> getExcludePaths() {
		return excludePaths;
	}

	public Set<String> getExcludePathSet() {
		return excludePathSet;
	}
}

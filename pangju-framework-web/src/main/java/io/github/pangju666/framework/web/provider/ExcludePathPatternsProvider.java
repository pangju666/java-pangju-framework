package io.github.pangju666.framework.web.provider;

import io.github.pangju666.commons.lang.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExcludePathPatternsProvider {
	private final Set<String> excludePaths;

	public ExcludePathPatternsProvider(Collection<String> excludePaths) {
		if (Objects.isNull(excludePaths) || excludePaths.isEmpty()) {
			this.excludePaths = Collections.emptySet();
		} else {
			this.excludePaths = excludePaths.stream()
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toUnmodifiableSet());
		}
	}

	public ExcludePathPatternsProvider(String... excludePaths) {
		if (Objects.isNull(excludePaths) || excludePaths.length == 0) {
			this.excludePaths = Collections.emptySet();
		} else {
			this.excludePaths = Arrays.stream(excludePaths)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toUnmodifiableSet());
		}
	}

	public Set<String> getExcludePaths() {
		return excludePaths;
	}
}
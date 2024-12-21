package io.github.pangju666.framework.data.mybatisplus.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AutoIDUtils {
	protected AutoIDUtils() {
	}

	public static List<Long> getIdList(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.toList();
	}

	public static Set<Long> getIdSet(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.collect(Collectors.toSet());
	}

	public static List<Long> getUniqueIdList(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.distinct()
			.toList();
	}
}
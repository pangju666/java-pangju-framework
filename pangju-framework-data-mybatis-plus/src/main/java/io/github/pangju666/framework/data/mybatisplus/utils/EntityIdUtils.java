package io.github.pangju666.framework.data.mybatisplus.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EntityIdUtils {
	protected EntityIdUtils() {
	}

	public static List<Long> getAutoIdList(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.toList();
	}

	public static Set<Long> getAutoIdSet(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.collect(Collectors.toSet());
	}

	public static List<Long> getUniqueAutoIdList(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.distinct()
			.toList();
	}

	public static List<String> getUUIdList(final Collection<String> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(StringUtils::isNotBlank)
			.toList();
	}

	public static Set<String> getUUIdSet(final Collection<String> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.toSet());
	}

	public static List<String> getUniqueUUIdList(final Collection<String> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(StringUtils::isNotBlank)
			.distinct()
			.toList();
	}

	public static List<Long> getSnowflakeIdList(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(Objects::nonNull)
			.toList();
	}

	public static Set<Long> getSnowflakeIdSet(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	public static List<Long> getUniqueSnowflakeIdList(final Collection<Long> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.filter(Objects::nonNull)
			.distinct()
			.toList();
	}
}
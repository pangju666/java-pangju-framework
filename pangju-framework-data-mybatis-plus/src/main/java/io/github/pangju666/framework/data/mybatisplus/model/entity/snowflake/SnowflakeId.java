package io.github.pangju666.framework.data.mybatisplus.model.entity.snowflake;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public interface SnowflakeId {
	static List<Long> getIdList(final Collection<? extends SnowflakeId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(SnowflakeId::getId)
			.filter(Objects::nonNull)
			.toList();
	}

	static Set<Long> getIdSet(final Collection<? extends SnowflakeId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(SnowflakeId::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	static List<Long> getUniqueIdList(final Collection<? extends SnowflakeId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(SnowflakeId::getId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();
	}

	Long getId();

	void setId(Long id);
}

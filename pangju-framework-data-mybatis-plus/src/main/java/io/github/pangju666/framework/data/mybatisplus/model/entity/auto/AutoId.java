package io.github.pangju666.framework.data.mybatisplus.model.entity.auto;

import io.github.pangju666.framework.data.mybatisplus.model.entity.base.Id;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public interface AutoId extends Id<Long> {
	static List<Long> getIdList(final Collection<? extends AutoId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(AutoId::getId)
			.filter(Objects::nonNull)
			.toList();
	}

	static Set<Long> getIdSet(final Collection<? extends AutoId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(AutoId::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	static List<Long> getUniqueIdList(final Collection<? extends AutoId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(AutoId::getId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();
	}
}

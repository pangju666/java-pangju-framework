package io.github.pangju666.framework.data.mybatisplus.model.entity.auto;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public interface AutoID {
	static List<Long> getIdList(final Collection<? extends AutoID> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(AutoID::getId)
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.toList();
	}

	static Set<Long> getIdSet(final Collection<? extends AutoID> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(AutoID::getId)
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.collect(Collectors.toSet());
	}

	static List<Long> getUniqueIdList(final Collection<? extends AutoID> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(AutoID::getId)
			.filter(id -> Objects.nonNull(id) && id >= 1)
			.distinct()
			.toList();
	}

	Long getId();

	void setId(Long id);
}

package io.github.pangju666.framework.data.mybatisplus.model.entity.uuid;

import io.github.pangju666.framework.data.mybatisplus.model.entity.base.Id;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface UUId extends Id<String> {
	static List<String> getIdList(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(StringUtils::isNotBlank)
			.toList();
	}

	static Set<String> getIdSet(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.toSet());
	}

	static List<String> getUniqueIdList(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(StringUtils::isNotBlank)
			.distinct()
			.toList();
	}
}

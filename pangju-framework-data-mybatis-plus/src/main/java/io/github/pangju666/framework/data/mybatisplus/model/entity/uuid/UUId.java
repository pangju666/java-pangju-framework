package io.github.pangju666.framework.data.mybatisplus.model.entity.uuid;

import io.github.pangju666.commons.lang.pool.RegExPool;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface UUId {
	Pattern PATTERN = RegExUtils.compile(RegExPool.UUID_SIMPLE, true, true);

	static List<String> getIdList(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(value -> StringUtils.isNotBlank(value) && PATTERN.matcher(value).matches())
			.toList();
	}

	static Set<String> getIdSet(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(value -> StringUtils.isNotBlank(value) && PATTERN.matcher(value).matches())
			.collect(Collectors.toSet());
	}

	static List<String> getUniqueIdList(final Collection<? extends UUId> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(UUId::getId)
			.filter(value -> StringUtils.isNotBlank(value) && PATTERN.matcher(value).matches())
			.distinct()
			.toList();
	}

	String getId();

	void setId(String id);
}

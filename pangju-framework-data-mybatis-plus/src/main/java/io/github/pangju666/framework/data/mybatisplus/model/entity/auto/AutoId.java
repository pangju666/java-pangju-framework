/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

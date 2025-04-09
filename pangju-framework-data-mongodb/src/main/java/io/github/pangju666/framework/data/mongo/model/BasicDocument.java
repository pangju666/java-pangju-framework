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

package io.github.pangju666.framework.data.mongo.model;

import io.github.pangju666.framework.data.mongo.pool.MongoConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BasicDocument implements Serializable {
	@MongoId(value = FieldType.STRING)
	@Field(name = MongoConstants.ID_FIELD_NAME)
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static String generateId() {
		return ObjectId.get().toHexString();
	}

	public static List<String> getIdList(final Collection<? extends BasicDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(BasicDocument::getId)
			.filter(StringUtils::isNotBlank)
			.toList();
	}

	public static Set<String> getIdSet(final Collection<? extends BasicDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}
		return collection.stream()
			.map(BasicDocument::getId)
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.toSet());
	}

	public static List<String> getUniqueIdList(final Collection<? extends BasicDocument> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}
		return collection.stream()
			.map(BasicDocument::getId)
			.filter(StringUtils::isNotBlank)
			.distinct()
			.toList();
	}
}
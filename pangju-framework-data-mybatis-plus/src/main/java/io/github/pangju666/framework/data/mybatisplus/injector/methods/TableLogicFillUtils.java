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

package io.github.pangju666.framework.data.mybatisplus.injector.methods;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import io.github.pangju666.framework.data.mybatisplus.annotation.TableLogicFill;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.stream.Collectors.joining;

final class TableLogicFillUtils {
	private TableLogicFillUtils() {
	}

	private static boolean isTableLogicFill(final Field field) {
		return field.getAnnotation(TableLogicFill.class) != null;
	}

	private static String getFillSql(final TableFieldInfo info) {
		TableLogicFill logicDelFill = info.getField().getAnnotation(TableLogicFill.class);
		return info.getColumn() + "=" + logicDelFill.value() + ",";
	}

	private static List<TableFieldInfo> getFillFieldInfoList(final TableInfo tableInfo) {
		return tableInfo.getFieldList()
			.stream()
			.filter(i -> TableLogicFillUtils.isTableLogicFill(i.getField()))
			.toList();
	}

	public static String logicDeleteSetSql(final TableInfo tableInfo) {
		List<TableFieldInfo> list = getFillFieldInfoList(tableInfo);
		String sqlSet = "";
		if (!CollectionUtils.isEmpty(list)) {
			sqlSet = list.stream()
				.map(TableLogicFillUtils::getFillSql)
				.collect(joining(""));
		}
		sqlSet += tableInfo.getLogicDeleteSql(false, false);
		return "SET " + sqlSet;
	}
}

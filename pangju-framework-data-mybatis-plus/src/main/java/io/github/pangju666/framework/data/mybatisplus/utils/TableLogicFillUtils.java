package io.github.pangju666.framework.data.mybatisplus.utils;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.TableLogicFill;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.stream.Collectors.joining;


public class TableLogicFillUtils {
	private TableLogicFillUtils() {
	}

	private static boolean isLogicDelFill(final Field field) {
		return field.getAnnotation(TableLogicFill.class) != null;
	}

	private static String getFillSql(final TableFieldInfo info) {
		TableLogicFill logicDelFill = info.getField().getAnnotation(TableLogicFill.class);
		return info.getColumn() + "=" + logicDelFill.value() + ",";
	}

	private static List<TableFieldInfo> getFillFieldInfoList(final TableInfo tableInfo) {
		return tableInfo.getFieldList()
			.stream()
			.filter(i -> TableLogicFillUtils.isLogicDelFill(i.getField()))
			.toList();
	}

	public static String logicDelSetSql(final TableInfo tableInfo) {
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

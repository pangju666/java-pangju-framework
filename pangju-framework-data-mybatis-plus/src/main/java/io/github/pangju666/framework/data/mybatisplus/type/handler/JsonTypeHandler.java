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

package io.github.pangju666.framework.data.mybatisplus.type.handler;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.google.gson.JsonSyntaxException;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * JSON类型处理器（基于{@link com.google.gson.Gson Gson}实现）
 * <p>
 * 用于在MyBatis中处理Java对象与JSON字符串之间的转换。
 * 支持将Java对象序列化为JSON字符串存储到数据库，以及将数据库中的JSON字符串反序列化为Java对象。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public final class JsonTypeHandler extends BaseTypeHandler<Object> {
	/**
	 * 目标类型
	 * <p>
	 * 用于指定JSON字符串反序列化的目标类型
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final Class<?> type;

	/**
	 * 构造函数
	 * <p>
	 * 创建一个指定目标类型的JSON类型处理器
	 * </p>
	 *
	 * @param type 目标类型，不能为null
	 * @throws IllegalArgumentException 如果type为null
	 * @since 1.0.0
	 */
	public JsonTypeHandler(Class<?> type) {
		Assert.notNull(type, "type 不能为null");
		this.type = type;
	}

	/**
	 * 设置非空参数
	 * <p>
	 * 将Java对象转换为JSON字符串并设置到PreparedStatement中
	 * </p>
	 *
	 * @param ps        预处理语句
	 * @param i         参数位置
	 * @param parameter 参数值
	 * @param jdbcType  JDBC类型
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, toJson(parameter));
	}

	/**
	 * 获取可为空的结果（通过列名）
	 * <p>
	 * 从ResultSet中获取指定列名的JSON字符串，并转换为Java对象
	 * </p>
	 *
	 * @param rs         结果集
	 * @param columnName 列名
	 * @return 转换后的Java对象，如果JSON为空则返回空集合或null
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		final String json = rs.getString(columnName);
		return StringUtils.isBlank(json) ? getNullValue() : parse(json);
	}

	/**
	 * 获取可为空的结果（通过列索引）
	 * <p>
	 * 从ResultSet中获取指定列索引的JSON字符串，并转换为Java对象
	 * </p>
	 *
	 * @param rs          结果集
	 * @param columnIndex 列索引
	 * @return 转换后的Java对象，如果JSON为空则返回空集合或null
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		final String json = rs.getString(columnIndex);
		return StringUtils.isBlank(json) ? getNullValue() : parse(json);
	}

	/**
	 * 获取可为空的结果（从存储过程）
	 * <p>
	 * 从CallableStatement中获取指定列索引的JSON字符串，并转换为Java对象
	 * </p>
	 *
	 * @param cs          可调用语句
	 * @param columnIndex 列索引
	 * @return 转换后的Java对象，如果JSON为空则返回空集合或null
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		final String json = cs.getString(columnIndex);
		return StringUtils.isBlank(json) ? getNullValue() : parse(json);
	}

	/**
	 * 解析JSON字符串
	 * <p>
	 * 将JSON字符串解析为指定类型的Java对象
	 * </p>
	 *
	 * @param json JSON字符串
	 * @return 解析后的Java对象
	 * @throws SQLException 如果解析过程中发生错误
	 * @since 1.0.0
	 */
	private Object parse(String json) throws SQLException {
		try {
			return JsonUtils.fromString(json, type);
		} catch (JsonSyntaxException e) {
			throw new SQLException("json字符串解析失败", e);
		}
	}

	/**
	 * 将对象转换为JSON字符串
	 * <p>
	 * 将Java对象序列化为JSON字符串
	 * </p>
	 *
	 * @param obj Java对象
	 * @return 序列化后的JSON字符串
	 * @throws SQLException 如果序列化过程中发生错误
	 * @since 1.0.0
	 */
	private String toJson(Object obj) throws SQLException {
		try {
			return JsonUtils.toString(obj);
		} catch (JsonSyntaxException e) {
			throw new SQLException("json字符串转换失败", e);
		}
	}

	/**
	 * 获取空值
	 * <p>
	 * 根据目标类型返回相应的空集合或null
	 * </p>
	 *
	 * @return 空集合或null
	 * @since 1.0.0
	 */
	private Object getNullValue() {
		if (type.isAssignableFrom(List.class)) {
			return Collections.emptyList();
		} else if (type.isAssignableFrom(Set.class)) {
			return Collections.emptySet();
		} else if (type.isAssignableFrom(Map.class)) {
			return Collections.emptyMap();
		} else if (type.isAssignableFrom(Enumeration.class)) {
			return Collections.emptyEnumeration();
		} else if (type.isAssignableFrom(Iterator.class)) {
			return Collections.emptyIterator();
		}
		return null;
	}
}
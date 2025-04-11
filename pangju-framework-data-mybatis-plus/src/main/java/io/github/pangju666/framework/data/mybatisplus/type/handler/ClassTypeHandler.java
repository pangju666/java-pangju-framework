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

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class类型处理器
 * <p>
 * 用于在MyBatis中处理Java Class对象与字符串之间的转换。
 * 支持将Class对象转换为类名字符串存储到数据库，以及将数据库中的类名字符串转换为Class对象。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public final class ClassTypeHandler extends BaseTypeHandler<Class<?>> {
	private static final Map<String, Class<?>> CLASS_NAME_MAP = new ConcurrentHashMap<>(10);

	/**
	 * 设置非空参数
	 * <p>
	 * 将Class对象转换为类名字符串并设置到PreparedStatement中
	 * </p>
	 *
	 * @param ps        预处理语句
	 * @param i         参数位置
	 * @param parameter Class对象
	 * @param jdbcType  JDBC类型
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Class<?> parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getTypeName());
	}

	/**
	 * 获取可为空的结果（通过列名）
	 * <p>
	 * 从ResultSet中获取指定列名的类名字符串，并转换为Class对象
	 * </p>
	 *
	 * @param rs         结果集
	 * @param columnName 列名
	 * @return 转换后的Class对象，如果类名为空则返回null
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public Class<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getClass(rs.getString(columnName));
	}

	/**
	 * 获取可为空的结果（通过列索引）
	 * <p>
	 * 从ResultSet中获取指定列索引的类名字符串，并转换为Class对象
	 * </p>
	 *
	 * @param rs          结果集
	 * @param columnIndex 列索引
	 * @return 转换后的Class对象，如果类名为空则返回null
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public Class<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getClass(rs.getString(columnIndex));
	}

	/**
	 * 获取可为空的结果（从存储过程）
	 * <p>
	 * 从CallableStatement中获取指定列索引的类名字符串，并转换为Class对象
	 * </p>
	 *
	 * @param cs          可调用语句
	 * @param columnIndex 列索引
	 * @return 转换后的Class对象，如果类名为空则返回null
	 * @throws SQLException 如果转换过程中发生错误
	 * @since 1.0.0
	 */
	@Override
	public Class<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getClass(cs.getString(columnIndex));
	}

	/**
	 * 将类名字符串转换为Class对象
	 * <p>
	 * 通过类名获取对应的Class对象
	 * </p>
	 *
	 * @param className 类名字符串
	 * @return Class对象，如果类名为空则返回null
	 * @throws SQLException 如果找不到对应的类
	 * @since 1.0.0
	 */
	private Class<?> getClass(String className) throws SQLException {
		if (StringUtils.isBlank(className)) {
			return null;
		}
		if (CLASS_NAME_MAP.containsKey(className)) {
			return CLASS_NAME_MAP.get(className);
		}

		try {
			Class<?> clz = Class.forName(className);
			CLASS_NAME_MAP.put(className, clz);
			return clz;
		} catch (ClassNotFoundException e) {
			CLASS_NAME_MAP.put(className, null);
			throw new SQLException("无法将值" + className + "转换为Class对象");
		}
	}
}
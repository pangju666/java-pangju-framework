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
import java.util.Objects;
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
	/**
	 * 类名到类型的缓存映射
	 *
	 * <p>缓存 Class 全限定名到 {@link Class} 的映射，用于减少重复的类型解析/加载开销。</p>
	 *
	 * <h3>线程安全</h3>
	 * <ul>
	 *   <li>使用 {@link java.util.concurrent.ConcurrentHashMap}，支持高并发读写。</li>
	 * </ul>
	 *
	 * <h3>注意</h3>
	 * <ul>
	 *   <li>当类名解析失败时，使用 {@link #NOT_FOUND} 作为哨兵值进行缓存，避免重复查找。</li>
	 * </ul>
	 */
	private static final Map<String, Class<?>> CLASS_NAME_MAP = new ConcurrentHashMap<>(16);

	/**
	 * 类查找失败的哨兵类型
	 *
	 * <p>用于在 {@link #CLASS_NAME_MAP} 中缓存「未命中」结果，避免对同一类名进行重复解析。</p>
	 *
	 * <h3>实现说明</h3>
	 * <ul>
	 *   <li>使用匿名对象的 {@link Class} 作为占位类型，确保与任何业务类型不相等。</li>
	 * </ul>
	 *
	 * <h3>使用约定</h3>
	 * <ul>
	 *   <li>仅作为内部标记使用，不应对外暴露。</li>
	 *   <li>判定哨兵请使用引用相等（`==`），避免与真实类型混淆。</li>
	 * </ul>
	 */
	private static final Class<?> NOT_FOUND = new Object() {
	}.getClass();

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
		Class<?> clz = CLASS_NAME_MAP.get(className);
		if (Objects.nonNull(clz)) {
			if (clz == NOT_FOUND) {
				throw new SQLException("类未找到: " + className);
			}
			return clz;
		}

		try {
			clz = Class.forName(className);
			Class<?> existing = CLASS_NAME_MAP.putIfAbsent(className, clz);
			return Objects.nonNull(existing) ? existing : clz;
		} catch (ClassNotFoundException e) {
			CLASS_NAME_MAP.putIfAbsent(className, NOT_FOUND);
			throw new SQLException("类未找到: " + className);
		}
	}
}
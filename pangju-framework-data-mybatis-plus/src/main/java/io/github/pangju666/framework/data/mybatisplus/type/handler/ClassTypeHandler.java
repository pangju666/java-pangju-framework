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

@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public final class ClassTypeHandler extends BaseTypeHandler<Class<?>> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Class<?> parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getTypeName());
	}

	@Override
	public Class<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getClass(rs.getString(columnName));
	}

	@Override
	public Class<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getClass(rs.getString(columnIndex));
	}

	@Override
	public Class<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getClass(cs.getString(columnIndex));
	}

	private Class<?> getClass(String className) throws SQLException {
		try {
			return StringUtils.isBlank(className) ? null : Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new SQLException("无法将值" + className + "转换为Class对象");
		}
	}
}

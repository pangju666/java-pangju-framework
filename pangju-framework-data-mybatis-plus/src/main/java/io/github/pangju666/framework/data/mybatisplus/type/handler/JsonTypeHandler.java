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

@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public final class JsonTypeHandler extends BaseTypeHandler<Object> {
	private final Class<?> type;

	public JsonTypeHandler(Class<?> type) {
		Assert.notNull(type, "type 不能为null");
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, toJson(parameter));
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		final String json = rs.getString(columnName);
		return StringUtils.isBlank(json) ? getNullValue() : parse(json);
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		final String json = rs.getString(columnIndex);
		return StringUtils.isBlank(json) ? getNullValue() : parse(json);
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		final String json = cs.getString(columnIndex);
		return StringUtils.isBlank(json) ? getNullValue() : parse(json);
	}

	private Object parse(String json) throws SQLException {
		try {
			return JsonUtils.fromString(json, type);
		} catch (JsonSyntaxException e) {
			throw new SQLException("json字符串解析失败", e);
		}
	}

	private String toJson(Object obj) throws SQLException {
		try {
			return JsonUtils.toString(obj);
		} catch (JsonSyntaxException e) {
			throw new SQLException("json字符串转换失败", e);
		}
	}

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
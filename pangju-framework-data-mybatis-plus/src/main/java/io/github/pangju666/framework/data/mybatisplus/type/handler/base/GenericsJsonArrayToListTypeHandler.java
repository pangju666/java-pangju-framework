package io.github.pangju666.framework.data.mybatisplus.type.handler.base;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pangju666.framework.core.exception.base.ServerException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class GenericsJsonArrayToListTypeHandler<T> extends BaseTypeHandler<List<T>> {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private final TypeReference<List<T>> typeReference;

	public GenericsJsonArrayToListTypeHandler(TypeReference<List<T>> typeReference) {
		Assert.notNull(typeReference, "TypeReference argument cannot be null");
		this.typeReference = typeReference;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, toJson(parameter));
	}

	@Override
	public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		final String json = rs.getString(columnName);
		return StringUtils.isBlank(json) ? Collections.emptyList() : parse(json);
	}

	@Override
	public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		final String json = rs.getString(columnIndex);
		return StringUtils.isBlank(json) ? Collections.emptyList() : parse(json);
	}

	@Override
	public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		final String json = cs.getString(columnIndex);
		return StringUtils.isBlank(json) ? Collections.emptyList() : parse(json);
	}

	private List<T> parse(String json) {
		try {
			return OBJECT_MAPPER.readValue(json, typeReference);
		} catch (IOException e) {
			throw new ServerException("json字符串解析失败", e);
		}
	}

	private String toJson(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new ServerException("json字符串转换失败", e);
		}
	}
}
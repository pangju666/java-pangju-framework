package io.github.pangju666.framework.data.mybatisplus.type.handler.base;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class GenericsVarcharToListTypeHandler<T> extends BaseTypeHandler<List<T>> {
	private final Function<String, T> mapper;
	private final String delimiter;

	public GenericsVarcharToListTypeHandler(Function<String, T> mapper) {
		this(",", mapper);
	}

	public GenericsVarcharToListTypeHandler(String delimiter, Function<String, T> mapper) {
		this.delimiter = delimiter;
		this.mapper = mapper;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
		List<String> values = parameter.stream()
			.map(element -> Objects.isNull(element) ? StringUtils.EMPTY : element.toString())
			.toList();
		ps.setString(i, String.join(delimiter, values));
	}

	@Override
	public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getListResult(rs.getString(columnName));
	}

	@Override
	public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getListResult(rs.getString(columnIndex));
	}

	@Override
	public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getListResult(cs.getString(columnIndex));
	}

	protected List<T> getListResult(String result) {
		if (StringUtils.isBlank(result)) {
			return Collections.emptyList();
		}
		return Arrays.stream(result.split(delimiter))
			.map(value -> mapper.apply(result))
			.toList();
	}
}
package io.github.pangju666.framework.data.mybatisplus.type.handler;

import org.apache.commons.lang3.BooleanUtils;
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

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class BooleanListTypeHandler extends BaseTypeHandler<List<Boolean>> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<Boolean> parameter, JdbcType jdbcType) throws SQLException {
		List<String> values = parameter.stream()
			.map(element -> Objects.isNull(element) ? "null" : element.toString())
			.toList();
		ps.setString(i, String.join(",", values));
	}

	@Override
	public List<Boolean> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getListResult(rs.getString(columnName));
	}

	@Override
	public List<Boolean> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getListResult(rs.getString(columnIndex));
	}

	@Override
	public List<Boolean> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getListResult(cs.getString(columnIndex));
	}

	protected List<Boolean> getListResult(String result) {
		if (StringUtils.isBlank(result)) {
			return Collections.emptyList();
		}
		return Arrays.stream(result.split(","))
			.map(value -> BooleanUtils.toBooleanObject(result, "true", "false", "null"))
			.toList();
	}
}

package io.github.pangju666.framework.data.mybatisplus.type.handler.geometric;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.geometric.PGpolygon;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PolygonTypeHandler extends BaseTypeHandler<PGpolygon> {
	public void setNonNullParameter(PreparedStatement ps, int i, PGpolygon parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, parameter);
	}

	public PGpolygon getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return (PGpolygon) rs.getObject(columnName);
	}

	public PGpolygon getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return (PGpolygon) rs.getObject(columnIndex);
	}

	public PGpolygon getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return (PGpolygon) cs.getObject(columnIndex);
	}
}

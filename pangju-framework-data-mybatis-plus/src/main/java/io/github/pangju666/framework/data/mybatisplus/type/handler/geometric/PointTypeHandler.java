package io.github.pangju666.framework.data.mybatisplus.type.handler.geometric;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.geometric.PGpoint;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointTypeHandler extends BaseTypeHandler<PGpoint> {
	public void setNonNullParameter(PreparedStatement ps, int i, PGpoint parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, parameter);
	}

	public PGpoint getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return (PGpoint) rs.getObject(columnName);
	}

	public PGpoint getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return (PGpoint) rs.getObject(columnIndex);
	}

	public PGpoint getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return (PGpoint) cs.getObject(columnIndex);
	}
}

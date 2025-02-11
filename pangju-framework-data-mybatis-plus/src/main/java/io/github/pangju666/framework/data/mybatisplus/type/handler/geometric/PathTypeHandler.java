package io.github.pangju666.framework.data.mybatisplus.type.handler.geometric;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.geometric.PGpath;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PathTypeHandler extends BaseTypeHandler<PGpath> {
	public void setNonNullParameter(PreparedStatement ps, int i, PGpath parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, parameter);
	}

	public PGpath getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return (PGpath) rs.getObject(columnName);
	}

	public PGpath getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return (PGpath) rs.getObject(columnIndex);
	}

	public PGpath getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return (PGpath) cs.getObject(columnIndex);
	}
}

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
public class ClassTypeHandler extends BaseTypeHandler<Class<?>> {
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

    protected Class<?> getClass(String className) throws SQLException {
        try {
            return StringUtils.isBlank(className) ? null : Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new SQLException("无法将值" + className + "转换为Class对象");
        }
    }
}

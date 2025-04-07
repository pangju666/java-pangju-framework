package io.github.pangju666.framework.data.mybatisplus.type.handler.list;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigInteger;
import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public final class BigIntegerVarcharToListTypeHandler extends GenericsVarcharToListTypeHandler<BigInteger> {
	public BigIntegerVarcharToListTypeHandler() {
		super((value) -> StringUtils.isBlank(value) ? null : new BigInteger(value));
	}
}

package io.github.pangju666.framework.data.mybatisplus.type.handler;

import io.github.pangju666.framework.data.mybatisplus.type.handler.base.GenericsVarcharListTypeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class BigDecimalVarcharListTypeHandler extends GenericsVarcharListTypeHandler<BigDecimal> {
	public BigDecimalVarcharListTypeHandler() {
		super((value) -> StringUtils.isBlank(value) ? null : new BigDecimal(value));
	}
}

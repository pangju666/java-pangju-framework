package io.github.pangju666.framework.data.mybatisplus.type.handler.list;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class BooleanVarcharToListTypeHandler extends GenericsVarcharToListTypeHandler<Boolean> {
	public BooleanVarcharToListTypeHandler() {
		super((value) -> BooleanUtils.toBooleanObject(value, "true", "false", "null"));
	}
}
